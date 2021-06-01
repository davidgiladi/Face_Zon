import copy
import threading
from firebase_admin import firestore
import face_recognition
import cv2
from datetime import datetime
import requests
import os
import time

import firebase_admin
from firebase_admin import credentials

cred = credentials.Certificate('serviceAccountKey.json')
firebase_admin.initialize_app(cred)

management_id = "75bNpBgVcjhWIuoXNWPcxGR8wrR2"


def get_the_name_by_id(uid):
    db = firestore.client()
    doc = db.collection("manger_id").document(management_id).collection("pupil").document(uid).get()
    return (doc.to_dict()["name"])


def upload(name):
    db = firestore.client()
    data = {"name": name, "year": int((datetime.now().strftime("%Y"))), "month": int((datetime.now().strftime("%m"))),
            "day": int((datetime.now().strftime("%d"))), "hour": int((datetime.now().strftime("%H"))),
            "minute": int((datetime.now().strftime("%M"))), "seconds": int((datetime.now().strftime("%S")))}
    db.collection("Entrance").document(management_id).collection("id_Entrance").add(data)




def update_list(ls):
    list_copy = []
    list_copy = copy.copy(ls)
    for f in ls:
        if (f["number"] < 0):
            f["number"] = f["number"] + 1
           
        else:
            f["number"] = f["number"] - 1
            print (f["number"])
            if (f["number"] == -1):
                print ("the number remove")
                list_copy.remove(f)
    return list_copy



def upload_images_to_python():
    db = firestore.client()
    docs = db.collection("manger_id").document(management_id).collection("pupil").get()
    for doc in docs:
        data = doc.to_dict()

        directory = doc.id
        parent_dir = "/home/pi/Desktop/py/face_project/known_faces"
        path = os.path.join(parent_dir, directory)
        try:
            os.makedirs(path, exist_ok=True)
        except OSError as error:
            print("Directory '%s' can not be created")
        list_url = data["url_image"]
        i = 0
        for im_url in list_url: 
            img_data = requests.get(im_url).content
            i = i + 1
            number = str(i)
            with open(path + '/' + number + ".jpeg", 'wb') as handler:
                handler.write(img_data)


def upload_new_images(known_face_encodings, known_face_names,name_by_id):
    db = firestore.client()
    docs = db.collection("manger_id").document(management_id).collection("pupil").where("new_data", "==", True).get()
    for doc in docs:
        name_by_id[doc.id] = get_the_name_by_id(doc.id)
        db.collection("manger_id").document(management_id).collection("pupil").document(doc.id).update(
            {"new_data": False})
       #x = threading.Thread(target= upload_images_by_new_Thread , args= (docs,known_face_encodings, known_face_names))
       #x.start()



        """name_by_id[doc.id] =get_the_name_by_id(doc.id)
        db.collection("manger_id").document(management_id).collection("pupil").document(doc.id).update(
            {"new_data": False})
"""
        for doc in docs:
            data = doc.to_dict()
            directory = doc.id
            parent_dir = "/home/pi/Desktop/py/face_project/known_faces"
            path = os.path.join(parent_dir, directory)
            
            try:
                os.makedirs(path, exist_ok=True)
            except OSError as error:
                print("Directory '%s' can not be created")
            list_url = data["url_image"]
            i = 0
            for im_url in list_url:
                img_data = requests.get(im_url).content
                i = i + 1
                number = str(i)
                with open(path + '/' + number + ".jpeg", 'wb') as handler:
                    handler.write(img_data)
                img = cv2.imread(path + '/' + number + ".jpeg")
                encoding = face_recognition.face_encodings(img)
                if not len(encoding):
                    print(path + '/' + number + ".jpeg", "can't be encoded")
                    continue
                else:
                    # Append encodings and name
                    encoding = face_recognition.face_encodings(img)[0]
                    known_face_encodings += [encoding]
                    known_face_names += [doc.id]
                    print(directory + i + " has encoded succssfuly")



def upload_images_by_new_Thread(docs,known_face_encodings, known_face_names):
    for doc in docs:
        data = doc.to_dict()
        directory = doc.id
        parent_dir = "/home/pi/Desktop/py/face_project/known_faces"
        path = os.path.join(parent_dir, directory)
        print(path)
        try:
            os.makedirs(path, exist_ok=True)
        except OSError as error:
            print("Directory '%s' can not be created")
        list_url = data["url_image"]
        i = 0
        for im_url in list_url:
            img_data = requests.get(im_url).content
            i = i + 1
            number = str(i)
            with open(path + '/' + number + ".jpeg", 'wb') as handler:
                handler.write(img_data)
            img = cv2.imread(path + '/' + number + ".jpeg")
            encoding = face_recognition.face_encodings(img)
            if not len(encoding):
                print(path + '/' + number + ".jpeg", "can't be encoded")
                continue
            else:
                # Append encodings and name
                encoding = face_recognition.face_encodings(img)[0]
                known_face_encodings += [encoding]
                known_face_names += [doc.id]
              

def is_there_new_images():
    db = firestore.client()
    docs = db.collection("manger_id").document(management_id).collection("pupil").where("new_data", "==", True).get()
    if(len(docs)==0):
        return False
    return True


def initialize_images(known_face_encodings, known_face_names,name_by_id):
    parent_dir = "/home/pi/Desktop/py/face_project/known_faces"
    for name in os.listdir("known_faces"):
        i = 0
        name_by_id[name] = get_the_name_by_id(name)
        for filename in os.listdir(f'{"known_faces"}/{name}'):
            i = i + 1
            number = str(i)
            path = os.path.join(parent_dir, name)

            img = cv2.imread(path + '/' + number + ".jpeg")
            encoding = face_recognition.face_encodings(img)
            if not len(encoding):
                print(name + '/'+ number + ".jpeg has encoded sucessfuly")
                continue
            else:
                # Append encodings and name
                encoding = face_recognition.face_encodings(img)[0]
                known_face_encodings += [encoding]
                known_face_names += [name]
                print(name + '/'+ number + ".jpeg has encoded sucessfuly")


def write_list_to_file(name_file,list_to_write):
        with open("texts/"+name_file, 'w') as filehandle:
            for listitem in list_to_write:
                filehandle.write('%s\n' % listitem)
  


if __name__ == '__main__':
    # Displaying the image
    #upload_images_to_python()
    print ("hi") 
    dir = {1:10}
    print(type(dir))
    dir = str(dir)
    print(dir)
    print(type(dir),len(dir))

    
 

               
    """ls1 =[]
    ls2 = []
    dir ={}
    upload_new_images(ls1,ls2,dir)
    print(ls1,ls2,dir)"""

 
   


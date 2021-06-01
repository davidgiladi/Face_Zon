import face_zen
import face_recognition
import os
import cv2
import numpy as np
from datetime import datetime
import copy
import socket
import pickle 


# Initialize some variables


global known_face_encodings 
known_face_encodings = []
global known_face_names 
known_face_names = []

name_by_id= {"Unknown":"Unknown"}
# with open('texts/list_known_face_encoldings.txt', 'r') as in_file:
#     known_face_encodings = in_file.read().split(']')
# with open('texts/list_known_face_names.txt', 'r') as in_file:
#     known_face_names = in_file.read().split('\n')
# known_face_encodings.pop()
# known_face_names.pop()

#known_face_encodings = np.expand_dims(known_face_encodings, -1)

"""
Author: Aavid Giladi
this function open a server that get the informtion of new images 

"""

def server_to_get_new_images(name_by_id):
    
    server_socket = socket.socket()
    server_socket.bind(("0.0.0.0", 5500 ))
    server_socket.listen()
    print("Server is up and running")
    (client_socket, client_address) = server_socket.accept()
    print("Client connected")
    try:
        recvd_data_1 = client_socket.recv(999999999)
        recvd_data_2 = client_socket.recv(999999999)
        recvd_data_3 = client_socket.recv(999999999)
    except:
        print("error")    
    data_of_name_by_id = pickle.loads(recvd_data_3)
    for i in data_of_name_by_id:
        name_by_id[i]= data_of_name_by_id[i]
    print (data_of_name_by_id)
    print(type (data_of_name_by_id))

    data_of_known_face_names = pickle.loads(recvd_data_2)
    for i in data_of_known_face_names:
        known_face_names.append(i)
    print (data_of_known_face_names)
    print(type (data_of_known_face_names))

    data_of_known_face_encodings = pickle.loads(recvd_data_1)
    for i in data_of_known_face_encodings:
        known_face_encodings.append(i)
    print (data_of_known_face_encodings)
    print(type (data_of_known_face_encodings))
    
  
   

    client_socket.close()
    server_socket.close()





#server_to_get_new_images(name_by_id)




# This is a demo of running face recognition on live video from your webcam. It's a little more complicated than the
# other example, but it includes some basic performance tweaks to make things run a lot faster:
#   1. Process each video frame at 1/2 resolution (though still display it at full resolution)
#   2. Only detect faces in every other frame of video.

# PLEASE NOTE: This example requires OpenCV (the `cv2` library) to be installed only to read from your webcam.
# OpenCV is *not* required to use the face_recognition library. It's only required if you want to run this
# specific demo. If you have trouble installing it, try any of the other demos that don't require it instead.

# Get a reference to webcam #0 (the default one)
video_capture = cv2.VideoCapture(0)
#address="http://192.168.43.1:8080/video"
#video_capture.open(address)


#small is more accurate
TOLERANCE = 0.5

KNOWN_FACES_DIR = 'known_faces'




# We oranize known faces as subfolders of KNOWN_FACES_DIR
# Each subfolder's name becomes our label (name)
def get_images():
    for name in os.listdir(KNOWN_FACES_DIR):
       # name_by_id[name] = face_zen.get_the_name_by_id(name)

        # Next we load every file of faces of known person
        for filename in os.listdir(f'{KNOWN_FACES_DIR}/{name}'):

            # Load an image
            #image = face_recognition.load_image_file(f'{KNOWN_FACES_DIR}/{name}/{filename}')

            image = face_recognition.load_image_file(f'{KNOWN_FACES_DIR}/{name}/{filename}')

            # Get 128-dimension face encoding
            # Always returns a list of found faces, for this purpose we take first face only (assuming one face per image as you can't be twice on one image)

            encoding = face_recognition.face_encodings(image)
            if not len(encoding):
                 print(name +"-" + filename, "can't be encoded")
                 continue
            else:
             # Append encodings and name
                 encoding = face_recognition.face_encodings(image)[0]
                 known_face_encodings.append(encoding)
                 known_face_names.append(name)

#get_images()

face_zen.initialize_images(known_face_encodings , known_face_names, name_by_id)
#face_zen.write_list_to_file("list_known_face_encodings.txt",known_face_encodings)
#face_zen.write_list_to_file("list_known_face_names.txt",known_face_names)


# Initialize some variables
face_locations = []
face_encodings = []
face_names = []
process_this_frame = True

#Itamar add this shit
number = 1
nameDict={}
"""
i add these list 
"""
list_face = []

def updateFaceCounter(key):
    if key in nameDict:
        nameDict[key]+=1
    else:
        nameDict[key]=0




while True:
    face_zen.upload_new_images(known_face_encodings , known_face_names,name_by_id)

    # Grab a single frame of video
    ret, frame = video_capture.read()

    # Resize frame of video to 1/2 size for faster face recognition processing
    small_frame = cv2.resize(frame, (0, 0), fx=0.5, fy=0.5)

    # Convert the image from BGR color (which OpenCV uses) to RGB color (which face_recognition uses)
    rgb_small_frame = small_frame[:, :, ::-1]

    # Only process every other frame of video to save time
    if process_this_frame:
        # Find all the faces and face encodings in the current frame of video
        face_locations = face_recognition.face_locations(rgb_small_frame)
        face_encodings = face_recognition.face_encodings(rgb_small_frame, face_locations)

        face_names = []
        for face_encoding in face_encodings:
            # See if the face is a match for the known face(s)
            matches = face_recognition.compare_faces(known_face_encodings, face_encoding,TOLERANCE)
            name = "Unknown"


            # # If a match was found in known_face_encodings, just use the first one.
            # if True in matches:
            #     first_match_index = matches.index(True)
            #     name = known_face_names[first_match_index]

            # Or instead, use the known face with the smallest distance to the new face

            # i added this code
            face_distances = face_recognition.face_distance(known_face_encodings, face_encoding)
            best_match_index = np.argmin(face_distances)
            if matches[best_match_index]:
                name = known_face_names[best_match_index]


                a = 0
                print(name)

                for f in list_face:
                    if name == f["name"]:
                        a = 1
                        if (f["number"] > -1):
                            f["number"] = f["number"] + 2
                            
                            if f["number"] == 6 or f["number"] == 5:
                                face_zen.upload(f["name"])
                                f["number"] = f["number"] - 60

                if a == 0:
                    list_face.append({"name":name, "number": 2})
                print(list_face)  

                # So far




            face_names.append(name)
            #this is a test to print the name to the screen
            mssg="{}.recognized {} at:{}"
            updateFaceCounter(name)
            number=nameDict[name]
            print(mssg.format(number,name,datetime.now().strftime("%d/%m/%y %H:%M:%S")))
        list_face = face_zen.update_list(list_face)

    process_this_frame = not process_this_frame

    # Display the results
    for (top, right, bottom, left), name in zip(face_locations, face_names):
        # Scale back up face locations since the frame we detected in was scaled to 1/2 size
        top *= 2
        right *= 2
        bottom *= 2
        left *= 2

        # Draw a box around the face
        cv2.rectangle(frame, (left, top), (right, bottom), (0, 0, 255), 2)

        # Draw a label with a name below the face
        cv2.rectangle(frame, (left, bottom - 35), (right, bottom), (0, 0, 255), cv2.FILLED)
        font = cv2.FONT_HERSHEY_DUPLEX
        cv2.putText(frame, name_by_id[name], (left + 6, bottom - 6), font, 1.0, (255, 255, 255), 1)

    # Display the resulting image
    cv2.imshow('Video', frame)

    # Hit 'q' on the keyboard to quit!
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# Release handle to the webcam
video_capture.release()
cv2.destroyAllWindows()




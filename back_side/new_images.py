import face_zen
import socket 
import pickle


SERVER_IP = "127.0.0.1"
PORT = 5500






if __name__ == '__main__':
    # Displaying the image
    #upload_images_to_python()
    print ("hi")
    known_face_encodings =[]
    known_face_names = []
    name_by_id ={}
    if(face_zen.is_there_new_images()):    
        face_zen.upload_new_images(known_face_encodings, known_face_names,name_by_id)
        try: 
            my_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            my_socket.connect((SERVER_IP,PORT))
            print("connect to the server succeesfuly")
            data_of_known_face_encodings=pickle.dumps(known_face_encodings)
            my_socket.send(data_of_known_face_encodings)
            data_of_known_face_names=pickle.dumps(known_face_names)
            my_socket.send(data_of_known_face_names)
            data_of_name_by_id=pickle.dumps(name_by_id)
            my_socket.send(data_of_name_by_id)

            my_socket.close()
        except: 
            print ("error")



 
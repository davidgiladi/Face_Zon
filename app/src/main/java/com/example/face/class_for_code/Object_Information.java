package com.example.face.class_for_code;

public class Object_Information {
    private String name;
    private String email;
    private  String uid;

    public Object_Information(String name , String email,String uid)
    {
        this.name = name;
        this.email = email;
        this.uid = uid;
    }



    public String get_name (){
        return name;
    }
    public String get_email (){
        return email;
    }
    public String get_uid(){
        return uid;
    }

}

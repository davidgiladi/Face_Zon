package com.example.face.class_for_code;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.face.R;

public class Object_Information {
    private String name;
    private String email;
    private  String uid;
    private String url_image;

    public Object_Information(String name , String email,String uid,String url_image)
    {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.url_image = url_image;
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
    public String get_url_image(){
        return url_image;
    }



}

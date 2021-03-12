package com.example.face;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.face.class_for_code.MyCustomAdapter;
import com.example.face.class_for_code.Object_Information;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {


    ListView ls;
    ArrayList<Object_Information> Objects;
    FirebaseFirestore db;
    FirebaseAuth fAut;
    MyCustomAdapter adapter;
    StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ls = findViewById(R.id.ls);
        fAut = FirebaseAuth.getInstance();
        Objects = new ArrayList<Object_Information>();


        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();





    }
}
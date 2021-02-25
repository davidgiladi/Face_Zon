package com.example.face;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.face.class_for_code.MyCustomAdapter;
import com.example.face.class_for_code.Object_Information;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class List_Object extends AppCompatActivity {
    ListView listView;
    FirebaseFirestore db;
    FirebaseAuth fAut;
    ArrayList <Object_Information> Objects;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list__object);
        listView = findViewById(R.id.listView);
        fAut = FirebaseAuth.getInstance();
        Objects = new ArrayList<Object_Information>();


        db = FirebaseFirestore.getInstance();

        read_data(new Firestore_on_call() {
            @Override
            public void on_call(List<Object_Information> Objects) {
                listView.setAdapter(new MyCustomAdapter(List_Object.this, Objects));



            }
        });



      

        listView.setAdapter(new MyCustomAdapter(this, Objects));






    }

    private void read_data (final Firestore_on_call firestore_on_call){
        db.collection("manger_id").document(fAut.getCurrentUser().getUid())
                .collection("pupil")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot document : task.getResult()) {
                                String email = document.getString("email");
                                String name = document.getString("name");
                                Object_Information Object = new  Object_Information (name,email);
                                Objects.add(Object);
                            }
                            firestore_on_call.on_call(Objects);


                        } else {
                            Log.d("Tag", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private interface  Firestore_on_call {
        void on_call(List <Object_Information> list);
    }



}
package com.example.face;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.face.class_for_code.MyCustomAdapter;
import com.example.face.class_for_code.Object_Information;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class List_Object extends AppCompatActivity {
    ListView listView;
     EditText etSearch ;;
    FirebaseFirestore db;
    FirebaseAuth fAut;
    ArrayList <Object_Information> Objects;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list__object);
        listView = findViewById(R.id.listView);
        etSearch = findViewById(R.id.ed_search);
        fAut = FirebaseAuth.getInstance();
        Objects = new ArrayList<Object_Information>();


        db = FirebaseFirestore.getInstance();

        read_data(new Firestore_on_call() {
                    @Override
                    public void on_call(List<Object_Information> Objects) {
                        MyCustomAdapter adapter = new  MyCustomAdapter(List_Object.this, Objects);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Object_Information o1 = (Object_Information) parent.getItemAtPosition(position);
                                Toast.makeText(List_Object.this, o1.get_uid(), Toast.LENGTH_LONG).show();


                            }
                        });

            }
        });


        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    String name = etSearch.getText().toString().trim();
                    read_data_after_search(name , new Firestore_on_call() {
                        @Override
                        public void on_call(List<Object_Information> Objects) {
                            MyCustomAdapter adapter = new  MyCustomAdapter(List_Object.this, Objects);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Object_Information o1 = (Object_Information) parent.getItemAtPosition(position);
                                    Toast.makeText(List_Object.this, o1.get_uid(), Toast.LENGTH_LONG).show();


                                }
                            });

                        }
                    });



                    return true;
                }
                return false;
            }
        });



















    }

    private void read_data (final Firestore_on_call firestore_on_call) {


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
                                    String uid = document.getId();
                                    Object_Information Object = new Object_Information(name, email,uid);
                                    Objects.add(Object);
                                }
                                firestore_on_call.on_call(Objects);


                            } else {
                                Log.d("Tag", "Error getting documents: ", task.getException());
                            }
                        }
                    });

        }




    private void read_data_after_search (String name ,final Firestore_on_call firestore_on_call){
        Objects.clear();
        db.collection("manger_id").document(fAut.getCurrentUser().getUid())
                .collection("pupil")
                .whereEqualTo("name",name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot document : task.getResult()) {
                                String email = document.getString("email");
                                String name = document.getString("name");
                                String uid = document.getId();
                                Object_Information Object = new  Object_Information (name,email, uid);
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
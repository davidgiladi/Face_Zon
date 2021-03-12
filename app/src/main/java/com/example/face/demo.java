package com.example.face;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.face.class_for_code.Your_Image_Adapter;
import com.example.face.dialog.Dialog_Error;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import static com.example.face.R.*;

public class demo extends AppCompatActivity {

    EditText user_name;
    EditText user_email;
    EditText user_password;
    FirebaseAuth fAut;
    FirebaseFirestore db;
    StorageReference mStorageRef;
    String name, email, password, manger_id, email_manger, password_manger;
    ImageView image_view_1;// main image of the user
    Uri uri_image_view_1;//uri of that image
    boolean is_upload_imade = false;
    ProgressBar progressBar;
    ArrayList<Uri> list_image_of_user;
    int upload_count;

    public static final int GALLERY_REQUEST_CODE = 105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_demo);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);



        user_name = findViewById(id.username);
        user_email = findViewById(id.user_email);
        user_password = findViewById(id.user_password);
        Button bt_save = findViewById(id.bt_save);
        fAut = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        FirebaseUser user = fAut.getCurrentUser();
        manger_id = user.getUid();


        image_view_1 = findViewById(id.image_view_1);

        progressBar = findViewById(id.progressBar);
        image_view_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*"); //allows any image file type. Change * to specific extension to limit it
                //**The following line is the important one!
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE); //SELECT_PICTURES is simply a global int used to check the calling intent in onActivityResult
/*
                Intent In_gl = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(In_gl, GALLERY_REQUEST_CODE);*/
            }
        });


        /// get the details of  the manger from firestore for sign
        DocumentReference docRef = db.collection("manager").document(manger_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "it is working ");
                        password_manger = document.getData().get("password").toString();
                        email_manger = document.getData().get("email").toString();


                    } else {
                        Log.d("tag", "No such document");
                        startActivity(new Intent(getApplicationContext(), Login.class));

                    }
                } else {
                    Log.d("tag", "get failed with ", task.getException());
                }
            }
        });

        /// finish to get the details of  the manger from firestore

        /// when you click save
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int number_of_error = 0;
                name = user_name.getText().toString().trim();
                email = user_email.getText().toString().trim();
                password = user_password.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    user_name.setError(" Please enter your name");
                    number_of_error++;
                }
                if (TextUtils.isEmpty(email)) {
                    user_email.setError(" Please enter your email");
                    number_of_error++;

                }
                if (TextUtils.isEmpty(password)) {
                    user_password.setError("Please enter password");
                    number_of_error++;
                } else {
                    if (password.length() < 6) {
                        user_password.setError("The password must contain at least 6 character\"");
                        number_of_error++;
                    }
                }
                if (!is_upload_imade) {
                    if (number_of_error == 0) {
                        Dialog_Error dialog_error_missing_picture = new Dialog_Error();
                        dialog_error_missing_picture.show(getSupportFragmentManager(), "example dialog");
                    }
                    number_of_error++;
                }

                if (number_of_error > 0) {
                    return;
                }

                // create user and put the details in firestore
                progressBar.setVisibility(View.VISIBLE);
                fAut.signOut();
                fAut.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    UpLoad_To_Firebase( manger_id, fAut.getCurrentUser().getUid()); // upload  to firebase

                                }

                            }

                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(demo.this, "Error ", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), Login.class));

                    }
                });
            }
        });


    }


    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == GALLERY_REQUEST_CODE) {
                list_image_of_user = new ArrayList<Uri>();

                if (data != null) {

                    if (data.getData() != null) {
                        is_upload_imade = true;
                        Uri contentURI = data.getData();
                        list_image_of_user.add(contentURI);

                        Log.d("TAG", "onActivityResult: " + contentURI.toString());
                        try {

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), contentURI);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {

                        if (data.getClipData() != null) {
                            is_upload_imade = true;
                            ClipData mClipData = data.getClipData();
                            for (int i = 0; i < mClipData.getItemCount(); i++) {

                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();
                                list_image_of_user.add(uri);

                            }
                        }

                    }

                }


            }

        }
    }



/*
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case GALLERY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {


                    uri_image_view_1 = data.getData();
                    image_view_1.setImageURI(uri_image_view_1);
                    is_upload_imade = true;
                }
                break;

        }
    }
*/


    /*
    Function gets the uri of tha user's picture.
    Additionally, gets tow string  uid and mid that determine the location where the image will be updated.


 */


    private void UpLoad_To_Firebase(final String mid, final String uid) {


        final ArrayList<String> url_address = new ArrayList<String>();
        for (upload_count = 0; upload_count < list_image_of_user.size(); upload_count++) {
            Uri IndividualImage = list_image_of_user.get(upload_count);

            final StorageReference ref = mStorageRef.child("manger/" + mid + "/" + "user/" + uid + "/" +upload_count+ "Main_picture.jpg");
            ref.putFile(IndividualImage).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(
                                    new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            url_address.add(String.valueOf(uri));


                                            if (url_address.size() == list_image_of_user.size()) {
                                                Map<String, Object> data = new HashMap<>();
                                                data.put("name", name);
                                                data.put("email", email);
                                                data.put("password", password);
                                                data.put("url_image", url_address);
                                                data.put("new_data", true);
                                                db.collection("manger_id").document(mid)
                                                        .collection("pupil").document(uid)
                                                        .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        fAut.signOut();
                                                        while (fAut.getCurrentUser() != null) {
                                                        }
                                                        fAut.signInWithEmailAndPassword(email_manger, password_manger).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if (task.isSuccessful()) {
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    Toast.makeText(demo.this, "The user added successfully  ", Toast.LENGTH_LONG).show();
                                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                                                                } else {
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    Toast.makeText(demo.this, "Error ", Toast.LENGTH_LONG).show();
                                                                    startActivity(new Intent(getApplicationContext(), Login.class));
                                                                }

                                                            }
                                                        });

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(demo.this, "Error ", Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(getApplicationContext(), Login.class));
                                                    }
                                                });
                                            }

                                        }
                                    }
                            );
                        }
                    }
            );




        }


    }
}




























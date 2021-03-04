package com.example.face;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

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
    ArrayList<ImageView> list_image_of_user = null;
    public static final int GALLERY_REQUEST_CODE = 105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        user_name = findViewById(R.id.username);
        user_email = findViewById(R.id.user_email);
        user_password = findViewById(R.id.user_password);
        Button bt_save = findViewById(R.id.bt_save);
        fAut = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        FirebaseUser user = fAut.getCurrentUser();
        manger_id = user.getUid();


        image_view_1 = findViewById(R.id.image_view_1);

        progressBar = findViewById(R.id.progressBar);
        image_view_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent In_gl = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(In_gl, GALLERY_REQUEST_CODE);
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
                                   UpLoad_To_Firebase(uri_image_view_1, manger_id, fAut.getCurrentUser().getUid()); // upload  to firebase
                                        }

                                    }

                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(demo.this, "Error ", Toast.LENGTH_LONG).show();
                        return;
                    }
                });
            }
        });


    }

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


    /*
    Function gets the uri of tha user's picture.
    Additionally, gets tow string  uid and mid that determine the location where the image will be updated.

 */
    private void UpLoad_To_Firebase(Uri imageUri, String mid, String uid) {


        final StorageReference ref = mStorageRef.child("manger/" + mid + "/" + "user/" + uid + "/" + "Main_picture.jpg");
        ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Log.d("upload", "The image was successfully uploaded to the server");
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url_image = uri.toString();
                        Map<String, Object> data = new HashMap<>();
                        data.put("name", name);
                        data.put("email", email);
                        data.put("password", password);
                        data.put("url_image", url_image);
                        FirebaseUser new_user = fAut.getCurrentUser();
                        String user_id = new_user.getUid();

                        db.collection("manger_id").document(manger_id)
                                .collection("pupil").document(user_id)
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
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(demo.this, "Error while attempt to upload the picture to the server", Toast.LENGTH_LONG).show();
            }
        });




    }



}




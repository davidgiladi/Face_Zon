package com.example.face;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth fAut;
    FirebaseStorage mStorageRef;
    FloatingActionButton addObjectButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStorageRef = FirebaseStorage.getInstance();

        addObjectButton = findViewById(R.id.add);

//        textView = findViewById(R.id.textView);
        //StorageReference ref = mStorageRef.getReferenceFromUrl("gs://facezen-18934.appspot.com/").child("manger/"+fAut.getCurrentUser().getUid()+"/"+"user one.jpg");
       /* File file = null;
        try {
            file = File.createTempFile("image","jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final File finalFile = file;
        ref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Bitmap bitmap = BitmapFactory.decodeFile(finalFile.getAbsolutePath());
                image_1.setImageBitmap(bitmap);
            }
        });*/

        fAut = FirebaseAuth.getInstance();
//        textView.setText( "Current user : " + fAut.getCurrentUser().getUid());


        addObjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), addObject.class));
            }
        });
    }

    //this function is for the items in the navigation bar
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.bt_log_out:
                fAut.signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                break;

            case R.id.bt_list:
                startActivity(new Intent(getApplicationContext(), List_Object.class));
                break;

            case R.id.customize:
                //todo here you make the custom request
                startActivity(new Intent(getApplicationContext(), CustomizeActivity.class));
                break;
        }
        return false;
    }

}
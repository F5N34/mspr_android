package com.mspr.msprjava.model;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;

import com.mspr.msprjava.R;
import com.mspr.msprjava.controller.MainActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {
    private String photoPath = null;
    ImageView imagePhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        takePicture();
    }

    private void takePicture(){
        //créer une fenêtre pour prendre une photo
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //test
        if(intent.resolveActivity(getPackageManager()) != null){
            // création d'un nom pour la photo prise
            String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File photoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File photoFile = File.createTempFile("photo_"+time, ".jpg", photoDir);
                //Enregistrer le chemin complet
                photoPath = photoFile.getAbsolutePath();
                //creation URI
                Uri photoUri = FileProvider.getUriForFile(CameraActivity.this,
                        CameraActivity.this.getApplicationContext().getPackageName()+".provider", photoFile);
                //tranfert URI vers intent pour enregistrer la photo dans fichier temp
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                //ouvrir l'activity
                startActivityForResult(intent, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        // verif du bon code de retour + état du retour
        if(requestCode == 1 && resultCode == RESULT_OK){
            //récupération de l'image
            Bitmap image = BitmapFactory.decodeFile(photoPath);
            imagePhoto = (ImageView)findViewById(R.id.coucou);
            imagePhoto.setImageBitmap(image);

        }
    }
}

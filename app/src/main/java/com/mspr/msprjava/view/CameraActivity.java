package com.mspr.msprjava.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mspr.msprjava.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {
    private String photoPath = null;
    ImageView imagePhoto;
    Button retakePicture;
    private StorageReference mStorageRef;
    private File photoFile;
    private String time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        takePicture();

    }

    public void takePicture(){
        //créer une fenêtre pour prendre une photo
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //test
        if(intent.resolveActivity(getPackageManager()) != null){
            // création d'un nom pour la photo prise
            time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File photoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                photoFile = File.createTempFile("photo_"+time, ".jpg", photoDir);
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
        else{
            Log.v("coucuo", "marche aps");
        }

    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        // verif du bon code de retour + état du retour
        if(requestCode == 1 && resultCode == RESULT_OK){
            //récupération de l'image
            Bitmap image = BitmapFactory.decodeFile(photoPath);
            imagePhoto = (ImageView)findViewById(R.id.picture);
            imagePhoto.setImageBitmap(image);
            startPopup();

        }
    }

    public void startPopup() {
        final AlertDialog.Builder popup = new AlertDialog.Builder(this);
        popup.setTitle("Photo");
        popup.setMessage("Voulez vous reprendre la photo ?");
        popup.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                takePicture();
            }
        });

        popup.setNegativeButton("Envoyer la photo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendPicture();
                //setContentView(R.layout.activity_accueil);
            }
        });
        popup.show();
    }

    public void sendPicture() {
        Uri file = Uri.fromFile(new File(photoPath));
        StorageReference riversRef = mStorageRef.child("pictures/photo_"+time+".jpg");
        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }
}

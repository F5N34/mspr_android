package com.mspr.msprjava.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mspr.msprjava.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;

public class CameraActivity extends AppCompatActivity {
    private String photoPath = null;
    ImageView imagePhoto;
    TextView textRecognize;
    Button retakePicture;
    private StorageReference mStorageRef;
    private File photoFile;
    private String time;
    Bitmap image;
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
            this.image = BitmapFactory.decodeFile(photoPath);
//            imagePhoto = (ImageView)findViewById(R.id.picture);
//            imagePhoto.setImageBitmap(image);
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
        StorageReference riversRef = mStorageRef.child("photo_"+time+".jpg");
        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "La photo c'est bien envoyée", Toast.LENGTH_SHORT).show();
                        runRecognizeText();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception ex) {
                        Toast.makeText(getApplicationContext(), "Erreur, veuillez réessayer", Toast.LENGTH_SHORT).show();
                    }

                });
    }

    public void runRecognizeText() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(this.image);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                recognizeText(texts);
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Erreur", Toast.LENGTH_SHORT).show();
                                    }
                                });

    }

    public void recognizeText(FirebaseVisionText texts) {
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if(blocks.size() == 0){
            Toast.makeText(getApplicationContext(), "Erreur", Toast.LENGTH_SHORT).show();
            return;
        }

        for (FirebaseVisionText.TextBlock block : texts.getTextBlocks()) {
            String txt = block.getText();
            textRecognize = (TextView)findViewById(R.id.textReco);
            textRecognize.setText(txt);
        }

/*        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                Log.v("te", "tes");
            }
        }*/

/*        String resultText = texts.getText();
        for (FirebaseVisionText.TextBlock block: texts.getTextBlocks()) {
            String blockText = block.getText();
            Float blockConfidence = block.getConfidence();
            List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            for (FirebaseVisionText.Line line: block.getLines()) {
                String lineText = line.getText();
                Float lineConfidence = line.getConfidence();
                List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();
                for (FirebaseVisionText.Element element: line.getElements()) {
                    String elementText = element.getText();
                    Float elementConfidence = element.getConfidence();
                    List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();
                }
            }
        }*/
    }
}

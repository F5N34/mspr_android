package com.mspr.msprjava.view;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.FirebaseApp;
import com.mspr.msprjava.R;

public class MainActivity extends AppCompatActivity {
    private Button signIn;
    public ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_accueil );
        signIn = (Button)findViewById(R.id.signIn);
        img = (ImageView)findViewById(R.id.logo);
        Typeface tf =  Typeface.createFromAsset( getAssets(), "fonts/Roboto-Medium.ttf" );
        signIn.setTypeface( tf );
        //createOnClickBtnSignIn();
        FirebaseApp.initializeApp(this);
        FirebaseApp.getInstance();
        createOnClickBtnSignIn();
    }

    protected void onStart(){
        super.onStart();
        Log.v( "onStart", "Good" );
    }

    protected void onResume(){
        super.onResume();
        Log.v( "onResume", "Good" );
    }

    protected void onPause(){
        super.onPause();
        Log.v( "onPause", "Good" );
    }

    protected void onStop(){
        super.onStop();
        Log.v( "onStop", "Good" );
    }

    private void createOnClickBtnSignIn(){
        signIn = (Button) findViewById( R.id.signIn );
        signIn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                goToCamera();
            }
        });
    }

    private void goToCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }
}

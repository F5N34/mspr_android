package com.mspr.msprjava.controller;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.mspr.msprjava.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_accueil );

        Typeface tf =  Typeface.createFromAsset( getAssets(), "fonts/Roboto-Medium.ttf" );
        Button signIn = (Button) findViewById( R.id.signIn );
        signIn.setTypeface( tf );
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


}

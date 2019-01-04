package com.mspr.msprjava;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_accueil );

        Typeface tf =  Typeface.createFromAsset( getAssets(), "fonts/Roboto-Medium.ttf" );
        Button signIn = (Button) findViewById( R.id.signIn );
        signIn.setTypeface( tf );
    }
}

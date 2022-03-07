package com.example.bsafe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRGenerator extends AppCompatActivity {
    String qrValue;
    ImageView qrImage;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_qr_code);

        qrImage = findViewById(R.id.qrContainer);
        qrValue = "I am allergic to Luca";

        QRGEncoder qrgEncoder = new QRGEncoder(qrValue, null, QRGContents.Type.TEXT,500);
        try {
            // Getting QR-Code as Bitmap
            Bitmap bitmap = qrgEncoder.getBitmap();
            // Setting Bitmap to ImageView
            qrImage.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
}

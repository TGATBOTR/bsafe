package com.example.bsafe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;


import com.example.bsafe.Auth.Session;
import com.example.bsafe.Database.Models.User;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Inject
    public Session session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User currentUser = session.getUser();

        TextView label = (TextView) findViewById(R.id.label);

        label.setText("Hello, " + currentUser.firstName);
    }
}
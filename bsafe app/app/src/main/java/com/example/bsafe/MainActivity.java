package com.example.bsafe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;


import com.example.bsafe.Auth.Session;
import com.example.bsafe.Database.Daos.AllergyDao;
import com.example.bsafe.Database.Models.Allergy;
import com.example.bsafe.Database.Models.User;

import java.util.List;

import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Inject
    public Session session;

    @Inject
    public AllergyDao allergyDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User currentUser = session.getUser();

        TextView label = (TextView) findViewById(R.id.label);

        label.setText("Hello, " + currentUser.firstName);

        Thread t = new Thread() {
            public void run() {
                List<Allergy> allergies = allergyDao.getUserAllergies(currentUser.uid);
            }
        };

    }
}
package com.example.bsafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.bsafe.Auth.Session;
import com.example.bsafe.Database.Daos.AllergyDao;
import com.example.bsafe.Database.Models.Allergy;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddAllergyActivity extends AppCompatActivity {

    @Inject
    public Session session;
    @Inject
    public AllergyDao allergyDao;

    private TextView allergyName;
    private TextView allergySeverity;
    private TextView allergySymptoms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_allergy);

        allergyName = findViewById(R.id.allergyName);
        allergySeverity = findViewById(R.id.allergySeverity);
        allergySymptoms = findViewById(R.id.allergySymptoms);

    }

    /*
    ADD ALLERGY TO DB
     */
    public void addAllergy(View view) {
        Allergy allergy = new Allergy();

        // SET VALUES
        allergy.name = allergyName.getText().toString();
        allergy.scale = Integer.parseInt(allergySeverity.getText().toString());
        allergy.attachToUser(session.getUser());
        allergy.symptoms = allergySymptoms.getText().toString();

        // TODO: INSERT INTO DB


        Thread t = new Thread() {
            public void run() {
                allergyDao.insertAll(allergy);
            }
        };
        t.start();
        try {
            t.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        Intent i=new Intent(getBaseContext(),MainActivity.class);
        finish();
        startActivity(i);
    }
}
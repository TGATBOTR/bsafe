package com.example.bsafe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bsafe.Auth.Session;
import com.example.bsafe.Database.Daos.AllergyDao;
import com.example.bsafe.Database.Models.Allergy;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UpdateAllergyActivity extends AppCompatActivity {

    @Inject
    public Session session;
    @Inject
    public AllergyDao allergyDao;

    private Allergy allergy;

    private TextView allergyName;
    private TextView allergySeverity;
    private TextView allergySymptoms;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_allergy);

        allergyName = findViewById(R.id.allergyName);
        allergySeverity = findViewById(R.id.allergySeverity);
        allergySymptoms = findViewById(R.id.allergySymptoms);

        int index = getIntent().getIntExtra("index", -1);

        if (index != -1){
            Thread t = new Thread() {
                public void run() {
                    List<Allergy> allergies = allergyDao.getUserAllergies(session.getUser().uid);
                    allergy = allergies.get(index);
                }
            };
            t.start();
            try { t.join(); } catch (InterruptedException e){ e.printStackTrace(); }
        }

        allergyName.setText(allergy.name);
        allergySeverity.setText(""+allergy.scale);
        allergySymptoms.setText(allergy.symptoms);

    }

    /*
    ADD ALLERGY TO DB
     */
    public void updateAllergy(View view) {
        Allergy newAllergy = new Allergy();

        // SET VALUES
        newAllergy.uid = allergy.uid;
        newAllergy.name = allergyName.getText().toString();
        newAllergy.scale = Integer.parseInt(allergySeverity.getText().toString());
        newAllergy.attachToUser(session.getUser());
        newAllergy.symptoms = allergySymptoms.getText().toString();

        // TODO: INSERT INTO DB


        Thread t = new Thread() {
            public void run() {
                allergyDao.updateUsers(newAllergy);
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

    public void deleteAllergy(View view){
        Thread t = new Thread() {
            public void run() {
                allergyDao.delete(allergy);
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
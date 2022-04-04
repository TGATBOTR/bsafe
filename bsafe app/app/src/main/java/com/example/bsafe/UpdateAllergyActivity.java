package com.example.bsafe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bsafe.Auth.Session;
import com.example.bsafe.Database.Daos.AllergyDao;
import com.example.bsafe.Database.Models.Allergy;
import com.example.bsafe.I18n.Localizer;
import com.google.android.material.slider.Slider;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UpdateAllergyActivity extends AppCompatActivity {

    @Inject
    public Session session;
    @Inject
    public AllergyDao allergyDao;
    @Inject
    public Localizer i18n;

    int index;

    private Allergy allergy;

    private TextView allergyName;
    private Slider allergySeverity;
    private TextView allergySymptoms;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_allergy);

        allergyName = findViewById(R.id.allergyName);
        allergySeverity = findViewById(R.id.allergySeverity);
        allergySymptoms = findViewById(R.id.allergySymptoms);

        index = getIntent().getIntExtra("index", -1);

        if (index != -1){
            Thread t = new Thread() {
                public void run() {
                    List<Allergy> allergies = allergyDao.getUserAllergies(session.getUser().uid);
                    setAllergy(allergies.get(index));
                }
            };
            t.start();
            try { t.join(); } catch (InterruptedException e){ e.printStackTrace(); }
        }

        allergyName.setText(allergy.name);
        if (allergy.scale != null) {
            allergySeverity.setValue(allergy.scale);
        }
        allergySymptoms.setText(allergy.symptoms);

    }

    private synchronized void setAllergy(Allergy allergy){
        this.allergy = allergy;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLocalisation();
    }

    public void updateLocalisation()
    {
        ((EditText) findViewById(R.id.allergyName)).setHint(i18n.get("ENTER_ALLERGY"));
//        ((EditText) findViewById(R.id.allergySeverity)).setHint(i18n.get("ENTER_ALLERGY_LEVEL"));
        ((EditText) findViewById(R.id.allergySymptoms)).setHint(i18n.get("ENTER_ALLERGY_SYMPTOMS"));

        ((Button) findViewById(R.id.updateAllergy)).setText(i18n.get("UPDATE_ALLERGY"));
        ((Button) findViewById(R.id.deleteAllergy)).setText(i18n.get("DELETE"));
    }

    /*
    ADD ALLERGY TO DB
     */
    public void updateAllergy(View view) {
        Allergy newAllergy = new Allergy();

        // SET VALUES
        newAllergy.uid = allergy.uid;
        newAllergy.name = allergyName.getText().toString();
        newAllergy.scale = (int) allergySeverity.getValue();
        newAllergy.attachToUser(session.getUser());
        newAllergy.symptoms = allergySymptoms.getText().toString();

        // TODO: INSERT INTO DB


        Thread t = new Thread() {
            public void run() {
                allergyDao.updateAll(newAllergy);
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
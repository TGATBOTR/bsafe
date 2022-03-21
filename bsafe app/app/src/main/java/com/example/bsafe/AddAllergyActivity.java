package com.example.bsafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bsafe.Auth.Session;
import com.example.bsafe.Database.Daos.AllergyDao;
import com.example.bsafe.Database.Models.Allergy;
import com.example.bsafe.I18n.Localizer;
import com.google.android.material.slider.Slider;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddAllergyActivity extends AppCompatActivity {

    @Inject
    public Session session;
    @Inject
    public AllergyDao allergyDao;

    @Inject
    public Localizer i18n;

    private AutoCompleteTextView allergyName;
    private Slider allergySeverity;
    private AutoCompleteTextView allergySymptoms;

    private String[] commonAllergies;
    private String[] commonSymptoms;

    private ArrayAdapter<String> allergyAdapter;
    private ArrayAdapter<String> symptomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_allergy);

        allergyName = findViewById(R.id.allergyName);
        allergySeverity = findViewById(R.id.allergySeverity);
        allergySymptoms = findViewById(R.id.allergySymptoms);

        commonAllergies = getResources().getStringArray(R.array.commonAllergies);
        allergyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, commonAllergies);
        allergyName.setAdapter(allergyAdapter);
        allergyName.setThreshold(0);


        commonSymptoms = getResources().getStringArray(R.array.commonSymptoms);
        symptomAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, commonSymptoms);
        allergySymptoms.setAdapter(symptomAdapter);
        allergySymptoms.setThreshold(0);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLocalisation();
    }

    public void updateLocalisation()
    {
        ((EditText) findViewById(R.id.allergyName)).setHint(i18n.get("ENTER_ALLERGY"));
        ((EditText) findViewById(R.id.allergySymptoms)).setHint(i18n.get("ENTER_ALLERGY_SYMPTOMS"));

        ((Button) findViewById(R.id.addAllergy)).setText(i18n.get("ADD_ALLERGY"));
    }

    /*
    ADD ALLERGY TO DB
     */
    public void addAllergy(View view) {
        Allergy allergy = new Allergy();

        // SET VALUES
        allergy.name = allergyName.getText().toString();
        allergy.scale = (int) allergySeverity.getValue();
        allergy.attachToUser(session.getUser());
        allergy.symptoms = allergySymptoms.getText().toString();

        // INSERT INTO DB
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

        //GO BACK TO HOME PAGE
        Intent i=new Intent(getBaseContext(),MainActivity.class);
        finish();
        startActivity(i);
    }
}
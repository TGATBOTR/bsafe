package com.example.bsafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bsafe.Auth.Session;
import com.example.bsafe.Database.Daos.AllergyDao;
import com.example.bsafe.Database.Daos.EmergencyContactsDao;
import com.example.bsafe.Database.Models.Allergy;
import com.example.bsafe.Database.Models.EmergencyContacts;
import com.example.bsafe.Database.Models.User;
import com.example.bsafe.I18n.Localizer;

import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Inject
    public Session session;
    @Inject
    public AllergyDao allergyDao;
    @Inject
    public EmergencyContactsDao emergencyContactsDao;

    @Inject
    public Localizer i18n;

    private List <Allergy> allergies;
    private List <EmergencyContacts> emergencyContacts = new ArrayList<EmergencyContacts>();
    private boolean retrieved = false;


    private TextView englishAllergyName;
    private int currentAllergy = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        englishAllergyName = findViewById(R.id.textView3);

        User currentUser = session.getUser();


        EmergencyContacts emergencyContactUK = new EmergencyContacts();
        emergencyContactUK.name = "United Kingdom";
        emergencyContactUK.number = 999;
        emergencyContacts.add(emergencyContactUK);

        EmergencyContacts emergencyContactRO = new EmergencyContacts();
        emergencyContactRO.name = "Romania";
        emergencyContactRO.number = 112;
        emergencyContacts.add(emergencyContactRO);

        EmergencyContacts emergencyContactIT = new EmergencyContacts();
        emergencyContactIT.name = "Italy";
        emergencyContactIT.number = 112;
        emergencyContacts.add(emergencyContactIT);

        EmergencyContacts emergencyContactUS = new EmergencyContacts();
        emergencyContactUS.name = "United States";
        emergencyContactUS.number = 911;
        emergencyContacts.add(emergencyContactUS);

        EmergencyContacts emergencyContactSP = new EmergencyContacts();
        emergencyContactSP.name = "Spain";
        emergencyContactSP.number = 112;
        emergencyContacts.add(emergencyContactSP);

        EmergencyContacts emergencyContactSW = new EmergencyContacts();
        emergencyContactSW.name = "Switzerland";
        emergencyContactSW.number = 117;
        emergencyContacts.add(emergencyContactSW);


        Thread t = new Thread() {
            public void run() {
                allergies = allergyDao.getUserAllergies(currentUser.uid);
                for (EmergencyContacts emergencyContact : emergencyContacts){
                    if (emergencyContactsDao.searchByName(emergencyContact.name).size() == 0 ){
                        emergencyContactsDao.insertAll(emergencyContact);
                    }
                }
            }
        };
        t.start();
        try { t.join(); } catch (InterruptedException e){ e.printStackTrace(); }

        setAllergyText();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLocalisation();
    }

    public void updateLocalisation()
    {
        ((TextView) findViewById(R.id.textView2)).setText(i18n.get("LANGUAGE") + ":");
        ((TextView) findViewById(R.id.textView4)).setText(i18n.get("FRENCH") + ":");

        ((Button) findViewById(R.id.button)).setText(i18n.get("SHOW_ALL"));
        ((Button) findViewById(R.id.qrButton)).setText(i18n.get("GENERATE_QR"));
        ((Button) findViewById(R.id.editButton)).setText(i18n.get("EDIT"));
    }

    // MOVE BETWEEN ALLERGIES
    // will change to swipe gesture
    public void shift( View view){
        int shift;
        if (view.getId() == R.id.right){
            shift = 1;
        } else {
            shift = -1;
        }
        if (allergies.size() != 0) {
            currentAllergy += shift;
            if (currentAllergy < 0){
                currentAllergy+=allergies.size();
            }
            if (currentAllergy > allergies.size()-1){
                currentAllergy-=allergies.size();
            }
            setAllergyText();
        }
    }

    // UPDATE TEXT ON SCREEN
    // will change to swipe gesture, may not need
    //TODO:ADD TRANSLATED TEXT
    private void setAllergyText(){
        String text;
        if(allergies.size() != 0){
            text = allergies.get(currentAllergy).name;
        } else {
            text = "ADD AN ALLERGY!";
        }
        englishAllergyName.setText(text);
    }

    // GO TO ADD ALLERGY PAGE
    public void addAllergy(View view) {
        Intent i=new Intent(getBaseContext(),AddAllergyActivity.class);
        startActivity(i);
    }

    // GO TO ADD QR PAGE
    public void QRpage(View view) {
        Intent i=new Intent(getBaseContext(),QRGenerator.class);
        startActivity(i);
    }

    //GO TO VIEW ALL ALLERGIES PAGE
    public void showAll(View view) {
        Intent i=new Intent(getBaseContext(),ViewAllergies.class);
        startActivity(i);
    }

    // GO TO CHANGE LANGUAGE PAGE
    public void changeLanguagePage(View view) {
        Intent intent = new Intent(getBaseContext(), ChangeLanguage.class);
        //finish();
        startActivity(intent);

    }

    public void seeContacts(View view) {
        Intent i=new Intent(getBaseContext(),ViewContacts.class);
        startActivity(i);
    }
}
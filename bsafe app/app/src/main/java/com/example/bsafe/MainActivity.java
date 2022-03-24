package com.example.bsafe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.bsafe.Auth.Session;
import com.example.bsafe.Database.Daos.AllergyDao;
import com.example.bsafe.Database.Daos.EmergencyContactsDao;
import com.example.bsafe.Database.Models.Allergy;
import com.example.bsafe.Database.Models.EmergencyContacts;
import com.example.bsafe.Database.Models.User;
import com.example.bsafe.I18n.Localizer;

import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

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

    private Map<String, String> langOptions;
    public static String targetLanguage;

    @SuppressLint("ClickableViewAccessibility")
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

        MainActivity mainActivity = this;

        ((Spinner) findViewById(R.id.spinnerTargetLanguage)).setOnItemSelectedListener(this);

        ConstraintLayout page = findViewById(R.id.page);
        page.setOnTouchListener(new OnSwipeTouchListener(mainActivity){
            public void onSwipeTop() {
                return;
            }
            public void onSwipeRight() {
                shift(1);
            }
            public void onSwipeLeft() {
                shift(-1);
            }
            public void onSwipeBottom() {
                return;
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        this.targetLanguage = this.langOptions.get(text);
        translateAllergy();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    @Override
    protected void onResume() {
        super.onResume();
        updateLocalisation();
    }

    public void updateLocalisation()
    {
        ((TextView) findViewById(R.id.textView2)).setText(i18n.get("LANGUAGE") + ":");

        ((Button) findViewById(R.id.button)).setText(i18n.get("SHOW_ALL"));
        ((Button) findViewById(R.id.qrButton)).setText(i18n.get("QR"));
        ((Button) findViewById(R.id.editButton)).setText(i18n.get("EDIT"));
        ((Button) findViewById(R.id.button2)).setText(i18n.get("CONTACTS"));

        Spinner sp = ((Spinner) findViewById(R.id.spinnerTargetLanguage));

        this.langOptions = new HashMap<>();

        // Create hash map with translated display names for languages
        List<String> displayItems = new ArrayList<>();
        int selectedId = 0;
        int counter = 0;
        for(String key: TranslationAPI.targetLanguages.keySet()) {
            String translation = i18n.get(key);
            String value = TranslationAPI.targetLanguages.get(key);
            this.langOptions.put(translation, value);
            displayItems.add(translation);

            if(this.targetLanguage != null) {
                if(this.targetLanguage.equals(value)) {
                    selectedId = counter;
                }
            }

            counter += 1;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, displayItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);

        if(this.targetLanguage == null) {
            sp.setSelection(0);
        } else {
            sp.setSelection(selectedId);
        }

    }

    // MOVE BETWEEN ALLERGIES
    // will change to swipe gesture
    public void shift(int shiftAmount){
        if (allergies.size() != 0) {
            currentAllergy += shiftAmount;
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
            translateAllergy();
        } else {
            text = "ADD AN ALLERGY!";
        }
        englishAllergyName.setText(text);
    }

    private void translateAllergy()
    {
        ((TextView) findViewById(R.id.translatedAllergyName)).setText(i18n.get("LOADING"));

        String text = allergies.get(currentAllergy).name;


        TranslationAPI translateTask = new TranslationAPI(this.targetLanguage, text, new OnTaskCompleted() {
            @Override
            void onTaskCompleted(String translation) {
                ((TextView) findViewById(R.id.translatedAllergyName)).setText(translation);
            }
        });

        translateTask.execute();
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
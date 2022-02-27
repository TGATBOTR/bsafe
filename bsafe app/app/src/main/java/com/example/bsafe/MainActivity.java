package com.example.bsafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

    private List <Allergy> allergies;
    private boolean retrieved = false;


    private TextView englishAllergyName;
    private int currentAllergy = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        englishAllergyName = findViewById(R.id.textView3);

        User currentUser = session.getUser();

        Thread t = new Thread() {
            public void run() {
                allergies = allergyDao.getUserAllergies(currentUser.uid);
            }
        };
        t.start();
        try { t.join(); } catch (InterruptedException e){ e.printStackTrace(); }

        setAllergyText();
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
        finish();
        startActivity(i);
    }

    // GO TO ADD QR PAGE
    public void QRpage(View view) {
        Intent i=new Intent(getBaseContext(),QRGenerator.class);
        finish();
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

}
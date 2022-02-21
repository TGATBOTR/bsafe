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
                retrieved = true;
            }
        };
        t.start();

        while (!retrieved){
        }
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        findViewById(R.id.relativeLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.right).setVisibility(View.VISIBLE);
        findViewById(R.id.left).setVisibility(View.VISIBLE);
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
            currentAllergy = currentAllergy % allergies.size();
            setAllergyText();
        }
    }

    // UPDATE TEXT ON SCREEN
    // will change to swipe gesture, may not need
    private void setAllergyText(){
        String text;
        if(allergies.size() != 0){
            text = allergies.get(currentAllergy).name;
        } else {
            text = "none";
        }
        englishAllergyName.setText(text);
    }

    // GO TO ADD ALLERGY PAGE
    public void addAllergy(View view) {
        Intent i=new Intent(getBaseContext(),AddAllergyActivity.class);
        finish();
        startActivity(i);
    }
}
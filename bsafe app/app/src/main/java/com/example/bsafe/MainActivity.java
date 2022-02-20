package com.example.bsafe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.bsafe.Auth.Session;
import com.example.bsafe.Database.Daos.AllergyDao;
import com.example.bsafe.Database.Models.Allergy;
import com.example.bsafe.Database.Models.User;

import org.w3c.dom.Text;

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

//        TextView label = (TextView) findViewById(R.id.label);
//        label.setText("Hello, " + currentUser.firstName);

        Thread t = new Thread() {
            public void run() {
                allergies = allergyDao.getUserAllergies(currentUser.uid);
                retrieved = true;
            }
        };
        t.start();

        while (!retrieved){
        }
        setAllergyText();
    }

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

    private void setAllergyText(){
        String text;
        if(allergies.size() != 0){
            text = allergies.get(currentAllergy).name;
        } else {
            text = "none";
        }
        englishAllergyName.setText(text);
    }

}
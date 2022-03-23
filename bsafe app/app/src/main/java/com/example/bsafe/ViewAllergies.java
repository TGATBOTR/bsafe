package com.example.bsafe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.bsafe.Auth.Session;
import com.example.bsafe.Database.Daos.AllergyDao;
import com.example.bsafe.Database.Models.Allergy;
import com.example.bsafe.Database.Models.EmergencyContacts;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ViewAllergies extends AppCompatActivity implements SearchView.OnQueryTextListener{

    @Inject
    public Session session;
    @Inject
    public AllergyDao allergyDao;

    private List<Allergy> allergies;

    private LinearLayout layout;
    private SearchView searchView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_allergies);

        Thread t = new Thread() {
            public void run() {
                allergies = allergyDao.getUserAllergies(session.getUser().uid);
            }
        };
        t.start();
        try { t.join(); } catch (InterruptedException e){ e.printStackTrace(); }

        ((TextView) findViewById(R.id.textView6)).setText("Allergies");
        searchView = findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(this);

        layout = findViewById(R.id.linearlayout);
        setList(allergies);

    }


    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchList(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchList(newText);
        return false;
    }

    private void searchList(String query){
        layout.removeAllViews();
        List<Allergy> allergiesList= new ArrayList<>(allergies);
        for (Allergy allergy : allergies){
            String contactName = allergy.name.toLowerCase();
            if (!contactName.contains(query.toLowerCase())){
                allergiesList.remove(allergy);
            }
        }
        setList(allergiesList);
    }

    @SuppressLint("SetTextI18n")
    private void setList(List<Allergy> allergyList){
        for (int i = 0; i < allergyList.size(); i++){
            // ROW
            AllergyView horizontalLayout = new AllergyView(this, i);
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
            horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(50)));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);

<<<<<<< HEAD
            //Country
            TextView country = new TextView(this);
            country.setText(allergyList.get(i).name);
            country.setTextSize(20);
            country.setLayoutParams(params);
            country.setGravity(Gravity.CENTER);
            horizontalLayout.addView(country);

            //Number
            TextView number = new TextView(this);
            number.setText("translate");
            number.setTextSize(20);
            number.setLayoutParams(params);
            number.setGravity(Gravity.CENTER);
            horizontalLayout.addView(number);
=======
            //ENGLISH TEXT
            // TODO: CHANGE AFTER TRANSLATE API. getNativeLanguage() METHOD
            TextView english = new TextView(this);
            english.setText(allergies.get(i).name);
            english.setTextSize(20);
            english.setLayoutParams(params);
            english.setGravity(Gravity.CENTER);
            horizontalLayout.addView(english);

            //TRANSLATED TEXT
            TextView translate = new TextView(this);
            TranslationAPI translateTask = new TranslationAPI(MainActivity.targetLanguage, allergies.get(i).name, new OnTaskCompleted() {
                @Override
                void onTaskCompleted(String translation) {
                    translate.setText(translation);
                }
            });
            translateTask.execute();

            english.setTextSize(20);
            translate.setLayoutParams(params);
            translate.setGravity(Gravity.CENTER);
            horizontalLayout.addView(translate);

            layout.addView(horizontalLayout);
        }
    }
}

/*
Custom class to make an array of linearlayouts clickable
 */
class AllergyView extends LinearLayout{
    int index;

    public AllergyView(Context context, int i){
        super(context);
        this.index = i;
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,UpdateAllergyActivity.class);

                intent.putExtra("index", index);

                context.startActivity(intent);
            }
        });
    }
}
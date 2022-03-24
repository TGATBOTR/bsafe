package com.example.bsafe;

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

import androidx.appcompat.app.AppCompatActivity;

import com.example.bsafe.Auth.Session;
import com.example.bsafe.Database.Daos.EmergencyContactsDao;
import com.example.bsafe.Database.Models.EmergencyContacts;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ViewContacts extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private LinearLayout layout;

    @Inject
    public Session session;
    @Inject
    public EmergencyContactsDao emergencyContactsDao;

    private List<EmergencyContacts> emergencyContacts;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_allergies);

        Thread t = new Thread() {
            public void run() {
                emergencyContacts = emergencyContactsDao.getAll();
            }
        };
        t.start();
        try { t.join(); } catch (InterruptedException e){ e.printStackTrace(); }

        ((TextView) findViewById(R.id.textView6)).setText("Contacts");
        SearchView searchView = findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(this);

        layout = findViewById(R.id.linearlayout);
        setList(emergencyContacts);

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
        List<EmergencyContacts> emergencyContactsList= new ArrayList<>(emergencyContacts);
        for (EmergencyContacts contact : emergencyContacts){
            String contactName = contact.name.toLowerCase();
            if (!contactName.contains(query.toLowerCase())){
                emergencyContactsList.remove(contact);
            }
        }
        setList(emergencyContactsList);
    }

    @SuppressLint("SetTextI18n")
    private void setList(List<EmergencyContacts> emergencyContactsList){
        for (int i = 0; i < emergencyContactsList.size(); i++){
            // ROW
            LinearLayout horizontalLayout = new LinearLayout(this);
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
            horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(50)));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);

            //Country
            TextView country = new TextView(this);
            country.setText(emergencyContactsList.get(i).name);
            country.setTextSize(20);
            country.setLayoutParams(params);
            country.setGravity(Gravity.CENTER);
            horizontalLayout.addView(country);

            //Number
            TextView number = new TextView(this);
            number.setText(emergencyContactsList.get(i).number.toString());
            number.setTextSize(20);
            number.setLayoutParams(params);
            number.setGravity(Gravity.CENTER);
            horizontalLayout.addView(number);

            layout.addView(horizontalLayout);
        }
    }
}

///*
//Custom class to make an array of linearlayouts clickable
// */
//class ContactView extends LinearLayout{
//    int index;
//
//    public ContactView(Context context, int i){
//        super(context);
//        this.index = i;
//        this.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(context,UpdateAllergyActivity.class);
//
//                intent.putExtra("index", index);
//
//                context.startActivity(intent);
//            }
//        });
//    }
//}
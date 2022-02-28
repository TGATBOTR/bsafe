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
import android.widget.TextView;

import com.example.bsafe.Auth.Session;
import com.example.bsafe.Database.Daos.AllergyDao;
import com.example.bsafe.Database.Models.Allergy;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ViewAllergies extends AppCompatActivity {

    @Inject
    public Session session;
    @Inject
    public AllergyDao allergyDao;

    private List<Allergy> allergies;

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

        LinearLayout layout = findViewById(R.id.linearlayout);
        for (int i = 0; i < allergies.size(); i++){
            // ROW
            AllergyView horizontalLayout = new AllergyView(this, i);
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
            horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(50)));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);

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
            translate.setText("translated"); // TODO: CHANGE AFTER TRANSLATE API
            english.setTextSize(20);
            translate.setLayoutParams(params);
            translate.setGravity(Gravity.CENTER);
            horizontalLayout.addView(translate);

            layout.addView(horizontalLayout);
        }

    }


    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
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
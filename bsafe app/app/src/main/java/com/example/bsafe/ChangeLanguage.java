package com.example.bsafe;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bsafe.Auth.Session;
import com.example.bsafe.Database.Daos.UserDao;
import com.example.bsafe.Database.Models.User;
import com.example.bsafe.I18n.Localizer;
import com.webianks.library.scroll_choice.ScrollChoice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChangeLanguage extends Navigation {
    String language;
    ScrollChoice scrollChoice;
    TextView textView;

    Map<String, Locale> languages = new HashMap<>();





    @Inject
    public Localizer i18n;

    @Inject
    public Session session;

    @Inject
    public UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_languages);
        //ScrollChoice scrollChoice = (ScrollChoice) findViewById(R.id.scroll_choice);
        initViews();
        loadDatas();
    }

    private void loadDatas() {

        Locale l;
        l = new Locale("en");
        languages.put("English - " + i18n.get("LANGUAGE", l), l);
        l = new Locale("ro");
        languages.put("Romanian - " + i18n.get("LANGUAGE", l), l);
        l = new Locale("it");
        languages.put("Italian - " + i18n.get("LANGUAGE", l), l);
        l = new Locale("cy");
        languages.put("Welsh - " + i18n.get("LANGUAGE", l), l);

        List<String> datas = new ArrayList<>(languages.keySet());
        Collections.sort(datas);

        // Find the selected id
        int i = 0;
        int selected = 0;
        Locale userLocale = i18n.getLocale();

        for(String caption : datas) {
            if(languages.get(caption).equals(userLocale)) {
                selected = i;
            }
            i++;
        }

        scrollChoice.addItems(datas, selected);
    }

    private void initViews() {
        //textView = (TextView)findViewById(R.id.txt_result);
        scrollChoice = (ScrollChoice) findViewById(R.id.scroll_choice);
    }

    public synchronized Locale setLanguage(String language){
        this.language = language;

        Locale locale = this.languages.get(language);

        // Fail safe to prevent the locale being null
        locale = locale == null ? Locale.getDefault() : locale;

        // Save in the database
        User user = this.session.getUser();

        user.setLocale(locale);
        userDao.updateUsers(user);

        return locale;
    }
    public String getLanguage(){
        return this.language;
    }

    private class TaskUpdateLocale extends AsyncTask<String, Void, Locale> {

        @Override
        protected Locale doInBackground(String... params) {
            return setLanguage(params[0]);
        }

        @Override
        protected void onPostExecute(Locale result) {
            super.onPostExecute(result);

            // Set locale on the localizer
            i18n.setLocale(result);

            //GO BACK TO HOME PAGE
            onBackPressed();
            /*Intent i=new Intent(getBaseContext(),MainActivity.class);
            //finish();
            startActivity(i);*/
        }
    }


    public void editLanguage(View view) {
        new TaskUpdateLocale().execute(scrollChoice.getCurrentSelection());
    }
}

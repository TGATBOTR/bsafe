package com.example.bsafe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.webianks.library.scroll_choice.ScrollChoice;

import java.util.ArrayList;
import java.util.List;


public class ChangeLanguage extends AppCompatActivity {
    String language;
    List<String> datas = new ArrayList<>();
    ScrollChoice scrollChoice;
    TextView textView;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_languages);
        //ScrollChoice scrollChoice = (ScrollChoice) findViewById(R.id.scroll_choice);
        initViews();
        loadDatas();
        scrollChoice.addItems(datas,2);


    }

    private void loadDatas() {
        datas.add("English");
        datas.add("Spanish");
        datas.add("Russian");
        datas.add("Italian");


    }

    private void initViews() {
        //textView = (TextView)findViewById(R.id.txt_result);
        scrollChoice = (ScrollChoice) findViewById(R.id.scroll_choice);
    }

    public void setLanguage(String language){
        this.language = language;
        //System.out.println(language);
    }
    public String getLanguage(){
        return this.language;
    }

    public void editLanguage(View view) {
        setLanguage(scrollChoice.getCurrentSelection());
        //GO BACK TO HOME PAGE
        Intent i=new Intent(getBaseContext(),MainActivity.class);
        finish();
        startActivity(i);

    }
}

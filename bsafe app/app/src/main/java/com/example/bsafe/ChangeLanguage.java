package com.example.bsafe;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ChangeLanguage extends AppCompatActivity {
    public String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("here");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_languages);
        System.out.println("QUi");
    }

    public void setLanguage(String language){
        this.language = language;
    }
    public String getLanguage(){
        return this.language;
    }
}

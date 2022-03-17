package com.example.bsafe;

import android.os.AsyncTask;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;


public class TranslationAPI extends AsyncTask<String, String, String> {
    // just pasted in the API key for the prototype
    private static final String APIKey = "AIzaSyDYdZsUSJH4KKCjyHydYB_bWnSRW2k8-pU";
    private final String targetLang;
    private final String translateThis;

    private final OnTaskCompleted listener;

    public TranslationAPI(String targetLang,
                          String translateThis, OnTaskCompleted listener){ // params here
        this.targetLang = targetLang;
        this.translateThis = translateThis;
        this.listener=listener;
    }

    @Override
    protected String doInBackground(String... strings) {
        Translate translate = TranslateOptions.newBuilder().setApiKey(APIKey).build().getService();
        Translation translation = translate.translate(translateThis, Translate.TranslateOption.targetLanguage(targetLang));
        return translation.getTranslatedText();
    }

    @Override
    protected void onPostExecute(String s) {
        // call the listener in the other class
        listener.onTaskCompleted(s);
    }
}


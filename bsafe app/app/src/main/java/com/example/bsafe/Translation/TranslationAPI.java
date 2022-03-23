package com.example.bsafe.Translation;

import android.os.AsyncTask;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.util.HashMap;
import java.util.Map;


public class TranslationAPI extends AsyncTask<String, String, String> {
    // just pasted in the API key for the prototype
    private static final String APIKey = "AIzaSyBm8G6TOYa-wkZEfzUcXSZ-vn-jo21RSUY"; //"AIzaSyDYdZsUSJH4KKCjyHydYB_bWnSRW2k8-pU";
    private final String targetLang;
    private final String translateThis;


    private static Map<String, String> cache = new HashMap<String, String>();

    private final OnTaskCompleted listener;

    public static Map<String, String> targetLanguages;
    static {
        targetLanguages = new HashMap<>();
        targetLanguages.put("ENGLISH", "en");
        targetLanguages.put("ITALIAN", "it");
        targetLanguages.put("ROMANIAN", "ro");
        targetLanguages.put("WELSH", "cy");
    }

    public TranslationAPI(String targetLang,
                          String translateThis, OnTaskCompleted listener){ // params here
        this.targetLang = targetLang;
        this.translateThis = translateThis;
        this.listener=listener;
    }

    @Override
    protected String doInBackground(String... strings) {
        String lookup = targetLang + " " + translateThis;
        if(TranslationAPI.cache.containsKey(lookup)) {
            return TranslationAPI.cache.get(lookup);
        }


        Translate translate = TranslateOptions.newBuilder().setApiKey(APIKey).build().getService();
        Translation translation = translate.translate(translateThis, Translate.TranslateOption.targetLanguage(targetLang));

        TranslationAPI.cache.put(lookup, translation.getTranslatedText());

        return translation.getTranslatedText();
    }

    @Override
    protected void onPostExecute(String s) {
        // call the listener in the other class
        listener.onTaskCompleted(s);
    }
}


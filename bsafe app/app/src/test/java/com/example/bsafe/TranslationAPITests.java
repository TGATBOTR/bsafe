package com.example.bsafe;

import android.os.AsyncTask;
import android.widget.TextView;

import com.example.bsafe.I18n.Localizer;
import com.example.bsafe.Translation.OnTaskCompleted;
import com.example.bsafe.Translation.TranslationAPI;
import com.google.api.client.util.DateTime;

import org.junit.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;


public class TranslationAPITests
{
    @Test
    public void translatePhrase()
    {
        assert(true);
        String text = "peanuts";
        String expectedResult = "cacahuètes";
        final String[] result = { "" };

        TranslationAPI translateTask = new TranslationAPI(TranslationAPI.targetLanguages.get("FRENCH"), text, translation ->
        {
            result[0] = translation;
        });

        translateTask.execute();

        long start = System.currentTimeMillis();
        long end = 5 * 1000;
        while (translateTask.getStatus() == AsyncTask.Status.RUNNING || translateTask.getStatus() == AsyncTask.Status.PENDING)
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            assert(System.currentTimeMillis() - start >= end);
        }

        assert(result[0].equals(expectedResult));
    }
}

package com.example.bsafe.Utils;

import androidx.annotation.NonNull;

import com.example.bsafe.Database.Models.Allergy;
import com.example.bsafe.Translation.TranslationAPI;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class TranslationUtils
{
    public boolean TryTranslateAllergiesSynchronously(@NonNull List<Allergy> allergies, @NonNull String targetLanguage)
    {
        // TODO: Make this operate on a copy, avoiding the case of a half translated list of allergies
        for (Allergy allergy: allergies)
        {
            if (!TryTranslateAllergySynchronously(allergy, targetLanguage))
            {
                return false;
            }
        }
        return true;
    }

    public boolean TryTranslateAllergySynchronously(@NonNull Allergy allergy, @NonNull String targetLanguage)
    {
        TranslationAPI nameTask = new TranslationAPI(targetLanguage, allergy.name, (translation -> allergy.name = translation));
        TranslationAPI symptomsTask = new TranslationAPI(targetLanguage, allergy.symptoms, (translation -> allergy.symptoms = translation));

        Thread waitThread = new Thread(() ->
        {
            try
            {
                nameTask.execute().get();
                symptomsTask.execute().get();
            }
            catch (ExecutionException | InterruptedException e)
            {
                System.err.println("Error while translating " + allergy.name + " to " + targetLanguage);
                throw new RuntimeException();
            }
        });

        // Workaround for knowing if thread failed
        final boolean[] success = { true };
        waitThread.setUncaughtExceptionHandler((thread, throwable) -> success[0] = false);
        waitThread.start();

        try { waitThread.join(); }
        catch (InterruptedException e) { e.printStackTrace(); success[0] = false; }

        return success[0];
    }
}

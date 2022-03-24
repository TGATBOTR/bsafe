package com.example.bsafe.QRCodes;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bsafe.Auth.Session;
import com.example.bsafe.Database.Daos.AllergyDao;
import com.example.bsafe.Database.Models.Allergy;
import com.example.bsafe.I18n.Localizer;
import com.example.bsafe.MainActivity;
import com.example.bsafe.R;
import com.example.bsafe.Translation.TranslationAPI;
import com.example.bsafe.Utils.NetworkUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class QRGenerator extends AppCompatActivity {

    @Inject public Session session;
    @Inject public AllergyDao allergyDao;
    @Inject public Localizer i18n;
    @Inject public NetworkUtils networkUtils;

    private final int qrCodeSize = 500;
    private final List<Allergy> allergies = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // TODO: Probably move to translation class / similar
    private static final HashMap<String, Integer> allergyIds = new HashMap<String, Integer>()
    {{
        put("celery", 0);
        put("cereals", 1);
        put("crustaceans", 2);
        put("eggs", 3);
        put("fish", 4);
        put("lupin", 5);
        put("milk", 6);
        put("molluscs", 7);
        put("mustard", 8);
        put("nuts", 9);
        put("peanuts", 10);
        put("sesame seeds", 11);
        put("soya", 12);
        put("sulphites", 13);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_qr_code);

        int uid = session.getUser().uid;
        Thread getAllergiesThread = new Thread(() -> allergies.addAll(allergyDao.getUserAllergies(uid)));
        getAllergiesThread.start();
        try { getAllergiesThread.join(); } catch (InterruptedException e) { e.printStackTrace(); }

        if (allergies.size() == 0)
        {
            displayQR("");
            return;
        }

        // TODO: Show loading screen

        TranslationAPI headTask = null;
        for (Allergy allergy: allergies)
        {
            Action nextTask = (translationTask -> translationTask.execute());
            headTask = queueAllergyTranslation(headTask, allergy, MainActivity.targetLanguage, (head) ->
            {
                if (head == null)
                {
                    displayQR(getQRContent(allergies));
                    // TODO: Hide loading screen
                }
                else
                {
                    nextTask.invoke(head);
                }
            });
        }

        headTask.execute();
    }

    private TranslationAPI queueAllergyTranslation(TranslationAPI head, Allergy allergy, String targetLanguage, Action onCompleted)
    {
        TranslationAPI symptomsTask = new TranslationAPI(targetLanguage, allergy.symptoms, (translation ->
        {
            allergy.symptoms = translation;
            onCompleted.invoke(head);
        }));
        TranslationAPI nameTask = new TranslationAPI(targetLanguage, allergy.name, (translation ->
        {
            allergy.name = translation;
            symptomsTask.execute();
        }));

        return nameTask;
    }

    private void displayQR(String qrValue)
    {
        QRGEncoder qrgEncoder = new QRGEncoder(qrValue, null, QRGContents.Type.TEXT, qrCodeSize);

        Bitmap bitmap = qrgEncoder.getBitmap();
        if (bitmap == null)
        {
            // TODO: Replace with placeholder image for missing QR code
            bitmap = Bitmap.createBitmap(qrCodeSize, qrCodeSize, Bitmap.Config.ARGB_8888);
        }

        ImageView qrImage = findViewById(R.id.qrContainer);
        qrImage.setImageBitmap(bitmap);
    }

    @NonNull
    private String getQRContent(@NonNull List<Allergy> allergies)
    {
        // TODO: Translation goes here

        boolean online = networkUtils.deviceIsConnectedToInternet();
        return (online ? getQRLink(allergies) : getQRString(allergies));
    }

    @NonNull
    private String getQRLink(@NonNull List<Allergy> allergies)
    {
        String url = "https://tgatbotr.github.io/index.html";

        if (allergies.size() == 0)
        {
            return url;
        }

        Locale targetLocale = new Locale(MainActivity.targetLanguage);
        String[] theader = new String[]
        {
            i18n.get("ALLERGY", targetLocale),
            i18n.get("SEVERITY", targetLocale),
            i18n.get("SYMPTOMS", targetLocale),
            i18n.get("VIEW_ALLERGIES", targetLocale)
        };

        String[][] tcontent = new String[allergies.size()][4];
        for (int i = 0; i < allergies.size(); i++)
        {
            Allergy allergy = allergies.get(i);
            String allergyNameLower = allergy.name.toLowerCase();

            tcontent[i][0] = String.valueOf(allergyIds.containsKey(allergyNameLower) ? allergyIds.get(allergyNameLower) : -1);
            tcontent[i][1] = allergy.name;
            tcontent[i][2] = String.valueOf(allergy.scale);
            tcontent[i][3] = allergy.symptoms;
        }

        UrlData data = new UrlData();
        data.tHeader = theader;
        data.tContent = tcontent;

        String urlDataString = "";
        try
        {
            urlDataString = objectMapper.writeValueAsString(data);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }

        // Should be safe as project requires minimum API version 26 anyway
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            urlDataString = Base64.getUrlEncoder().encodeToString(urlDataString.getBytes());
        }
        else
        {
            urlDataString = "";
        }

        try
        {
            URL domainUrl = new URL(url);
            URL fullUrl = new URL(domainUrl, "?data=" + urlDataString);

            return fullUrl.toString();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        return url;
    }

    @NonNull
    private String getQRString(@NonNull List<Allergy> allergies)
    {
        StringBuilder qrString = new StringBuilder();

        for (Allergy allergy: allergies)
        {
            if (qrString.length() > 0)
            {
                qrString.append("____");
            }

            qrString.append('(');
            qrString.append(allergy.name);
            qrString.append("--");
            qrString.append(allergy.scale).append("/").append(10);
            qrString.append(')');
        }

        return qrString.toString();
    }

    private class UrlData
    {
        public String[] tHeader;
        public String[][] tContent;
    }
}

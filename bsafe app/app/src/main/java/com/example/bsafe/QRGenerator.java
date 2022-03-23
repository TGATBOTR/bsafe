package com.example.bsafe;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bsafe.Auth.Session;
import com.example.bsafe.Database.Daos.AllergyDao;
import com.example.bsafe.Database.Models.Allergy;
import com.example.bsafe.Utils.NetworkUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class QRGenerator extends AppCompatActivity {

    @Inject
    public Session session;
    @Inject
    public AllergyDao allergyDao;
    @Inject
    public NetworkUtils networkUtils;

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

        String qrValue = getQRContent(allergies);
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

        String[] theader = new String[] { "Allergy", "Severity", "Symptoms" };
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

        // Should be safe as project requires API v26 anyway
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

package com.example.bsafe;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bsafe.Auth.Session;
import com.example.bsafe.Database.Daos.AllergyDao;
import com.example.bsafe.Database.Models.Allergy;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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

    private final int qrCodeSize = 500;
    private final List<Allergy> allergies = new ArrayList<>();

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

        // TODO: detect if internet connection

        boolean online = true;
        return (online ? getQRLink(allergies) : getQRString(allergies));
    }

    @NonNull
    private String getQRLink(@NonNull List<Allergy> allergies)
    {
        if (allergies.size() == 0)
        {
            return "";
        }

        StringBuilder urlArgs = new StringBuilder();
        urlArgs.append("?allergies=");
        for (Allergy allergy: allergies)
        {
            urlArgs.append('+');
            urlArgs.append(allergy.name);
            urlArgs.append('+');
            urlArgs.append(allergy.scale);
            urlArgs.append('+');
            urlArgs.append(allergy.symptoms);
        }

        try
        {
            URL hostUrl = new URL("https", "tgatbotr.github.io", "index.html");
            URL fullUrl = new URL(hostUrl, urlArgs.toString());

            return fullUrl.toString();
        }
        catch (MalformedURLException e)
        {
            return "Nothing to see here...";
        }
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
            qrString.append(String.format("(%s--%d/%d--%s)", allergy.name, allergy.scale, 10, allergy.symptoms));
        }

        return qrString.toString();
    }
}

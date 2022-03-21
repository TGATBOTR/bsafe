package com.example.bsafe;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
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

        boolean online = isConnectedToInternet(getApplication());
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

            qrString.append('(');
            qrString.append(allergy.name);
            qrString.append("--");
            qrString.append(allergy.symptoms);
            qrString.append(')');
        }

        return qrString.toString();
    }

    private boolean isConnectedToInternet(@NonNull Application application)
    {
        // See https://stackoverflow.com/questions/57284582/networkinfo-has-been-deprecated-by-api-29
        boolean ret = false;

        ConnectivityManager connectivityManager = (ConnectivityManager)application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null)
            {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                ret = networkCapabilities != null
                        &&(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            }
        }
        else
        {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            ret = (networkInfo != null && networkInfo.isConnected());
        }

        return ret;
    }
}

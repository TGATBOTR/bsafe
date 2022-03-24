package com.example.bsafe;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Navigation extends AppCompatActivity {

    //Go Home
    public void home(View view){
        if (this.getClass() == MainActivity.class){
            return;
        }
        Intent i=new Intent(getBaseContext(),MainActivity.class);
        startActivity(i);
    }

    // GO TO ADD QR PAGE
    public void QRpage(View view) {
        if (this.getClass() == QRGenerator.class){
            return;
        }
        Intent i=new Intent(getBaseContext(),QRGenerator.class);
        startActivity(i);
    }

    //GO TO VIEW ALL ALLERGIES PAGE
    public void showAll(View view) {
        if (this.getClass() == ViewAllergies.class){
            return;
        }
        Intent i=new Intent(getBaseContext(),ViewAllergies.class);
        startActivity(i);
    }

    // GO TO CHANGE LANGUAGE PAGE
    public void changeLanguagePage(View view) {
        if (this.getClass() == ChangeLanguage.class){
            return;
        }
        Intent intent = new Intent(getBaseContext(), ChangeLanguage.class);
        //finish();
        startActivity(intent);

    }

    public void seeContacts(View view) {
        if (this.getClass() == ViewContacts.class){
            return;
        }
        Intent i=new Intent(getBaseContext(),ViewContacts.class);
        startActivity(i);
    }

}

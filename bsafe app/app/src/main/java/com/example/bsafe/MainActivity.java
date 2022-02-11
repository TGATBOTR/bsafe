package com.example.bsafe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bsafe.Database.DB;
import com.example.bsafe.Database.DBProvider;
import com.example.bsafe.Database.Daos.UserDao;
import com.example.bsafe.Database.Models.User;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    protected DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = DBProvider.get(this.getApplicationContext());
    }

    public void onSaveButtonClick(View view) {
        Thread t = new Thread() {
            public void run() {
                EditText edit = (EditText) findViewById(R.id.editTextTextPersonName);

                String name = edit.getText().toString();

                UserDao usersDB = db.userDao();

                User user = null;

                List<User> users = usersDB.getAll();

                Boolean insert = true;

                if(users.isEmpty()) {
                    user = new User();
                } else {
                    user = users.get(0);
                    insert = false;
                }

                user.firstName = name;
                user.lastName = "Not Used";

                if(!insert) {
                    usersDB.updateUsers(user);
                } else {
                    usersDB.insertAll(user);
                }
            }
        };

        t.start();
    }

    public void onLoadButtonClick(View view) {

        Thread t = new Thread() {
            public void run() {
                TextView label = (TextView) findViewById(R.id.labelOut);

                UserDao usersDB = db.userDao();

                User user = null;

                List<User> users = usersDB.getAll();

                if(users.isEmpty()) {
                    label.setText("No Data Saved");
                } else {
                    user = users.get(0);

                    label.setText(user.firstName);
                }
            }
        };

        t.start();

    }
}
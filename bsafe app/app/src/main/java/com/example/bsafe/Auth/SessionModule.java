package com.example.bsafe.Auth;


import com.example.bsafe.Database.Daos.UserDao;
import com.example.bsafe.Database.Models.User;

import java.util.List;

import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class SessionModule {

    private class GetUserThread extends Thread {
        public User user;
    }

    @Provides
    @Singleton
    public Session provideSession(UserDao userDao) {

        GetUserThread t = new GetUserThread() {
            public void run() {
                List<User> users = userDao.getAll();

                User user;

                if (users.isEmpty()) {
                    // Create new user
                    user = new User();
                    user.firstName = "Fred";
                    user.lastName = "Llewellyn";

                    userDao.insertAll(user);
                } else {
                    user = users.get(0);
                }

                this.user = user;
            }
        };

        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            // Do nothing as on main thread
        }

        Session session = new Session();

        session.login(t.user);

        return session;
    }
}

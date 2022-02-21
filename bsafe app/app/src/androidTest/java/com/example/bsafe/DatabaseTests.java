package com.example.bsafe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.bsafe.Auth.Session;
import com.example.bsafe.Database.DB;
import com.example.bsafe.Database.Daos.AllergyDao;
import com.example.bsafe.Database.Daos.UserDao;
import com.example.bsafe.Database.Models.Allergy;
import com.example.bsafe.Database.Models.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(AndroidJUnit4.class)
public class DatabaseTests {
    private TestContexts context = new TestContexts();

    private UserDao userDao;
    private AllergyDao allergyDao;
    private DB db;

    /**
     * Runs before each test
     */
    @Before
    public void createDb()
    {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, DB.class).build();
        userDao = db.userDao();
        allergyDao = db.allergyDao();
    }

    /**
     * Runs after each test
     */
    @After
    public void closeDb()
    {
        db.close();
    }

    @Test
    public void createAndReadUser()
    {
        Session session = context.createUserAndSession();
        User user = session.getUser();
        context.addNewUserToDb(user);

        User _user = context.readLastUserFromDb();
        assertEquals(user.uid, _user.uid);
        assertEquals(user.firstName, _user.firstName);
        assertEquals(user.lastName, user.lastName);
    }

    @Test
    public void createAndReadAllergy()
    {
        Session session = context.createUserAndSession();
        context.addNewUserToDb(session.getUser());
        Allergy allergy = context.addNewAllergyToDb(session.getUser());

        Allergy _allergy = context.readLastAllergyFromDb();
        assertEquals(allergy.uid, _allergy.uid);
        assertEquals(allergy.name, _allergy.name);
        assertEquals(allergy.scale, _allergy.scale);
        assertEquals(allergy.symptoms, _allergy.symptoms);
    }

    @Test
    public void updateAndReadAllergy()
    {
        Session session = context.createUserAndSession();
        context.addNewUserToDb(session.getUser());
        context.addNewAllergyToDb(session.getUser());

        // Update fields
        Allergy allergy = context.readLastAllergyFromDb();
        allergy.name = "rugs";
        allergy.scale = 4;
        allergy.symptoms = "acute organ hemorrhage";
        allergyDao.updateAll(allergy);

        Allergy _allergy = context.readLastAllergyFromDb();
        assertEquals(allergy.uid, _allergy.uid);
        assertEquals(allergy.name, _allergy.name);
        assertEquals(allergy.scale, _allergy.scale);
        assertEquals(allergy.symptoms, _allergy.symptoms);
    }

    @Test
    public void readAndDeleteAllergy()
    {
        Session session = context.createUserAndSession();
        context.addNewUserToDb(session.getUser());
        context.addNewAllergyToDb(session.getUser());

        Allergy allergy = context.readLastAllergyFromDb();
        allergyDao.delete(allergy);

        List<Allergy> allergies = allergyDao.getUserAllergies(session.getUser().uid);
        assertEquals(0, allergies.size());
    }

    private class TestContexts
    {
        public Session createUserAndSession()
        {
            User user = new User();
            user.firstName = "First_name";
            user.lastName = "Last_name";

            Session session = new Session();
            session.login(user);

            return session;
        }

        public void addNewUserToDb(User user)
        {
            userDao.insertAll(user);
        }

        public User readLastUserFromDb()
        {
            List<User> users = userDao.getAll();
            assert(users.size() > 0);
            User user = users.get(users.size() - 1);
            assertNotNull(user);

            return user;
        }

        public Allergy addNewAllergyToDb(User user)
        {
            Allergy allergy = new Allergy();
            allergy.name = "carpet";
            allergy.scale = 3;
            allergy.symptoms = "total organ failure";

            allergy.attachToUser(user);
            allergyDao.insertAll(allergy);

            return allergy;
        }

        public Allergy readLastAllergyFromDb()
        {
            List<Allergy> allergies = allergyDao.getAll();
            assert(allergies.size() > 0);
            Allergy allergy = allergies.get(allergies.size() - 1);
            assertNotNull(allergy);

            return allergy;
        }
    }
}
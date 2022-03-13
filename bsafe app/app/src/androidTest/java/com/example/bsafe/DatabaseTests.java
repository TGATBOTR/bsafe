package com.example.bsafe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.bsafe.Auth.Session;
import com.example.bsafe.Database.DB;
import com.example.bsafe.Database.Daos.AllergyDao;
import com.example.bsafe.Database.Daos.UserDao;
import com.example.bsafe.Database.Models.Allergy;
import com.example.bsafe.Database.Models.User;
import com.example.bsafe.I18n.Localizer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Locale;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(AndroidJUnit4.class)
public class DatabaseTests {
    private final TestUtils utils = new TestUtils();
    private Session session;
    private User user;
    private UserDao userDao;
    private AllergyDao allergyDao;
    private DB db;

    /**
     * Runs before each test
     */
    @Before
    public void createDb()
    {
        Context appContext = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(appContext, DB.class).build();
        userDao = db.userDao();
        allergyDao = db.allergyDao();
        session = this.utils.createSession();
        user = session.getUser();
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
        utils.addUserToDb(user);

        User _user = utils.readLastUserFromDb();
        assertEquals(user.firstName, _user.firstName);
        assertEquals(user.lastName, user.lastName);
    }

    @Test
    public void createAndReadMultipleUsers()
    {
        utils.addUserToDb(user);
        utils.addUserToDb(new User());

        List<User> users = userDao.getAll();
        assert(users.size() >= 2);

        assertNotEquals(users.get(0).uid, users.get(1).uid);
    }

    @Test
    public void readAndUpdateUser()
    {
        utils.addUserToDb(user);

        // Update fields
        user.firstName = "New_name";
        user.lastName = "New_lastname";
        userDao.updateUsers(user);

        User _user = utils.readLastUserFromDb();
        assertEquals(user.firstName, _user.firstName);
        assertEquals(user.lastName, _user.lastName);
    }

    @Test
    public void readAndDeleteUser()
    {
        utils.addUserToDb(user);

        userDao.delete(user);

        List<User> users = userDao.getAll();
        assertEquals(0, users.size());
    }

    @Test
    public void createAndReadAllergy()
    {
        utils.addUserToDb(user);
        Allergy allergy = utils.addNewAllergyToDb(user);

        Allergy _allergy = utils.readLastAllergyFromDb();
        assertEquals(allergy.name, _allergy.name);
        assertEquals(allergy.scale, _allergy.scale);
        assertEquals(allergy.symptoms, _allergy.symptoms);
    }

    @Test
    public void createAndReadAllergyLinkedToUser()
    {
        utils.addUserToDb(user);
        Allergy allergy = utils.addNewAllergyToDb(user);

        Allergy _allergy = utils.readLastAllergyFromDb(user);
        assertEquals(allergy.name, _allergy.name);
        assertEquals(allergy.scale, _allergy.scale);
        assertEquals(allergy.symptoms, _allergy.symptoms);
    }

    @Test
    public void createAndReadMultipleAllergies()
    {
        utils.addUserToDb(user);
        utils.addNewAllergyToDb(user);
        utils.addNewAllergyToDb(user);

        List<Allergy> allergies = allergyDao.getAll();
        assert(allergies.size() >= 2);

        assertNotEquals(allergies.get(0).uid, allergies.get(1).uid);
    }

    @Test
    public void readAndUpdateAllergy()
    {
        utils.addUserToDb(user);
        Allergy allergy = utils.addNewAllergyToDb(user);

        // Update fields
        allergy.name = "rugs";
        allergy.scale = 4;
        allergy.symptoms = "acute organ hemorrhage";
        allergyDao.updateAll(allergy);

        Allergy _allergy = utils.readLastAllergyFromDb();
        assertEquals(allergy.name, _allergy.name);
        assertEquals(allergy.scale, _allergy.scale);
        assertEquals(allergy.symptoms, _allergy.symptoms);
    }

    @Test
    public void readAndUpdateAllergyLinkedToUser()
    {
        utils.addUserToDb(user);
        Allergy allergy = utils.addNewAllergyToDb(user);

        // Update fields
        allergy.name = "rugs";
        allergy.scale = 4;
        allergy.symptoms = "acute organ hemorrhage";
        allergyDao.updateAll(allergy);

        Allergy _allergy = utils.readLastAllergyFromDb(user);
        assertEquals(allergy.name, _allergy.name);
        assertEquals(allergy.scale, _allergy.scale);
        assertEquals(allergy.symptoms, _allergy.symptoms);
    }

    @Test
    public void readAndDeleteAllergy()
    {
        utils.addUserToDb(user);
        utils.addNewAllergyToDb(user);

        Allergy allergy = utils.readLastAllergyFromDb(user);
        allergyDao.delete(allergy);

        List<Allergy> allergies = allergyDao.getAll();
        assertEquals(0, allergies.size());
    }

    private class TestUtils
    {
        @NonNull
        public Session createSession()
        {
            User user = new User();
            user.firstName = "First_name";
            user.lastName = "Last_name";

            Session session = new Session(new Localizer(Locale.getDefault()));
            session.login(user);

            return session;
        }

        public void addUserToDb(User user)
        {
            List<Long> ids = userDao.insertAll(user);
            user.uid = (ids.size() > 0 && ids.get(0) != null ? ids.get(0).intValue() : 0);
        }

        @NonNull
        public User readLastUserFromDb()
        {
            List<User> users = userDao.getAll();
            assert(users.size() > 0);
            User user = users.get(users.size() - 1);
            assertNotNull(user);

            return user;
        }

        @NonNull
        public Allergy addNewAllergyToDb(User user)
        {
            Allergy allergy = new Allergy();
            allergy.name = "carpet";
            allergy.scale = 3;
            allergy.symptoms = "total organ failure";

            allergy.attachToUser(user);

            // UID may change when allergy is inserted to db
            List<Long> ids = allergyDao.insertAll(allergy);
            allergy.uid = (ids.size() > 0 && ids.get(0) != null ? ids.get(0).intValue() : 0);

            return allergy;
        }

        @NonNull
        public Allergy readLastAllergyFromDb(User user)
        {
            List<Allergy> allergies = allergyDao.getUserAllergies(user.uid);
            assert(allergies.size() > 0);
            Allergy allergy = allergies.get(allergies.size() - 1);
            assertNotNull(allergy);

            return allergy;
        }

        @NonNull
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
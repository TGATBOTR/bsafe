package com.example.bsafe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.activity.ComponentActivity;
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
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseTests {
    private static Session session;
    private static UserDao userDao;
    private static AllergyDao allergyDao;
    private static DB db;

//    @Before
//    public void beforeTest()
//    {
//        // Run before every test
//    }

//    @After
//    public void afterTest()
//    {
//        // Run after every test
//    }

    private void createAndLoginUser()
    {
        User user = new User();
        user.firstName = "First_name";
        user.lastName = "Last_name";

        session = new Session();
        session.login(user);
    }

    @Test
    public void A_CreateDb()
    {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, DB.class).build();
        userDao = db.userDao();
        allergyDao = db.allergyDao();
        createAndLoginUser();

        assertNotNull(db);
        assertNotNull(session);
        assertNotNull(session.getUser());
    }

    @Test
    public void B_CreateAndReadUser()
    {
        User user = session.getUser();

        // Create new user
        userDao.insertAll(user);

        // Read user to check creation was successful
        User _user = userDao.getUserById(user.uid);
        assertNotNull(_user);
        assertEquals(user.uid, _user.uid);
        assertEquals(user.firstName, _user.firstName);
        assertEquals(user.lastName, user.lastName);
    }

    @Test
    public void C_CreateAndReadAllergy()
    {
        User user = session.getUser();

        // Create allergy
        Allergy allergy = new Allergy();
        allergy.name = "carpet";
        allergy.scale = 3;
        allergy.symptoms = "total organ failure";
        allergy.attachToUser(user);
        allergyDao.insertAll(allergy);

        // Read allergy
        List<Allergy> allergies = allergyDao.getUserAllergies(user.uid);
        assertEquals(1, allergies.size());
        Allergy _allergy = allergies.get(0);

        assertNotNull(_allergy);
        assertEquals(allergy.uid, _allergy.uid);
        assertEquals(allergy.name, _allergy.name);
        assertEquals(allergy.scale, _allergy.scale);
        assertEquals(allergy.symptoms, _allergy.symptoms);

        System.out.printf("Size after: %d%n", allergyDao.getUserAllergies(user.uid).size());
    }

    @Test
    public void D_UpdateAndReadAllergy()
    {
        User user = session.getUser();

        // Read current allergy
        List<Allergy> allergies = allergyDao.getUserAllergies(user.uid);
        assertEquals(1, allergies.size());
        Allergy allergy = allergies.get(0);
        assertNotNull(allergy);

        // Update allergy fields
        allergy.name = "rugs";
        allergy.scale = 4;
        allergy.symptoms = "acute organ hemorrhage";
        allergyDao.updateAll(allergy);

        // Read allergy again to check update was successful
        allergies = allergyDao.getUserAllergies(user.uid);
        assertEquals(1, allergies.size());
        Allergy _allergy = allergies.get(0);

        assertNotNull(_allergy);
        assertEquals(allergy.uid, _allergy.uid);
        assertEquals(allergy.name, _allergy.name);
        assertEquals(allergy.scale, _allergy.scale);
        assertEquals(allergy.symptoms, _allergy.symptoms);
    }

    @Test
    public void E_ReadAndDeleteAllergy()
    {
        User user = session.getUser();

        // Read to check content exists
        List<Allergy> allergies = allergyDao.getUserAllergies(user.uid);
        System.out.printf("Num allergies: %d%n", allergies.size());
        assertEquals(allergies.size(), 1);
        Allergy allergy = allergies.get(0);

        // Delete allergy
        allergyDao.delete(allergy);

        // Check allergy has been deleted
        allergies = allergyDao.getUserAllergies(user.uid);
        assertEquals(allergies.size(), 0);
    }
}
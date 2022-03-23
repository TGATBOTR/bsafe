package com.example.bsafe.Database.Daos;

        import androidx.room.Dao;
        import androidx.room.Delete;
        import androidx.room.Insert;
        import androidx.room.Query;
        import androidx.room.Update;

        import com.example.bsafe.Database.Models.Allergy;
        import com.example.bsafe.Database.Models.EmergencyContacts;
        import com.example.bsafe.Database.Models.User;

        import java.util.List;

@Dao
public interface EmergencyContactsDao {
    @Query("SELECT * FROM emergencyContacts")
    List<EmergencyContacts> getAll();

    @Query("SELECT * FROM emergencyContacts WHERE name = :name")
    List<EmergencyContacts>  searchByName(String name);

    @Insert
    void insertAll(EmergencyContacts... emergencyContacts);
//
//    @Update
//    public void updateAll(Allergy... allergies);
//
//    @Delete
//    void delete(Allergy allergy);
}

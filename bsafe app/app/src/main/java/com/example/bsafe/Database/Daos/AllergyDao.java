package com.example.bsafe.Database.Daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bsafe.Database.Models.Allergy;
import com.example.bsafe.Database.Models.User;

import java.util.List;

public interface AllergyDao {
    @Query("SELECT * FROM allergies")
    List<Allergy> getAll();

    @Query("SELECT * FROM allergies WHERE user_id = :uid")
    void getUserAllergies(Integer user_id);

    @Insert
    void insertAll(Allergy... allergies);

    @Update
    public void updateUsers(Allergy... allergies);

    @Delete
    void delete(Allergy allergy);
}

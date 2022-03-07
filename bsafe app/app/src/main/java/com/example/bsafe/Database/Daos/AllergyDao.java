package com.example.bsafe.Database.Daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bsafe.Database.Models.Allergy;
import com.example.bsafe.Database.Models.User;

import java.util.List;

@Dao
public interface AllergyDao {
    @Query("SELECT * FROM allergies")
    List<Allergy> getAll();

    @Query("SELECT * FROM allergies WHERE user_id = :user_id")
    List<Allergy> getUserAllergies(Integer user_id);

    @Insert
    List<Long> insertAll(Allergy... allergies);

    @Update
    public void updateAll(Allergy... allergies);

    @Delete
    void delete(Allergy allergy);
}

package com.example.bsafe.Database.Daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bsafe.Database.Models.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users")
    List<User> getAll();

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    User getUserById(Integer uid);

    @Insert
    List<Long> insertAll(User... users);

    @Update
    public void updateUsers(User... users);

    @Delete
    void delete(User user);


}

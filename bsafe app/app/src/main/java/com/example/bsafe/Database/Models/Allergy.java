package com.example.bsafe.Database.Models;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Represents an allergy of a user
 */
@Entity(
        tableName = "allergies",
        foreignKeys = {
                @ForeignKey(entity = User::class,
                        parentColumns = "uid",
                        childColumns = "user_id",
                        onDelete = ForeignKey.CASCADE // Delete child allergies when user is deleted
                )
        }
)
public class Allergy {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "user_id")
    public Integer userId;

    @ColumnInfo(name = "scale")
    public Integer scale;

    @ColumnInfo(name = "symptoms")
    public String symptoms;

    public void attachToUser(User user)
    {
        this.userId = user.uid;
    }
}

package com.example.bsafe.Database.Models;


        import androidx.room.ColumnInfo;
        import androidx.room.Entity;
        import androidx.room.PrimaryKey;

/**
 * Represents an allergy of a user
 */
@Entity(
        tableName = "emergencyContacts"
)
public class EmergencyContacts {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "number")
    public Integer number;

}

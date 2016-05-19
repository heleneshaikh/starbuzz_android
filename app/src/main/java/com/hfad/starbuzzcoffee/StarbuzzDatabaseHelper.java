package com.hfad.starbuzzcoffee;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StarbuzzDatabaseHelper extends SQLiteOpenHelper {

    //DB NEEDS A NAME & VERSION
    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "starbuzz";

    //1. CREATE DB
    public StarbuzzDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //APP IS RUN FOR THE FIRST TIME
    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);
    }

    //SQL helper > old version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //oldVersion => current version
        updateMyDatabase(db, oldVersion, newVersion);
    }

    public static void insertDrink(SQLiteDatabase db, String name, String description, int image_id) {
        //CREATE DATA
        ContentValues drinkValues = new ContentValues();
        drinkValues.put("NAME", name);
        drinkValues.put("DESCRIPTION", description);
        drinkValues.put("IMAGE_RESOURCE_ID", image_id);

        //INSERT DATA INTO TABLE
        db.insert("DRINK", null, drinkValues);
    }

    public void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        //CREATE TABLE
        if (oldVersion < 1) {
            db.execSQL("CREATE TABLE DRINK ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "NAME TEXT,"
                    + "DESCRIPTION TEXT, "
                    + "IMAGE_RESOURCE_ID INTEGER);");

            insertDrink(db, "Latte", "The procedure is a virtual spacecraft.", R.drawable.latte);
            insertDrink(db, "Espresso", "Jolly roger, yer not commanding me without a pestilence!", R.drawable.espresso);
            insertDrink(db, "Cappuccino", "The alien is mechanically final.", R.drawable.cappuccino);
        }

        //ADD COLUMN
        if (oldVersion <= 2) {
            db.execSQL("ALTER TABLE DRINK ADD COLUMN FAVORITE NUMERIC");
        }
    }



}

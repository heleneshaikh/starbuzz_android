package com.hfad.starbuzzcoffee;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DrinkActivity extends AppCompatActivity {
    public static final String EXTRA_DRINKNO = "drinkNo";
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);

        // 1| Get the drink that was clicked
        index = (int) getIntent().getExtras().get(EXTRA_DRINKNO); //id of item that was clicked

        try {
            SQLiteOpenHelper starbuzzDatabaseHelper = new StarbuzzDatabaseHelper(this);
            SQLiteDatabase db = starbuzzDatabaseHelper.getWritableDatabase();
            Cursor cursor = db.query("DRINK",
                    new String[]{"NAME", "DESCRIPTION", "IMAGE_RESOURCE_ID", "FAVORITE"},
                    "_id = ?",
                    new String[]{Integer.toString(index)},
                    null, null, null
            );

            if (cursor.moveToFirst()) { //in the right order!
                String nameText = cursor.getString(0);
                String descriptionText = cursor.getString(1);
                int photoId = cursor.getInt(2);

                TextView name = (TextView) findViewById(R.id.name);
                if (name != null) {
                    name.setText(nameText);
                }

                TextView description = (TextView) findViewById(R.id.description);
                if (description != null) {
                    description.setText(descriptionText);
                }

                ImageView image = (ImageView) findViewById(R.id.photo);
                if (image != null) {
                    image.setImageResource(photoId);
                    image.setContentDescription(nameText);
                }

                CheckBox favorite = (CheckBox) findViewById(R.id.favorite);
                if (favorite != null) {
                    boolean isFavorite = (cursor.getInt(3) == 1);
                    favorite.setChecked(isFavorite);
                }
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    public void onFavoriteClicked(View view) {
        index = (int) getIntent().getExtras().get(EXTRA_DRINKNO);
        new UpdateDrinkTask().execute(index);

        /*
        CheckBox favorite = (CheckBox) findViewById(R.id.favorite);
        ContentValues values = new ContentValues();
        values.put("FAVORITE", favorite.isChecked());

        try {
            SQLiteOpenHelper starbuzzDatabaseHelper = new StarbuzzDatabaseHelper(this);
            SQLiteDatabase db = starbuzzDatabaseHelper.getWritableDatabase();
            db.update("DRINK", values, "_id=?", new String[]{Integer.toString(index)});
            db.close();
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        */
    }

    private class UpdateDrinkTask extends AsyncTask<Integer, Void, Boolean> {
        ContentValues values;

        @Override
        protected void onPreExecute() {
            CheckBox favorite = (CheckBox) findViewById(R.id.favorite);
            values = new ContentValues();
            values.put("FAVORITE", favorite.isChecked());
        }

        protected Boolean doInBackground(Integer... drinks) {
            int index = drinks[0];
            SQLiteOpenHelper starbuzzDatabaseHelper = new StarbuzzDatabaseHelper(DrinkActivity.this);
            try {
                SQLiteDatabase db = starbuzzDatabaseHelper.getWritableDatabase();
                db.update("DRINK", values, "_id=?", new String[]{Integer.toString(index)});
                db.close();
                return true;
            } catch (SQLiteException e) {
                return false;
            }
        }

        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast toast = Toast.makeText(DrinkActivity.this, "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}

package com.hfad.starbuzzcoffee;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class TopLevelActivity extends AppCompatActivity {
    private Cursor cursor;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_level);
        ListView favList = (ListView) findViewById(R.id.list_fav);

        //event listener for when you click on <item>
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { //if user clicks on drinks item, open drinkcategory
                    Intent intent = new Intent(TopLevelActivity.this, DrinkCategoryActivity.class);
                    startActivity(intent);
                }
            }
        };

        //link together event listener & view
        ListView listView = (ListView) findViewById(R.id.list_options);
        if (listView != null) {
            listView.setOnItemClickListener(itemClickListener);
        }

        try {
            SQLiteOpenHelper starbuzzDBHelper = new StarbuzzDatabaseHelper(this);
            db = starbuzzDBHelper.getReadableDatabase();
            cursor = db.query("DRINK",
                    new String[]{"_id", "NAME"},
                    "FAVORITE=1",
                    null, null, null, null);

            CursorAdapter adapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1,
                    cursor,
                    new String[]{"NAME"},
                    new int[]{android.R.id.text1},
                    0);
            if (favList != null) {
                favList.setAdapter(adapter);
            }
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TopLevelActivity.this, DrinkActivity.class);
                intent.putExtra(DrinkActivity.EXTRA_DRINKNO, (int) id);
                startActivity(intent);
            }
        };
        if (favList != null) {
            favList.setOnItemClickListener(clickListener);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();

        try {
            SQLiteOpenHelper starbuzzDBHelper = new StarbuzzDatabaseHelper(this);
            db = starbuzzDBHelper.getReadableDatabase();
            Cursor newCursor = db.query("DRINK",
                    new String[]{"_id", "NAME"},
                    "FAVORITE=1",
                    null, null, null, null);
            ListView favList = (ListView) findViewById(R.id.list_fav);
            CursorAdapter adapter = (CursorAdapter) favList.getAdapter();
            adapter.changeCursor(newCursor);
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }
}

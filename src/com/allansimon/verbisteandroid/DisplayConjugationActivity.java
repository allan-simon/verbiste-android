package com.allansimon.verbisteandroid;

import com.allansimon.verbisteandroid.MainActivity;
import com.allansimon.verbisteandroid.ExternalDbOpenHelper;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.content.Intent;

import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TableLayout;
import android.widget.TextView;

import android.graphics.Color;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import android.util.Log;

public class DisplayConjugationActivity extends ActionBarActivity
{
    private static final String DB_NAME = "conjugation.db";

    private SQLiteDatabase database;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conjugation);

        String verb = getVerb();

        TableLayout tableLayout = (TableLayout) findViewById(
            R.id.conjugation_table
        );

        Cursor cursor = getConjugationsOf(verb);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            TableRow row = new TableRow(this);

            String radical = cursor.getString(0);
            TextView radicalView = new TextView(this);
            radicalView.setText(radical);
            row.addView(radicalView);


            String suffix = cursor.getString(1);
            TextView suffixView = new TextView(this);
            suffixView.setTextColor(Color.RED);
            suffixView.setText(suffix);
            row.addView(suffixView);

            tableLayout.addView(row);

        }
        cursor.close();

    }

    /**
     *
     */
    private Cursor getConjugationsOf(String verb)
    {
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(
            this,
            DB_NAME
        );
        database = dbOpenHelper.openDataBase();

        String[] params = {verb};
        //TODO replace rawQuery by call to the right methods
        return database.rawQuery(
            //concatenation of static strings is optimized at compile
            //times, so no need for string building ;-)
            "SELECT " +
            "    radical, " +
            "    suffix " +
            "FROM verb v " +
            "JOIN verb_type t ON v.verb_type_id = t.id " +
            "JOIN conjugation c ON v.verb_type_id = c.verb_type_id " +
            "WHERE v.infinitive = ?;",
            params
        );

    }

    /**
     *
     */
    private String getVerb()
    {
        Intent intent = getIntent();
        return intent.getStringExtra(MainActivity.EXTRA_VERB);
    }
}

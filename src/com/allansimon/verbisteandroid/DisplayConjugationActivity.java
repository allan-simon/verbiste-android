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

        int previousMode = -1;
        int previousTense = -1;
        Cursor cursor = getConjugationsOf(verb);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            // insert a new line after a new mode
            if (previousMode != cursor.getInt(3)) {
                TableRow row = new TableRow(this);
                TextView empty = new TextView(this);
                empty.setText(" ");
                row.addView(empty);
                tableLayout.addView(row);
                previousMode = cursor.getInt(3);
            }

            // insert a new line after a new tense
            if (previousTense != cursor.getInt(4)) {
                TableRow row = new TableRow(this);
                TextView empty = new TextView(this);
                empty.setText(" ");
                row.addView(empty);
                tableLayout.addView(row);
                previousTense = cursor.getInt(4);
            }

            TableRow row = new TableRow(this);

            String person = cursor.getString(2);
            TextView personView = new TextView(this);
            personView.setText(person + " ");
            row.addView(personView);

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
            "    suffix, " +
            "    CASE h_aspired " +
            "        WHEN 0 THEN p.base " +
            "        ELSE p.with_h_aspired " +
            "    END as person_text, " +
            "    mode, " +
            "    tense " +
            "FROM verb v " +
            "JOIN verb_type t ON v.verb_type_id = t.id " +
            "JOIN conjugation c ON v.verb_type_id = c.verb_type_id " +
            "JOIN person p ON p.id = c.person " +
            "WHERE v.infinitive = ? " +
            "ORDER BY mode, tense, person;",
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

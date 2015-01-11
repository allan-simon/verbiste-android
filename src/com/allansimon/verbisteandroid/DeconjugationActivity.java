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

import android.view.LayoutInflater;

import android.graphics.Color;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import android.util.Log;



public class DeconjugationActivity extends ActionBarActivity
{
    private static final String DB_NAME = "conjugation.db";

    private SQLiteDatabase database;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deconjugation);
        TableLayout tableLayout = (TableLayout) findViewById(
            R.id.deconjugation_table
        );

        String verb = getVerb();

        Cursor cursor = getDeconjugationsOf(verb);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            TableRow row = new TableRow(this);

            String mode = cursor.getString(0);
            addTextColumn(row, mode + " ");

            String tense = cursor.getString(1);
            addTextColumn(row, tense + " ");

            String person = cursor.getString(2);
            addTextColumn(row, person);

            tableLayout.addView(row);
        }

    }

    private void addTextColumn(TableRow row, String text)
    {
        LayoutInflater inflater = getLayoutInflater();
        TextView view = (TextView) inflater.inflate(R.layout.text_view, null);
        view.setText(text);
        row.addView(view);
    }

    /**
     *
     */
    private Cursor getDeconjugationsOf(String verb)
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
            "    infinitive, " +
            "    m.text as mode_text, " +
            "    t.text as tense_text, " +
            "    base as person_text " +
            "FROM conjugated_form cf " +
            "JOIN verb v ON v._id = cf.verb_id " +
            "JOIN conjugation c ON cf.conjugation_id = c.id " +
            "JOIN person p ON p.id = c.person " +
            "JOIN tense t ON t.id = c.tense " +
            "JOIN mode m ON m.id = c.mode " +
            "WHERE conjugated = ? " +
            "ORDER BY v._id;",
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

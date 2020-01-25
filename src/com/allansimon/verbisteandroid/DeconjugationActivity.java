package com.allansimon.verbisteandroid;

import com.allansimon.verbisteandroid.MainActivity;
import com.allansimon.verbisteandroid.ExternalDbOpenHelper;

import android.os.Build;
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

        String tenses[] = getResources().getStringArray(R.array.tenses);
        String modes[] = getResources().getStringArray(R.array.modes);

        Cursor cursor = getDeconjugationsOf(verb);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            TableRow row = new TableRow(this);

            String infinitive = cursor.getString(0);
            addTextColumn(row, infinitive + " ");

            int modeId = cursor.getInt(1);
            addTextColumn(row, modes[modeId] + " ");

            int tenseId = cursor.getInt(2);
            addTextColumn(row, tenses[tenseId] + " ");

            tableLayout.addView(row);
        }

    }

    private void addTextColumn(TableRow row, String text)
    {
        LayoutInflater inflater = getLayoutInflater();
        TextView view = (TextView) inflater.inflate(R.layout.text_view, null);
        if (Build.VERSION.SDK_INT >= 11) {
            view.setTextIsSelectable(true);
        }
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

        String[] params = {verb, verb};
        //TODO replace rawQuery by call to the right methods
        return database.rawQuery(
            //concatenation of static strings is optimized at compile
            //times, so no need for string building ;-)
            "SELECT " +
            "    infinitive, " +
            "    c.mode as mode, " +
            "    c.tense as tense, " +
            "    c.person as person " +
            "FROM conjugated_form cf " +
            "JOIN verb v ON v._id = cf.verb_id " +
            "JOIN conjugation c ON cf.conjugation_id = c.id " +
            "WHERE conjugated = ? OR conjugated_ascii = ? " +
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

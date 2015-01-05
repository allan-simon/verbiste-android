package com.allansimon.verbisteandroid;

import com.allansimon.verbisteandroid.MainActivity;
import com.allansimon.verbisteandroid.ExternalDbOpenHelper;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.content.Intent;

import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TableLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.view.LayoutInflater;

import android.graphics.Color;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.DatabaseUtils;

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

                addTextColumn(row, " ");
                tableLayout.addView(row);
                previousMode = cursor.getInt(3);
            }

            // insert a new line after a new tense
            if (previousTense != cursor.getInt(4)) {
                TableRow row = new TableRow(this);

                addTextColumn(row, " ");
                tableLayout.addView(row);
                previousTense = cursor.getInt(4);
            }

            TableRow row = new TableRow(this);

            String person = cursor.getString(2);
            addTextColumn(row, person + " ");


            String radical = cursor.getString(0);
            String suffix = cursor.getString(1);
            addConjugatedColumn(
                row,
                radical,
                suffix
            );

            tableLayout.addView(row);

        }
        cursor.close();

    }

    /**
     *
     */
    private void addTextColumn(
        TableRow row,
        String text
    ) {
        TextView view = createTextView();
        view.setText(text);
        row.addView(view);
    }

    /**
     *
     */
    private void addConjugatedColumn(
        TableRow row,
        String radical,
        String suffix
    ) {
        // TODO: certainly there's a better way to put radical and suffix
        // in same TextView using SpannedString
        LinearLayout linear = new LinearLayout(this);

        TextView radicalView = createTextView();
        radicalView.setText(radical);

        TextView suffixView = createTextView();
        suffixView.setTextColor(Color.RED);
        suffixView.setText(suffix);

        linear.addView(radicalView);
        linear.addView(suffixView);

        row.addView(linear);
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

        String infinitiveColumn = "infinitive";
        String[] params = {verb};

        // check if we can find a verb with that infinitive
        boolean foundInfinitive = 0 < DatabaseUtils.longForQuery(
            database,
            "SELECT COUNT(*) FROM verb WHERE infinitive= ?",
            params
        );

        // if we've not found the "original" infinitive
        // we will look for the ascii version of it,
        // in case the user as type the verb without accent
        if (!foundInfinitive) {
            infinitiveColumn = "infinitive_ascii";
        }

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
            "WHERE v." + infinitiveColumn + " = ? " +
            "ORDER BY mode, tense, person;",
            params
        );

    }

    /**
     *
     */
    private TextView createTextView()
    {
        LayoutInflater inflater = getLayoutInflater();
        return (TextView) inflater.inflate(R.layout.text_view, null);
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

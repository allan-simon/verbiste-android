package com.allansimon.verbisteandroid;

import com.allansimon.verbisteandroid.MainActivity;
import com.allansimon.verbisteandroid.ExternalDbOpenHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.content.Intent;

import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TableLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.view.ViewGroup;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import android.view.LayoutInflater;

import android.graphics.Color;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.DatabaseUtils;

import android.util.Log;

public class DisplayConjugationActivity extends ActionBarActivity
{
    private static final String DB_NAME = "conjugation.db";
    //TODO replace by a enum
    private static final int RADICAL_COLUMN = 0 ;
    private static final int SUFFIX_COLUMN = 1 ;
    private static final int PERSON_COLUMN = 2 ;
    private static final int MODE_COLUMN = 3 ;
    private static final int TENSE_COLUMN = 4 ;

    private SQLiteDatabase database;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conjugation);

        String verb = getVerb();

        LinearLayout linearLayout = (LinearLayout) findViewById(
            R.id.full_conjugation
        );
        String tenses[] = getResources().getStringArray(R.array.tenses);
        String modes[] = getResources().getStringArray(R.array.modes);

        // TODO it would be certainly better and easier to
        // understand if we were first looping on the cursor
        // to create an aggregate of verbs by tense by mode
        // and only then using this aggregate to build the layout
        int currentMode = -1;
        int currentTense = -1;
        Cursor cursor = getConjugationsOf(verb);
        TableLayout oneTenseTable = null;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            boolean isNewMode = currentMode != cursor.getInt(MODE_COLUMN);
            boolean IsNewTense =
                currentTense != cursor.getInt(TENSE_COLUMN) ||
                isNewMode;

            currentMode = cursor.getInt(MODE_COLUMN);
            currentTense = cursor.getInt(TENSE_COLUMN);

            if (IsNewTense && oneTenseTable != null) {
                linearLayout.addView(oneTenseTable);
            }

            // insert a new line after a new mode
            if (isNewMode) {
                TextView textView = createModeTitle(linearLayout);
                textView.setText(modes[currentMode]);
                linearLayout.addView(textView);

            }

            // insert a new line after a new tense
            if (IsNewTense) {
                TextView textView = createTenseTitle(linearLayout);
                textView.setText(tenses[currentTense]);
                linearLayout.addView(textView);
                oneTenseTable = createTenseLayout();
            }

            TableRow row = new TableRow(this);

            String person = cursor.getString(PERSON_COLUMN);
            addTextColumn(row, person + " ");

            String radical = cursor.getString(RADICAL_COLUMN);
            String suffix = cursor.getString(SUFFIX_COLUMN);
            addConjugatedColumn(
                row,
                radical,
                suffix
            );

            oneTenseTable.addView(row);

        }

        if (oneTenseTable != null) {
            linearLayout.addView(oneTenseTable);
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
        TextView view = createTextView();
        SpannableString text = new SpannableString(radical + suffix);
        text.setSpan(
            new ForegroundColorSpan(Color.RED),
            radical.length(),
            radical.length() + suffix.length(),
            0
        );

        view.setText(text);

        row.addView(view);
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

        String infinitiveColumn = "_id";

        String hash = String.valueOf(hash32Bits(verb));

        // check if we can find a verb with that infinitive
        boolean foundInfinitive = 0 < DatabaseUtils.longForQuery(
            database,
            "SELECT COUNT(*) FROM verb WHERE " + infinitiveColumn + " = " + hash,
            null
        );

        // if we've not found the "original" infinitive
        // we will look for the ascii version of it,
        // in case the user as type the verb without accent
        if (!foundInfinitive) {
            infinitiveColumn = "infinitive_ascii_hash";
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
            "WHERE v." + infinitiveColumn + " = " + hash + " " +
            "ORDER BY mode, tense, person;",
            null
        );

    }

    /**
     *
     */
    private TableLayout createTenseLayout()
    {
        TableLayout view = (TableLayout) getLayoutInflater().inflate(
            R.layout.conjugation_part_full_tense,
            null
        );
        return view;
    }

    /**
     *
     */
    private TextView createTextView()
    {
        TextView view = (TextView) getLayoutInflater().inflate(
            R.layout.text_view,
            null
        );
        return view;
    }

    /**
     *
     */
    private TextView createTenseTitle(ViewGroup container)
    {
        return (TextView) getLayoutInflater().inflate(
            R.layout.conjugation_part_tense_title,
            container,
            false
        );
    }

    /**
     *
     */
    private TextView createModeTitle(ViewGroup container)
    {
        return (TextView) getLayoutInflater().inflate(
            R.layout.conjugation_part_mode_title,
            container,
            false
        );
    }


    /**
     *
     */
    private String getVerb()
    {
        Intent intent = getIntent();
        String verb = intent.getStringExtra(MainActivity.EXTRA_VERB);
        return verb.trim();
    }

    /**
     *
     */
    private static long hash32Bits(String input)
    {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(input.getBytes());
            byte[] digest = messageDigest.digest();


            return convertFourBytesToLong(
                digest[12],
                digest[13],
                digest[14],
                digest[15]
            );

        } catch (NoSuchAlgorithmException e) {
            return 42;
        }
    }

    /**
     *
     */
    private static long convertFourBytesToLong(
        byte b1,
        byte b2,
        byte b3,
        byte b4
    ) {
        return (
            ((long)(b1 & 0xFF) << 24) |
            ((b2 & 0xFF) << 16) |
            ((b3 & 0xFF) << 8)  |
             (b4 & 0xFF)
        );
    }

}

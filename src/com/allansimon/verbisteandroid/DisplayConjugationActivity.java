package com.allansimon.verbisteandroid;

import com.allansimon.verbisteandroid.MainActivity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;

public class DisplayConjugationActivity extends ActionBarActivity
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String verb = intent.getStringExtra(MainActivity.EXTRA_VERB);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(verb);
        setContentView(textView);
    }


}

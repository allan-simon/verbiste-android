package com.allansimon.verbisteandroid;

import com.allansimon.verbisteandroid.DisplayConjugationActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;

public class MainActivity extends Activity
{
    public final static String EXTRA_VERB = "com.allansimon.verbisteandroid.MESSAGE";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void conjugate(View view)
    {
        Intent intent = new Intent(this, DisplayConjugationActivity.class);
        EditText enterVerb = (EditText) findViewById(R.id.enter_verb);
        String verb = enterVerb.getText().toString();
        intent.putExtra(EXTRA_VERB, verb);
        startActivity(intent);
    }
}

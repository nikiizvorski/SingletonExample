package com.androidchill.niki.singletonexample.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidchill.niki.singletonexample.R;
import com.androidchill.niki.singletonexample.fragment.AlertDialogFragment;
import com.androidchill.niki.singletonexample.model.Singleton;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    //FORMAT for the CountDownTimer
    private static final String FORMAT = "%02d:%02d:%02d";

    //SharePreferences SetUp
    SharedPreferences sharedpreferences;
    public static final String gamePrefs = "Game";
    public static final String username = "username";
    public static final String score = "0";

    //Set User
    Singleton object = Singleton.getInstance();

    //Variable for Score
    int foundmatch = 0;

    //Not using Butterknife since we can't change the package.
    Button submit, replay;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        //set the view since we can't use butterknife since
        // the package name starts with android or java
        submit = (Button) findViewById(R.id.button);
        replay = (Button) findViewById(R.id.button2);
        editText = (EditText) findViewById(R.id.editText);

        //Add Visibility to the replay button
        replay.setVisibility(View.INVISIBLE);

        //Set SharedPrefs for the User
        sharedpreferences = getSharedPreferences(gamePrefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(username, Singleton.getInstance().getUser().getName());
        editor.putString(score, Singleton.getInstance().getUser().getScore());
        editor.apply();

        //Welcome MSG from Singleton :)

        String msg = Singleton.getInstance().getString();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        final Animation shakeWrong = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_wrong);
        final Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        // View element to be shake
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);


        //Set the Timer
        timerStart();

        //Replay Button
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerStart();
                submit.setClickable(true);
                replay.setVisibility(View.INVISIBLE);
            }
        });

        //Submit UserInput button
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //get user data from the editText
                final String data = editText.getText().toString().trim();

                if(TextUtils.isEmpty(data)){
                    //editText.setError(getString(R.string.no_text_error));
                    // Perform animation
                    imageView.startAnimation(shakeWrong);
                } else {
                    // Perform animation
                    imageView.startAnimation(shake);
                    setResults(data);
                }
            }
        });

    }

    private void setResults(String data) {
        if(object.getCharCount().containsKey(data.charAt(0))){
            Toast.makeText(MainActivity.this, "KEY IN SINGLETON!", Toast.LENGTH_SHORT).show();
            ArrayList<String> list = object.getCharCount().get(data.charAt(0));
            //Toast.makeText(MainActivity.this, list + " ", Toast.LENGTH_SHORT).show();
            if(list.contains(data.trim())){
                Toast.makeText(MainActivity.this, "KEY FOUND WORD TOO!", Toast.LENGTH_SHORT).show();
                //add variable for the score
                foundmatch++;

                //store the score
                String scored = String.valueOf(foundmatch);

                //Actual Score of real user
                object.getUser().setScore(scored);

                //store the user data in the sharedpreferences
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(username, object.getUser().getName());
                editor.putString(score, object.getUser().getScore());
                editor.apply();
            } else {
                Toast.makeText(MainActivity.this, "KEY FOUND BUT NO WORD", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "NO KEY IN SINGLETON!", Toast.LENGTH_SHORT).show();
        }
    }

    //create CountDownTimer
    private void timerStart() {
        new CountDownTimer(31000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {

                getSupportActionBar().setTitle("Your Game Time: " + "" + String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                //preset all the buttons on finish
                getSupportActionBar().setTitle(R.string.gameOver);
                submit.setClickable(false);
                replay.setVisibility(View.VISIBLE);
                //show user score
                alertUserAboutScore();
            }
        }.start();
    }

    //Create dialog fragment for user score
    private void alertUserAboutScore() {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        alertDialogFragment.show(getFragmentManager(), "show_score");
    }
}

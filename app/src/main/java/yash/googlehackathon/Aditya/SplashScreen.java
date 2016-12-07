package yash.googlehackathon.Aditya;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;

import yash.googlehackathon.R;
import yash.googlehackathon.SignInActivity;

public class SplashScreen extends AppCompatActivity {
    HTextView hTextView;
    private static int SPLASH_TIME_OUT = 3000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        hTextView = (HTextView) findViewById(R.id.text);
        hTextView.setAnimateType(HTextViewType.LINE);
        hTextView.animateText("APP NAME"); // animate

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent i = new Intent(SplashScreen.this, SignInActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}

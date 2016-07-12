package com.app3roodk.UI.Feedback;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app3roodk.R;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        if (savedInstanceState == null) {
            FeedbackFragment fragment = new FeedbackFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.feedback_container, fragment)
                    .commit();
        }
    }
}

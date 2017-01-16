package com.jumpntrap.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TextView;

import com.jumpntrap.R;

/**
 * HelpActivity defines an activity to show details of the game.
 */
public final class HelpActivity extends AppCompatActivity {
    /**
     * Create the activity.
     * @param savedInstanceState the instance state to save.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //removing the action bar
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_help);

        final TextView aboutTextView = (TextView) findViewById(R.id.about_text_view);

        final String aboutText = getString(R.string.about_text);
        aboutTextView.setText(aboutText);
    }
}

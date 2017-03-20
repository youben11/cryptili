package com.youben.cryptili;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.toolBar_title_settings);

        getFragmentManager().beginTransaction().replace(android.R.id.content,new PrefSettings()).commit();
    }

    public static class PrefSettings extends PreferenceFragment{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.activity_settings);
        }
    }
}

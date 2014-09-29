package ca.appvelopers.mcgillmobile.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.base.DrawerActivity;
import ca.appvelopers.mcgillmobile.object.HomePage;
import ca.appvelopers.mcgillmobile.object.Language;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.util.Help;
import ca.appvelopers.mcgillmobile.util.Load;
import ca.appvelopers.mcgillmobile.util.Save;
import ca.appvelopers.mcgillmobile.view.HomePageAdapter;

public class SettingsActivity extends DrawerActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
        super.onCreate(savedInstanceState);

        GoogleAnalytics.sendScreen(this, "Settings");
        // Walkthrough


        //Help
        TextView helpIcon = (TextView)findViewById(R.id.help_icon);
        helpIcon.setTypeface(App.getIconFont());
        LinearLayout helpContainer = (LinearLayout)findViewById(R.id.settings_help);
        helpContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, HelpActivity.class));
            }
        });

        //About
        TextView aboutIcon = (TextView)findViewById(R.id.about_icon);
        aboutIcon.setTypeface(App.getIconFont());
        LinearLayout aboutContainer = (LinearLayout)findViewById(R.id.settings_about);
        aboutContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
            }
        });

        //Report a Bug
        TextView bugIcon = (TextView)findViewById(R.id.bug_icon);
        bugIcon.setTypeface(App.getIconFont());
        LinearLayout bugContainer = (LinearLayout)findViewById(R.id.settings_bug);
        bugContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Inflate the view
                View dialogView = View.inflate(SettingsActivity.this, R.layout.dialog_edittext, null);

                //Create the Builder
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
                //Set up the view
                alertDialogBuilder.setView(dialogView);
                //EditText
                final EditText userInput = (EditText) dialogView.findViewById(R.id.dialog_input);

                //Set up the dialog
                alertDialogBuilder.setCancelable(false)
                        .setNegativeButton(getResources().getString(android.R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                })
                        .setPositiveButton(getResources().getString(android.R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        GoogleAnalytics.sendEvent(SettingsActivity.this, "About", "Report a Bug",
                                                null, null);

                                        Help.sendBugReport(SettingsActivity.this, userInput.getText().toString(), "");
                                    }
                                })
                        .create().show();
            }
        });

        //Set up the info
        Spinner languages = (Spinner)findViewById(R.id.settings_language);
        //Set up the array of languages
        List<String> languageStrings = new ArrayList<String>();
        languageStrings.add(getResources().getString(R.string.english));
        languageStrings.add(getResources().getString(R.string.french));
        Collections.sort(languageStrings);
        //Standard ArrayAdapter
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, languageStrings);
        languageAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        //Apply the adapter to the spinner
        languages.setAdapter(languageAdapter);
        //Set the default selected to the user's chosen language
        languages.setSelection(App.getLanguage().ordinal());
        languages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //Get the chosen language
                Language chosenLanguage = Language.values()[position];

                GoogleAnalytics.sendEvent(SettingsActivity.this, "Settings", "Language", chosenLanguage.getLanguageString(), null);

                //If it's different than the previously selected language, update it and reload
                if(App.getLanguage() != chosenLanguage){
                    App.setLanguage(chosenLanguage);

                    //Update locale and config
                    Locale locale = new Locale(chosenLanguage.getLanguageString());
                    Locale.setDefault(locale);
                    Configuration config = getBaseContext().getResources().getConfiguration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

                    startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
                    finish();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        Spinner homepages = (Spinner)findViewById(R.id.settings_homepage);
        final HomePageAdapter homePageAdapter = new HomePageAdapter(this);
        homepages.setAdapter(homePageAdapter);
        homepages.setSelection(homePageAdapter.getPosition(App.getHomePage()));
        homepages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //Get the chosen homepage
                HomePage chosenHomePage = homePageAdapter.getItem(position);

                GoogleAnalytics.sendEvent(SettingsActivity.this, "Settings", "Homepage", chosenHomePage.toString(), null);

                //Update it in App
                App.setHomePage(chosenHomePage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //Statistics
        CheckBox statistics = (CheckBox)findViewById(R.id.settings_statistics);
        statistics.setChecked(Load.loadStatistics(this));
        statistics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Save.saveStatistics(SettingsActivity.this, b);
            }
        });

        //Version Number
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String appVersionName = packageInfo.versionName;

            TextView versionNumber = (TextView)findViewById(R.id.settings_version);
            versionNumber.setText(getResources().getString(R.string.settings_version, appVersionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}

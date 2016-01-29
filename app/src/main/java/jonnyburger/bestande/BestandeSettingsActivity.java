package jonnyburger.bestande;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.MenuItem;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class BestandeSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Display the fragment as the main content.
        PrefsFragment fragment = new PrefsFragment();
        fragment.setCtx(this);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                fragment).commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(FetchGradeService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(fetchReceiver, filter);
    }

    public static long getIntervalFromEnum(Intervals interval) {
        int one_second = 1000;
        int one_minute = 60 * one_second;
        int one_hour = 60 * one_minute;
        switch (interval) {
            case MANUALLY:
                return 0;
            case MIN_15:
                return 15 * one_minute;
            case MIN_30:
                return 30 * one_minute;
            case MIN_60:
                return one_hour;
            case HOUR_3:
                return 3 * one_hour;
            case HOUR_8:
                return 8 * one_hour;
        }
        return 0;
    }


    public static class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        Context ctx;

        public void setCtx(Context context) {
            ctx = context;
        }
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }
        @Override
        public void onResume() {
            super.onResume();
            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
                Preference preference = getPreferenceScreen().getPreference(i);
                if (preference instanceof PreferenceGroup) {
                    PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
                    for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
                        updatePreference(preferenceGroup.getPreference(j));
                    }
                } else {
                    updatePreference(preference);
                }
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("authChanged")) return;
            if (key.equals("uzh_username") || key.equals("uzh_password") || key.equals("serverType") || key.equals("customServer")) {
                sharedPreferences.edit().putBoolean("authChanged", true).commit();
            }
            if (key.equals("refreshInterval")) {
                ListPreference listPreference = (ListPreference) findPreference(key);
                scheduleInterval();
            }
            updatePreference(findPreference(key));
        }

        private void updatePreference(Preference preference) {
            if (preference == null)
                return;
            if (preference.getKey().equals("refreshInterval")) {
                ListPreference listPreference = (ListPreference) preference;
                listPreference.setSummary(listPreference.getEntry());
            }
            if (preference.getKey().equals("uzh_username")) {
                EditTextPreference textPreference = (EditTextPreference) preference;
                textPreference.setSummary(textPreference.getText());
            }
        }
        public void scheduleInterval() {
            Intent intent = new Intent(ctx, BackgroundIntervalReceiver.class);

            PreferenceManager
                    .getDefaultSharedPreferences(ctx).edit().putInt("gradeCount", 0).apply();
            intent.putExtra("username", getUsername());
            intent.putExtra("password", getPassword());

            final PendingIntent pIntent = PendingIntent.getBroadcast(ctx, BackgroundIntervalReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            long firstMillits = System.currentTimeMillis();
            AlarmManager alarm = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

            Intervals refreshPref = Intervals.valueOf(PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getString("refreshInterval", Intervals.MANUALLY.toString()));

            if (refreshPref == Intervals.MANUALLY) {
                alarm.cancel(pIntent);
                try {
                    Snackbar.make(getView(), "Benachrichtigungen deaktiviert.", Snackbar.LENGTH_SHORT).show();
                }
                catch (NullPointerException e) {
                    //
                }
            }
            else {
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillits, getIntervalFromEnum(refreshPref), pIntent);
                try {
                    Snackbar.make(getView(), "Einstellungen gespeichert.", Snackbar.LENGTH_LONG).show();
                }
                catch (NullPointerException e) {
                    //
                }
            }
        }

        private String getUsername() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            return prefs.getString("uzh_username", null);
        }
        private String getPassword() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            return prefs.getString("uzh_password", null);
        }
    }

    private BroadcastReceiver fetchReceiver = new BroadcastReceiver() {

        private String getNextAlarm(Context context) {
            String refreshPref = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString("refreshInterval", Intervals.MANUALLY.toString());
            long timeFromNow = getIntervalFromEnum(Intervals.valueOf(refreshPref));
            Date nextAlarm = new Date(new Date().getTime() + timeFromNow);
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(nextAlarm);
            return "Nächster Check um " + calendar.get(Calendar.HOUR_OF_DAY)
                            + ":" + calendar.get(Calendar.MINUTE);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
            if (resultCode == RESULT_OK) {
                int oldGradeCount = PreferenceManager
                        .getDefaultSharedPreferences(context)
                        .getInt("gradeCount", 0);
                int newGradeCount = intent.getIntExtra("resultValue", 0);
                PreferenceManager
                        .getDefaultSharedPreferences(context).edit()
                        .putInt("gradeCount", newGradeCount).commit();
                String refreshPref = PreferenceManager.getDefaultSharedPreferences(context)
                        .getString("refreshInterval", Intervals.MANUALLY.toString());
                if (newGradeCount > oldGradeCount) {
                    String humanReadableInterval = "";
                    switch (Intervals.valueOf(refreshPref)) {
                        case MANUALLY:
                            humanReadableInterval = "nicht mehr";
                            break;
                        case MIN_15:
                            humanReadableInterval = "alle 15 Minuten";
                            break;
                        case MIN_30:
                            humanReadableInterval = "jede halbe Stunde";
                            break;
                        case MIN_60:
                            humanReadableInterval = "jede Stunde";
                            break;
                        case HOUR_3:
                            humanReadableInterval = "alle 3 Stunden";
                            break;
                        case HOUR_8:
                            humanReadableInterval = "alle 8 Stunden";

                    }
                    String text = oldGradeCount == 0
                            ? "Bestande prüft nun " + humanReadableInterval + ", ob neue Noten erhalten wurden."
                            : "Neue Note erhalten!";
                    android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.logo)
                            .setContentTitle(oldGradeCount == 0 ? "Benachrichtigungen aktiviert" : "Neue Note")
                            .setColor(ContextCompat.getColor(context, R.color.green))
                            .setPriority(oldGradeCount == 0 ? NotificationCompat.PRIORITY_DEFAULT : NotificationCompat.PRIORITY_HIGH)
                            .setContentText(text)
                            .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText(text));

                    android.app.NotificationManager mNotifyMgr = (android.app.NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    mNotifyMgr.notify(001, mBuilder.build());
                }
                else {
                    boolean shouldShow = PreferenceManager.getDefaultSharedPreferences(context)
                            .getBoolean("showFailedStatus", false);
                    if (shouldShow) {
                        String text = "Keine neue Note. " + getNextAlarm(context) + ".";
                        android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.logo)
                                .setContentTitle("Bestande")
                                .setPriority(NotificationCompat.PRIORITY_MIN)
                                .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText(text))
                                .setContentText(text);

                        android.app.NotificationManager mNotifyMgr = (android.app.NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        mNotifyMgr.notify(002, mBuilder.build());
                    }
                }

            }
            if (resultCode == RESULT_CANCELED) {
                NoCreditDataReason noDataReason = NoCreditDataReason.valueOf(intent.getStringExtra("error"));
                String reason = "";
                if (noDataReason == NoCreditDataReason.REQUEST_FAILED) {
                    reason = "Verbindung fehlgeschlagen.";
                }
                else if (noDataReason == NoCreditDataReason.NO_CREDENTIALS_SUPPLIED) {
                    reason = "Nicht eingeloggt";
                }
                else if (noDataReason == NoCreditDataReason.USERNAME_PW_WRONG) {
                    reason = "Falsche Login-Daten";
                }
                else if (noDataReason == NoCreditDataReason.USERNAME_UNKNOWN) {
                    reason = "Unbekannter Benutzername";
                }
                else {
                    reason = noDataReason.toString();
                }
                String text = "Konnte nicht nach neuen Noten suchen. " + reason;
                android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Laden fehlgeschlagen")
                        .setColor(ContextCompat.getColor(context, R.color.red))
                        .setContentText(text)
                        .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText(text));

                android.app.NotificationManager mNotifyMgr = (android.app.NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.notify(001, mBuilder.build());
            }
        }
    };

}
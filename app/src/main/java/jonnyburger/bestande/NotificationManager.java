package jonnyburger.bestande;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Random;

public class NotificationManager {
    private static NotificationManager notificationManager;
    private Context _context;

    private NotificationManager(Context context) {
        this._context = context;
    }

    public static synchronized NotificationManager getInstance(Context context) {
        if (notificationManager == null) {
            notificationManager = new NotificationManager(context);
        }
        return notificationManager;
    }

    // TODO: makeNotification
    // TODO: makeUpdate


    // TODO: connectWithGCM
    // TODO: getNewCount

    public int getBatch() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);

        int batch = prefs.getInt("batch", -1);
        if (batch != -1) {
            return batch;
        }
        int generated = new Random().nextInt(15);
        prefs.edit().putInt("batch", generated).apply();
        return generated;
    }

    public void postIntervalUpdate(String message) {
        PreferenceManager.getDefaultSharedPreferences(this._context).edit().putString("interval-info", message);
        // TODO: Emit event
    }


    public Intervals getCurrentInterval() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this._context);
        String intervalHash = prefs.getString("interval", null);

        if (intervalHash != null) {
            return Intervals.valueOf(intervalHash);
        }
        prefs.edit().putString("interval", Intervals.MANUALLY.name()).apply();
        return Intervals.MANUALLY;
    }
    public void setCurrentInterval(Intervals interval) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
        prefs.edit().putString("interval", interval.name());
    }

}

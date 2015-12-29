package jonnyburger.bestande;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class RestartIntervalReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        BestandeSettingsActivity.PrefsFragment a = new BestandeSettingsActivity.PrefsFragment();
        a.setCtx(context);
        a.scheduleInterval();
    }
}

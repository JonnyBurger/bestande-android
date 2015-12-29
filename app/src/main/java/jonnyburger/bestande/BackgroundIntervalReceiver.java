package jonnyburger.bestande;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BackgroundIntervalReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "jonnyburger.bestande.BackgroundIntervalReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, FetchGradeService.class);

        System.out.println("Start service");
        i.putExtra("username", intent.getStringExtra("username"));
        i.putExtra("password", intent.getStringExtra("password"));
        context.startService(i);
    }
}

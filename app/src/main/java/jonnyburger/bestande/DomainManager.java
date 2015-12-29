package jonnyburger.bestande;


import android.content.Context;
import android.preference.PreferenceManager;


public final class DomainManager {
    public static String getDomain(Context context) {
        String pref = PreferenceManager.getDefaultSharedPreferences(context).getString("serverType", "default");
        if (pref.equals("default")) {
            return "http://bestande.ch";
        }
        return PreferenceManager.getDefaultSharedPreferences(context).getString("customServer", "");
    }
}

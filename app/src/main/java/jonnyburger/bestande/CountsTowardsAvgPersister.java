package jonnyburger.bestande;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class CountsTowardsAvgPersister {

    public static void set(Credit credit, boolean counts, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("foo", 0);
        prefs.edit().putBoolean("counts-" + credit.module, counts).apply();
    }

    public static boolean get(Credit credit, Context context) {
        if (!canCount(credit)) return false;
        SharedPreferences prefs = context.getSharedPreferences("foo", 0);
        return prefs.getBoolean("counts-" + credit.module, defaultShouldCount(credit));
    }

    public static boolean defaultShouldCount(Credit credit) {
        if (!canCount(credit)) return false;
        if (credit.status == PassStatus.FAILED) return false;
        try {
            double grade = Double.parseDouble(credit.grade);
            return grade >= 1 && grade <= 6;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean canCount(Credit credit) {
        if (credit.status == PassStatus.DESELECTED || credit.status == PassStatus.BOOKED) {
            return false;
        }
        try {
            double grade = Double.parseDouble(credit.grade);
            return grade >= 1 && grade <= 6;
        }
        catch (NumberFormatException e) {
            return credit.grade.equals("BEST") || credit.grade.equals("N. BE") || credit.grade.equals("N.BE");
        }
    }
}

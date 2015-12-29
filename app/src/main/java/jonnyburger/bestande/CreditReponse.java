package jonnyburger.bestande;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class CreditReponse {
    public boolean success;
    public List<Semester> credits;
    public JSONObject stats;
    public NoCreditDataReason noDataReason = NoCreditDataReason.NOT_TRIED;
    public String stack = "";

    public CreditReponse(JSONObject obj) {
        try {
            this.success = obj.getBoolean("success");
            if (this.success) {
                JSONArray arr = obj.getJSONArray("credits");
                List<Semester> semesters = new ArrayList();
                for (int i = 0; i < arr.length(); i++) {
                    semesters.add(new Semester(arr.getJSONObject(i)));
                }
                this.credits = semesters;
                this.stats = obj.getJSONObject("stats");
            }
            else {
                String message = obj.getString("message");
                if (message != null) {
                    try {
                        this.noDataReason = NoCreditDataReason.valueOf(message);
                    }
                    catch (IllegalArgumentException e) {
                        this.noDataReason = NoCreditDataReason.OTHER_REASON;
                    }
                }
                else {
                    this.noDataReason = NoCreditDataReason.OTHER_REASON;
                }
                this.success = false;
                this.stack = obj.getString("stack");
            }
        }
        catch (Exception e) {
            this.noDataReason = NoCreditDataReason.REQUEST_FAILED;
            this.success = false;
        }

    }
}

package jonnyburger.bestande;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Dictionary;
import java.util.Map;

public class Credit implements Serializable{
    public String name = "";
    public String short_name = "";
    public Double credits_worth = 0.0;
    public Double credits_received = 0.0;
    public String link = "";
    public String module = "";
    public PassStatus status = PassStatus.UNKNOWN;
    public String grade = "";
    public String firstInGroup = null;
    public JSONObject stats = null;

    public Credit(JSONObject obj) {
        try {
            this.name = (String) obj.get("name");
            this.short_name = (String) obj.get("short_name");
            this.credits_worth = obj.getDouble("credits_worth");
            this.credits_received = obj.getDouble("credits_received");
            this.link = (String) obj.get("link");
            this.module = (String) obj.get("module");
            this.status = PassStatus.valueOf((String)obj.get("status"));
            this.grade = (String) obj.get("grade");
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public Credit(JSONObject stats, boolean isStats) {
        if (isStats) {
            this.stats = stats;
        }
    }
}

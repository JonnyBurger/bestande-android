package jonnyburger.bestande;

import org.json.JSONException;
import org.json.JSONObject;

public class Lecturer {
    public String link = "";
    public String name = "";

    public Lecturer(JSONObject jsonObject) {
        try {
            this.link = jsonObject.getString("link");
            this.name = jsonObject.getString("name");
        }
        catch (JSONException e) {
            System.out.println("Oops");
        }
    }
}

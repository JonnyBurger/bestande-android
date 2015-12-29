package jonnyburger.bestande;

import org.json.JSONException;
import org.json.JSONObject;

public class Room {
    public String link;
    public String name;

    public Room(JSONObject jsonObject) {
        try {
            this.link = jsonObject.getString("link");
            this.name = jsonObject.getString("name");
        }
        catch (JSONException e) {
            System.out.println("Failed to parse JSON");
        }
    }
}

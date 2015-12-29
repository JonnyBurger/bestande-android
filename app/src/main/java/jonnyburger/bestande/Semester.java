package jonnyburger.bestande;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Semester {
    public String semester;
    public List<Credit> credits;

    public Semester(JSONObject object) {
        try {
            this.semester = object.getString("semester");
            JSONArray array = (JSONArray) object.get("credits");
            List<Credit> creditList = new ArrayList();
            for (int i = 0; i < array.length(); i++) {
                creditList.add(new Credit(array.getJSONObject(i)));
            }
            this.credits = creditList;
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}

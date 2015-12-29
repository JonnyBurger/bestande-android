package jonnyburger.bestande;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {
    public List<Room> rooms;
    public List<Lecturer> lecturers;
    public EventType type = EventType.SO;
    public Date startdate;
    public Date enddate;
    public String number;

    public Event(JSONObject jsonObject) {
        try {
            JSONArray rooms = jsonObject.getJSONArray("rooms");
            this.rooms = new ArrayList();
            for (int i = 0; i < rooms.length(); i++) {
                this.rooms.add(new Room(rooms.getJSONObject(i)));
            }

            JSONArray lecturers = jsonObject.getJSONArray("lecturers");
            this.lecturers = new ArrayList();
            for (int i = 0; i < lecturers.length(); i++) {
                this.lecturers.add(new Lecturer(lecturers.getJSONObject(i)));
            }

            this.type = EventType.valueOf(jsonObject.getString("type"));
            this.startdate = new Date(jsonObject.getLong("starttime"));
            this.enddate = new Date(jsonObject.getLong("endtime"));
            this.number = jsonObject.getString("number");
        }
        catch (JSONException e) {

        }
    }

    public String getSubTitle() {
        String subtitle = "";

        try {
            subtitle += getDate();
            subtitle += ", ";
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            subtitle += room.name;
            if (i != (rooms.size() - 1)) {
                subtitle += ", ";
            }
        }
        return subtitle;
    }

    public String getDate() {
        return new SimpleDateFormat("dd.MM.yy").format(this.startdate)
                + ", " + new SimpleDateFormat("HH:mm").format(this.startdate)
                + " - " + new SimpleDateFormat("HH:mm").format(this.enddate);
    }
    public boolean isInPast() {
        return new Date().after(this.enddate);
    }
    public String getTitle() {
        if (this.type == EventType.AR) {
            return "Arbeit";
        }
        if (this.type == EventType.BL) {
            return "Blockkurs";
        }
        if (this.type == EventType.EX) {
            return "Exkursion";
        }
        if (this.type == EventType.KO) {
            return "Kolloquium";
        }
        if (this.type == EventType.PF) {
            return "Prüfung";
        }
        if (this.type == EventType.PR) {
            return "Praktikum";
        }
        if (this.type == EventType.PS) {
            return "Proseminar";
        }
        if (this.type == EventType.SE) {
            return "Seminar";
        }
        if (this.type == EventType.SK) {
            return "Sprachkurs";
        }
        if (this.type == EventType.SL) {
            return "Sprachlabor";
        }
        if (this.type == EventType.SO) {
            return "Sonstiges";
        }
        if (this.type == EventType.SS) {
            return "Selbststudium";
        }
        if (this.type == EventType.UE) {
            return "Übung";
        }
        if (this.type == EventType.VL) {
            return "Vorlesung";
        }
        if (this.type == EventType.VU) {
            return "Vorlesung / Übung";
        }
        return "Sonstiges";
    }
}

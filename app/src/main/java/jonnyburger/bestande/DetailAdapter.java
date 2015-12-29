package jonnyburger.bestande;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class DetailAdapter extends ArrayAdapter<Event> {
    public DetailAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }
    public DetailAdapter(Context context, int resource, List<Event> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        Event p = getItem(position);

        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());

        v = vi.inflate(R.layout.detail_cell, null);

        TextView titleView = (TextView) v.findViewById(R.id.courseTitle);
        TextView subtitleView = (TextView) v.findViewById(R.id.courseSubtitle);

        titleView.setText(p.getTitle() + " " + p.number);
        subtitleView.setText(p.getSubTitle());

        return v;
    }
}

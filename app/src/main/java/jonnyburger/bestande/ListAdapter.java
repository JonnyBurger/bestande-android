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

public class ListAdapter extends ArrayAdapter<Credit> {
    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }
    public ListAdapter(Context context, int resource, List<Credit> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        Credit p = getItem(position);

        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());
        if (p.stats != null) {
            v = vi.inflate(R.layout.stats_cell, null);
            TextView ectsLabel = (TextView) v.findViewById(R.id.statEcts);
            TextView avgLabel = (TextView) v.findViewById(R.id.statAvg);
            try {
                ectsLabel.setText(p.stats.getString("total_credits"));
                avgLabel.setText(p.stats.getString("avg"));
            }
            catch (Exception e) {
                ectsLabel.setText("-");
            }

        }
        else {
            v = vi.inflate(R.layout.credit_cell, null);
        }

        if (p.stats != null) {
            return v;
        }



        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.module_name);
            tt1.setText(p.short_name);
            TextView statusView = (TextView) v.findViewById(R.id.module_status);
            View ball = v.findViewById(R.id.ball);
            if (p.status == PassStatus.PASSED) {
                statusView.setTextColor(Color.argb(255, 46, 204, 113));
                statusView.setText("Bestanden");
                ball.setBackgroundResource(R.drawable.passed_ball);
            }
            else if (p.status == PassStatus.FAILED) {
                statusView.setTextColor(Color.argb(255, 231, 76, 60));
                statusView.setText("Fehlversuch");
                ball.setBackgroundResource(R.drawable.failed_ball);
            }
            else if (p.status == PassStatus.DESELECTED) {
                statusView.setTextColor(Color.argb(255, 230, 126, 34));
                statusView.setText("Abgewählt");
                ball.setBackgroundResource(R.drawable.deselected_ball);
            }
            else if (p.status == PassStatus.BOOKED) {
                statusView.setTextColor(Color.argb(255, 52, 152, 219));
                statusView.setText("Gebucht");
                ball.setBackgroundResource(R.drawable.booked_ball);
            }
            else if (p.status == PassStatus.UNKNOWN) {
                statusView.setText("Unbekannt");
            }
            TextView creditsView = (TextView) v.findViewById(R.id.module_credits);
            creditsView.setText("ECTS: " + p.credits_worth.toString());

            TextView gradeView = (TextView) v.findViewById(R.id.module_grade);
            if (p.grade.length() > 0) {
                gradeView.setText("Note: " + p.grade);
            }
            else {
                gradeView.setText("");
            }

            TextView header = (TextView) v.findViewById(R.id.section_seperator);
            if (p.firstInGroup != null) {
                header.setText(p.firstInGroup);
                header.setVisibility(View.VISIBLE);
            }
            else {
                header.setVisibility(View.GONE);
            }
        }

        return v;
    }
}

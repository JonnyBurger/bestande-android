package jonnyburger.bestande;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    public Credit ser;
    private static final int EVENT_CELL_HEIGHT = 51;
    List<Event> events;
    List<Event> prevEvents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        ser = (Credit) getIntent().getSerializableExtra("credit");
        setTitle(ser.short_name);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(ser.short_name);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Switch _switch = (Switch) findViewById(R.id.countsTowardsAvgSwitch);
        boolean counts = CountsTowardsAvgPersister.get(ser, this);
        boolean canCount = CountsTowardsAvgPersister.canCount(ser);
        _switch.setChecked(counts);
        _switch.setEnabled(canCount);
        final Context context = this;

        _switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CountsTowardsAvgPersister.set(ser, isChecked, context);
            }
        });
        makeRequest();

    }

    public void onWindowFocusChanged() {
        if (events == null) return;
        // get content height
        ListView upcomingView = (ListView) findViewById(R.id.nowListView);
        View child = upcomingView.getChildAt(0);
        if (child != null) {
            int contentHeight = child.getHeight();
            ViewGroup.LayoutParams lp = upcomingView.getLayoutParams();
            lp.height = (contentHeight + 2) * events.size();
            upcomingView.setVisibility(View.VISIBLE);
            upcomingView.setLayoutParams(lp);
            findViewById(R.id.upcomingLabel).setVisibility(View.VISIBLE);
        }

        ListView previousView = (ListView) findViewById(R.id.previousListView);
        View child2 = previousView.getChildAt(0);
        if (child2 != null) {
            int contentHeight2 = child2.getHeight();
            ViewGroup.LayoutParams prevLp = previousView.getLayoutParams();
            prevLp.height = (contentHeight2 + 2) * prevEvents.size();
            previousView.setVisibility(View.VISIBLE);
            previousView.setLayoutParams(prevLp);
            findViewById(R.id.previousLabel).setVisibility(View.VISIBLE);
        }
    }

    private void makeRequest() {
        new GradeRequest().execute();
    }
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private class GradeRequest extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... params) {
            RequestManager manager = new RequestManager(getApplicationContext());
            return manager.getEvents(DomainManager.getDomain(getApplicationContext()), ser, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Anfrage fehlgeschlagen", Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        protected void onPostExecute(JSONObject response) {
            try {
                JSONArray _events = response.getJSONArray("data");
                events = new ArrayList<>();
                prevEvents = new ArrayList<>();
                for (int i = 0; i < _events.length(); i++) {
                    Event event = new Event(_events.getJSONObject(i));
                    if (event.isInPast()) {
                        prevEvents.add(event);
                    }
                    else {
                        events.add(event);
                    }
                }
                ListView upcomingView = (ListView) findViewById(R.id.nowListView);
                ListView previousView = (ListView) findViewById(R.id.previousListView);
                ArrayAdapter adapter = new DetailAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, events);
                ArrayAdapter prevAdapter = new DetailAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, prevEvents);
                upcomingView.setAdapter(adapter);
                previousView.setAdapter(prevAdapter);
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onWindowFocusChanged();
                    }
                }, 100);
            }
            catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Anfrage fehlgeschlagen", Toast.LENGTH_SHORT).show();
            }
        }

    }

}

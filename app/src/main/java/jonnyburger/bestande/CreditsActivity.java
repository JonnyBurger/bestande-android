package jonnyburger.bestande;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreditsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView listview;
    private Context _context;
    private boolean hasData;

    private View progressBar;

    private CreditReponse crResponse;

    private NoCreditDataReason noDataReason = NoCreditDataReason.NOT_TRIED;

    private Snackbar myToast;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent settingsIntent = new Intent(this, BestandeSettingsActivity.class);
        startActivity(settingsIntent);
        return true;
    }

    private void displayLoadingOverlay() {
        Adapter adapter = listview.getAdapter();
        if (adapter == null) {
            myToast.show();
        }
        mSwipeRefreshLayout.setRefreshing(true);
    }

    private void hideLoadingOverlay() {
        mSwipeRefreshLayout.setRefreshing(false);
        myToast.dismiss();
    }

    private String getUsername() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString("uzh_username", null);
    }
    private String getPassword() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString("uzh_password", null);
    }


    private void refresh() {
        hideNoDataReason();
        if (this.usernameAndPasswordSupplied()) {
            this.displayLoadingOverlay();
            new GradeRequest().execute();

        }
        else {
            this.noDataReason = NoCreditDataReason.NO_CREDENTIALS_SUPPLIED;
            this.hasData = false;
            this.reloadView();
        }
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private class GradeRequest extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... params) {
            RequestManager manager = new RequestManager(getApplicationContext());
            return manager.makeRequest(getUsername(), getPassword(),DomainManager.getDomain(getApplicationContext()), new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hasData = false;
                    try {
                        CreditReponse credits = new CreditReponse(new JSONObject(new String(error.networkResponse.data)));
                        noDataReason = credits.noDataReason;
                    }
                    catch (Exception e) {
                        noDataReason = NoCreditDataReason.REQUEST_FAILED;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            reloadView();
                        }
                    });
                }
            });
        }
        @Override
        protected void onPostExecute(JSONObject response) {
            crResponse = new CreditReponse(response);
            hasData = crResponse.success;
            noDataReason = crResponse.noDataReason;
            reloadView();
        }

    }

    private boolean usernameAndPasswordSupplied() {
        String username = getUsername();
        String password = getPassword();

        return username != null && password != null && !username.equals("") && !password.equals("");
    }

    private boolean isOnline()
    {
        try
        {
            ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        try {
            getSupportActionBar().setElevation(0);
        }
        catch(Exception e) {
            // Do nothing for Android <= 4
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        _context = this;
        myToast = Snackbar.make(findViewById(R.id.creditLayout), "Lade Verzeichnis...", Snackbar.LENGTH_INDEFINITE);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

    }

    public void displayNoDataReason() {
        TextView emptyView = (TextView) findViewById(R.id.emptyView);
        if (this.noDataReason == NoCreditDataReason.NO_CREDENTIALS_SUPPLIED) {
            emptyView.setText("Willkommen! Bitte logge dich als erstes in deinen UZH-Account ein.");
        }
        else if (this.noDataReason == NoCreditDataReason.OTHER_REASON) {
            emptyView.setText("Fehler");
        }
        else if (this.noDataReason == NoCreditDataReason.NOT_TRIED) {
            emptyView.setText("Wird geladen...");
        }
        else if (this.noDataReason == NoCreditDataReason.REQUEST_FAILED) {
            if (isOnline()) {
                emptyView.setText("Es konnte keine Verbindung zum Server hergestellt werden.");
            }
            else {
                emptyView.setText("Du bist offline.");
            }
        }
        else if (this.noDataReason == NoCreditDataReason.NO_PASSWORD) {
            emptyView.setText("Es wurde kein Passwort angegeben.");
        }
        else if (this.noDataReason == NoCreditDataReason.NO_USERNAME) {
            emptyView.setText("Es wurde kein Benutzername angegeben.");
        }
        else if (this.noDataReason == NoCreditDataReason.OFFLINE) {
            emptyView.setText("Du bist offline.");
        }
        else if (this.noDataReason == NoCreditDataReason.SCRAPE_PARSE_ERROR) {
            emptyView.setText("Der UZH-Server hat etwas unerwartetes zurückgegeben.");
        }
        else if (this.noDataReason == NoCreditDataReason.LOGIN_PAGE_LOAD_FAIL) {
            emptyView.setText("Es konnte keine Verbindung zum Authentifizierungsserver hergestellt werden.");
        }
        else if (this.noDataReason == NoCreditDataReason.USERNAME_PW_WRONG) {
            emptyView.setText("Der Benutzername oder Passwort ist falsch.");
        }
        else {
            emptyView.setText(this.noDataReason.toString());
        }
        emptyView.setVisibility(View.VISIBLE);
        listview = (ListView) findViewById(R.id.listView);
        listview.setEmptyView(emptyView);
        listview.setAdapter(null);
    }

    public void hideNoDataReason() {

        ViewGroup root = (ViewGroup) findViewById(R.id.creditLayout);
        root.findViewById(R.id.emptyView).setVisibility(View.GONE);
    }

    private boolean authHasChanged() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("authChanged", false)) {
            prefs.edit().putBoolean("authChanged", false).commit();
            return true;
        }
        else {
            return false;
        }
    }

    private Double parseGrade(String grade) {
        if (grade.equals("BEST")) return 6.0;
        try {
            return Double.parseDouble(grade);
        }
        catch (Exception e) {
            return 1.0;
        }
    }

    private Double calculateAvg(List<Semester> semesters) {
        Double gradeAvg = 0.0;
        Double totalCredits = 0.0;

        for (int i = 0; i < semesters.size(); i++) {
            Semester semester = semesters.get(i);
            for (int j = 0; j < semester.credits.size(); j++) {
                Credit credit = semester.credits.get(j);
                if (!credit.grade.equals("")
                        && CountsTowardsAvgPersister.get(credit, this)
                        && (credit.status == PassStatus.PASSED
                            || credit.status == PassStatus.FAILED)) {
                    gradeAvg += credit.credits_worth * parseGrade(credit.grade);
                    totalCredits += credit.credits_worth;
                }
            }
        }
        return Math.floor(gradeAvg / totalCredits * 100) / 100;
    }

    private void makeAdapter() {
        final ArrayList<Credit> list = new ArrayList<>();
        try {
            crResponse.stats.put("avg", calculateAvg(crResponse.credits));
        }
        catch (Exception e) {
            System.out.println("cannot do avg");
        }
        list.add(new Credit(crResponse.stats, true));

        for (int i = 0; i < crResponse.credits.size(); i++) {
            for (int j = 0; j < crResponse.credits.get(i).credits.size(); j++) {
                Credit a = crResponse.credits.get(i).credits.get(j);
                if (j == 0) {
                    a.firstInGroup = crResponse.credits.get(i).semester;
                }
                list.add(a);
            }
        }

        final ArrayAdapter adapter = new ListAdapter(_context, android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);
        final Context _context = this;

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    Intent settingsIntent = new Intent(_context, DetailActivity.class);
                    settingsIntent.putExtra("credit", list.get(position));
                    startActivity(settingsIntent);
                }
            }
        });


        hideLoadingOverlay();
    }

    private void reloadView() {
        mSwipeRefreshLayout.setRefreshing(false);
        if (authHasChanged()) {
            this.refresh();
            return;
        }
        if (hasData) {
            makeAdapter();
            listview.invalidateViews();
            hideNoDataReason();
        }
        else {
            displayNoDataReason();
            hideLoadingOverlay();
            if (this.noDataReason == NoCreditDataReason.NOT_TRIED) {
                refresh();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.reloadView();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}

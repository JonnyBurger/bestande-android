package jonnyburger.bestande;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FetchGradeService extends IntentService {
    public static final String ACTION = "jonnyburger.bestande.FetchGradeService";

    public FetchGradeService() {
        super("fetch-service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Intent in = new Intent(ACTION);
        in.putExtra("resultCode", Activity.RESULT_OK);

        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

        RequestManager mgr = new RequestManager(this);

        JSONObject response = mgr.makeRequest(intent.getStringExtra("username"), intent.getStringExtra("password"), DomainManager.getDomain(this), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                in.putExtra("resultCode", Activity.RESULT_CANCELED);
                try {
                    CreditReponse credits = new CreditReponse(new JSONObject(new String(error.networkResponse.data)));
                    in.putExtra("error", credits.noDataReason.toString());
                }
                catch (JSONException | NullPointerException e) {
                    in.putExtra("error", NoCreditDataReason.REQUEST_FAILED.toString());
                }
                broadcastManager.sendBroadcast(in);
            }
        });
        if (response != null) {
            in.putExtra("resultCode", Activity.RESULT_OK);
            int gradesReceived = 0;
            List<Semester> semesters = new CreditReponse(response).credits;
            for (int i = 0; i < semesters.size(); i++) {
                Semester semester = semesters.get(i);
                for (int j = 0; j < semester.credits.size(); j++) {
                    Credit credit = semester.credits.get(j);
                    if (credit.status == PassStatus.PASSED || credit.status == PassStatus.UNKNOWN) {
                        gradesReceived++;
                    }
                }
            }
            in.putExtra("resultValue", gradesReceived);
            broadcastManager.sendBroadcast(in);
        }
    }
}

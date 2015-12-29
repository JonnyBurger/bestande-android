package jonnyburger.bestande;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RequestManager {
    private Context _context;
    private String username;
    private String password;

    public RequestManager(Context context) {
        this._context = context;
    }
    public JSONObject makeRequest(String username, String password, String domain, Response.ErrorListener errorListener) {
        RequestQueue queue = Volley.newRequestQueue(this._context);

        JSONObject jsonObject = null;

        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        jsonObject = new JSONObject(params);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, domain + "/api", jsonObject, future, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(20000, 0, 0));
        queue.add(request);
        try {
            return future.get(50, TimeUnit.SECONDS);
        }
        catch (InterruptedException | ExecutionException | TimeoutException e) {
            errorListener.onErrorResponse(new VolleyError(e));
        }
        return null;
    }

    public JSONObject getEvents(String domain, Credit credit, Response.ErrorListener errorListener) {
        RequestQueue queue = Volley.newRequestQueue(this._context);

        JSONObject jsonObject = null;

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, domain + "/api/vorlesung?url=" + credit.link, jsonObject, future, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(20000, 0, 0));
        queue.add(request);
        try {
            return future.get(50, TimeUnit.SECONDS);
        }
        catch (InterruptedException | ExecutionException | TimeoutException e) {
            errorListener.onErrorResponse(new VolleyError(e));
        }
        return null;
    }

}

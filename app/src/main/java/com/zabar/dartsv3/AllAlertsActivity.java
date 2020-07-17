package com.zabar.dartsv3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sinch.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AllAlertsActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    ArrayList<Alert> alerts = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_alerts);

        //starting the service
        SharedPreferences sd = this.getSharedPreferences("authInfo", 0);
        String myId =  sd.getString("myID", "");
        if(myId.equals(""))
            return;


        String url = Server.getUrl() + "/qrunit/"+myId+"/alerts";
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, this,  this);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if(response.has("err")){
                Toast.makeText(this, response.getJSONObject("err").getString("message") + "Inalertinfo", Toast.LENGTH_SHORT).show();

            }else if(response.has("succ")){
                if(response.has("alerts")){
                    JSONArray ja = response.getJSONArray("alerts");
                    for(int i = 0; i < ja.length(); i++){
                        Alert alert = Alert.fromJSONObject(ja.getJSONObject(i));
                        if(alert != null && alert.suspect != null)
                            this.alerts.add(0, alert);
                    }
                    updateFront();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }



    public void updateFront(){
        AllAlertsAdapter aaa = new AllAlertsAdapter(this, alerts);
        RecyclerView rv = findViewById(R.id.alertsHolder);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(aaa);

    }
}

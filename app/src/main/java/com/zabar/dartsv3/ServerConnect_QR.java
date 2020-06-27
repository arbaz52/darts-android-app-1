package com.zabar.dartsv3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ServerConnect_QR extends AsyncTask {
    public static final String SERVER_IP="http://192.168.100.138:3000";
    Context context;
    String myID;
    public ServerConnect_QR(Context context, String myID){
        this.context=context;
        this.myID = myID;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {

            URL mUrl = new URL(SERVER_IP + "/qrunit/" + myID);
            HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Content-length", "0");
            httpConnection.setUseCaches(false);
            httpConnection.setAllowUserInteraction(false);
            httpConnection.setConnectTimeout(100000);
            httpConnection.setReadTimeout(100000);

            httpConnection.connect();

            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;

    }

    protected void onPostExecute(String s){
        super.onPostExecute(s);

        if(s.equals("")){

        }
        else{
            try {
                JSONObject reader = new JSONObject(s);
                if (reader.has("err")){
                    String error_msg=reader.getJSONObject("err").getString("message");
                    Toast.makeText(this.context, error_msg, Toast.LENGTH_SHORT).show();
                }
                else if(reader.has("succ")){
                    String succ_msg=reader.getJSONObject("succ").getString("message");
                    JSONArray units=reader.getJSONArray("qrunit");

                    Toast.makeText(this.context, succ_msg, Toast.LENGTH_SHORT).show();
                    SharedPreferences sp= this.context.getSharedPreferences("authInfo",0 );
                    SharedPreferences.Editor spe=sp.edit();



                    spe.putString("units_array", units.toString());
                    spe.commit();


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}

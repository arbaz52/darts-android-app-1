package com.zabar.dartsv3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ServerConnect extends AsyncTask<String, String, String> {
<<<<<<< HEAD
    public static final String SERVER_IP="http://192.168.0.100:3000";
=======
    public static final String SERVER_IP="http://192.168.100.138:3000";
>>>>>>> 82abe9fea0dbb5a06ec4caffa67c6deacda2c461
    public static final String TAG="kanwal";

    Context context;
    String auth;
    public ServerConnect(Context context, String auth){
        this.context=context;
        this.auth = auth;
    }
    @Override
    protected String doInBackground(String... strings) {
        try {

            URL mUrl = new URL(SERVER_IP + "/qrunit/authenticate/" + auth);
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
                    Intent intent=new Intent(this.context, MainActivity.class);
                    this.context.startActivity(intent);
                }
                else if(reader.has("succ")){
                    String succ_msg=reader.getJSONObject("succ").getString("message");
                    String myID=reader.getString("qrunitId");
                    Toast.makeText(this.context, succ_msg, Toast.LENGTH_SHORT).show();
                    SharedPreferences sp= this.context.getSharedPreferences("authInfo",0 );
                    SharedPreferences.Editor spe=sp.edit();


                    spe.putString("myID", myID);
                    spe.commit();

                    ServerConnect_QR serverQR=new ServerConnect_QR(context, myID);
                    serverQR.execute();

                    Intent intentTPL=new Intent(this.context, MapsActivity.class);
                    this.context.startActivity(intentTPL);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}

package com.zabar.dartsv3;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int QR_CODE_RESULT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        Intent i = new Intent(this, InboxActivity.class);
        //startActivity(i);

        RelativeLayout signupbtn=findViewById(R.id.signup);
        SharedPreferences sp= getApplicationContext().getSharedPreferences("authInfo",0 );
        String key=sp.getString("myID","");
        if(key.equals("")){
            Toast.makeText(this, "Login first", Toast.LENGTH_SHORT).show();
        }
        else{
            Intent loggedin=new Intent(MainActivity.this, MapsActivity.class);
            startActivity(loggedin);
        }
        //Button loginBtn = findViewById(R.id.button);
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QRCodeScannerActivity.class);
                startActivityForResult(intent, QR_CODE_RESULT);
            }
        });

    }
       /*EditText t = findViewById(R.id.editText);
        t.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_ENTER){
                    dirtyLogin(t.getText().toString());
                }
                return false;
            }
        });
    }

    private void dirtyLogin(String id){
        SharedPreferences sp= getApplicationContext().getSharedPreferences("authInfo",0 );
        SharedPreferences.Editor spe=sp.edit();
        spe.putString("myID", id);
        spe.commit();
        Intent intentTPL=new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intentTPL);
    }
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch(requestCode){
                case QR_CODE_RESULT:

                    String auth=data.getStringExtra("myID");
                    ServerConnect server=new ServerConnect(this, auth);
                    server.execute();


                    break;
            }
        }
    }

}

package com.zabar.dartsv3;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static final int QR_CODE_RESULT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent(this, InboxActivity.class);
        //startActivity(i);


        Button loginBtn = findViewById(R.id.button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
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

                    SharedPreferences sp= getApplicationContext().getSharedPreferences("authInfo",0 );
                    SharedPreferences.Editor spe=sp.edit();
                    spe.putString("myID", data.getStringExtra("myID"));
                    spe.commit();
                    Intent intentTPL=new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(intentTPL);

                    break;
            }
        }
    }

}

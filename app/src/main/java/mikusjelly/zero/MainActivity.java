package mikusjelly.zero;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

import me.mikusjelly.zerolib.util.Log;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
    }

    @Override
    protected void onStart() {
        try {
            new MyRPC(getApplicationContext()).startLocal(9999);
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStart();

    }
}

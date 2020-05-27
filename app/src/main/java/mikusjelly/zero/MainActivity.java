package mikusjelly.zero;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    private MyRPC rpc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
    }

    @Override
    protected void onStart() {
        try {
            rpc = new MyRPC(getApplicationContext());
            rpc.startLocal(9999);
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}

package com.martellux.lifecycle.sample;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.martellux.lifecycle.Lifecycle;
import com.martellux.lifecycle.annotation.LifecycleBinder;

public class MainActivity extends AppCompatActivity {

    @LifecycleBinder
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            MainFragment f = new MainFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.content_main, f).commit();
        }
    }

    @LifecycleBinder
    @Override
    protected void onStart() {
        super.onStart();
    }


    @LifecycleBinder
    @Override
    protected void onStop() {
        super.onStop();
    }

    @LifecycleBinder
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void doTest(View view) {
        ThirdPartyLibrary2 runnable1 = new ThirdPartyLibrary2(Lifecycle.hook(this, new MyInterface2() {
            @Override
            public void doSomethingA() {
                ((TextView) findViewById(R.id.textView1)).setText("DONE A1");
                DialogFragment df = new DialogFragment();
                df.show(getFragmentManager(), "TAG");
            }

            @Override
            public void doSomethingB(String s) {
                ((TextView) findViewById(R.id.textView2)).setText("DONE B " + s);
            }

        }, new Handler(), Lifecycle.Delivery.DEFAULT));

        new Thread(runnable1).start();
    }

}

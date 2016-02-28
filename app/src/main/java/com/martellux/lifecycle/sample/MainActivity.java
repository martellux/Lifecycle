package com.martellux.lifecycle.sample;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.martellux.lifecycle.Lifecycle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Lifecycle.bind(this);
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

        }, new Handler(), Lifecycle.Delivery.DONT_DELIVER_ON_SAVED_INSTANCE));

        new Thread(runnable1).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Lifecycle.restoredInstanceState(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Lifecycle.savedInstanceState(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Lifecycle.unbind(this);
    }

    public interface MyInterface2 {
        public void doSomethingA();
        public void doSomethingB(String s);
    }

}

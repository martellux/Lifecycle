package com.martellux.lifecycle.sample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.martellux.lifecycle.Lifecycle;
import com.martellux.lifecycle.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void doTest(View view) {
        ThirdPartyLibrary2 runnable1 = new ThirdPartyLibrary2(Lifecycle.hook(this, new MyInterface2() {
            @Override
            public void doSomethingA() {
                ((TextView) findViewById(R.id.textView1)).setText("DONE A1");
            }

            @Override
            public void doSomethingB(String s) {
                ((TextView) findViewById(R.id.textView2)).setText("DONE B " + s);
            }
        }, new Handler()));

        new Thread(runnable1).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Lifecycle.bind(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Lifecycle.unbind(this);
    }

    public interface MyInterface2 {
        public void doSomethingA();
        public void doSomethingB(String s);
    }

}

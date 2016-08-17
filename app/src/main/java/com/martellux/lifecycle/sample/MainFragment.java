package com.martellux.lifecycle.sample;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.martellux.lifecycle.Lifecycle;
import com.martellux.lifecycle.annotation.LifecycleBinder;

/**
 * Created by alessandromartellucci on 16/08/16.
 */
public class MainFragment extends Fragment implements View.OnClickListener {

    private TextView button, tv1, tv2;

    @LifecycleBinder
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        button = (TextView)view.findViewById(R.id.button);
        tv1 = (TextView)view.findViewById(R.id.textView1);
        tv2 = (TextView)view.findViewById(R.id.textView2);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        button.setOnClickListener(this);
    }

    @LifecycleBinder
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @LifecycleBinder
    @Override
    public void onStart() {
        super.onStart();
    }

    @LifecycleBinder
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        ThirdPartyLibrary2 runnable1 = new ThirdPartyLibrary2(Lifecycle.hook(this, new MyInterface2() {
            @Override
            public void doSomethingA() {
                tv1.setText("DONE A1");
            }

            @Override
            public void doSomethingB(String s) {
                tv2.setText("DONE B " + s);
            }

        }, new Handler(), Lifecycle.Delivery.DEFAULT));

        new Thread(runnable1).start();
    }
}

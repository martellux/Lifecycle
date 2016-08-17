package com.martellux.lifecycle.sample;

/**
 * Created by alessandromartellucci on 23/01/16.
 */
public class ThirdPartyLibrary2 implements Runnable {

    private MyInterface2 myInterface;

    public ThirdPartyLibrary2(MyInterface2 myInterface) {
        this.myInterface = myInterface;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(3000);
            myInterface.doSomethingA();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

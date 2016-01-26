package com.martellux.lifecycle.sample;

/**
 * Created by alessandromartellucci on 23/01/16.
 */
public class ThirdPartyLibrary1 implements Runnable {

    private ThirdPartyCallbackInterface thirdPartyCallbackInterface;

    public ThirdPartyLibrary1(ThirdPartyCallbackInterface thirdPartyCallbackInterface) {
        this.thirdPartyCallbackInterface = thirdPartyCallbackInterface;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(3000);
            thirdPartyCallbackInterface.doSomethingA();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

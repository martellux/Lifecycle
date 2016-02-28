package com.martellux.lifecycle;

import android.os.Handler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by alessandromartellucci on 25/02/16.
 */
class CallbackInvocationHandler<T> implements InvocationHandler {

    /**
     * The callback reference
     */
    private final T mCallback;
    /**
     * The execution delivery mode
     */
    private final int mDelivery;
    /**
     * The Android Hanlder
     */
    private final Handler mHandler;
    /**
     * The context object's reference name
     */
    private final String mContextReferenceName;
    /**
     * The proxy of callback's reference Java class
     */
    private final Class<T> mProxiedCallbackReferenceName;

    /**
     *
     * @param handler
     * @param contextObject
     * @param callback
     * @param delivery
     */
    CallbackInvocationHandler(Handler handler, Object contextObject, T callback, int delivery) {
        mHandler = handler;
        mDelivery = delivery;
        mCallback = callback;
        mContextReferenceName = CallbackProxyManager.retrieveContextReferenceName(contextObject);
        mProxiedCallbackReferenceName = CallbackProxyManager.retrieveProxiedCallbackReferenceName(callback);
    }

    /**
     * The proxied method. It executes the main business logic
     * @param proxy
     * @param method
     * @param objects
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, final Object[] objects) throws Throwable {
        boolean errorWhileRetrievingReceiver = false;
        Method methodToExecuteTemp = null;
        Object receiverToExecuteTemp = null;
        Object context = CallbackProxyManager.retrieveContext(mContextReferenceName);

        if (context == null) {
            return null;
        }

        Object callbackObject = CallbackProxyManager.retrieveCallbackObject(mContextReferenceName, mProxiedCallbackReferenceName);
        if (callbackObject == null) {
            try {
                Constructor<?> ctor = mCallback.getClass().getDeclaredConstructor(CallbackProxyManager.retrieveContext(mContextReferenceName).getClass());
                ctor.setAccessible(true);
                receiverToExecuteTemp = ctor.newInstance(CallbackProxyManager.retrieveContext(mContextReferenceName));
                methodToExecuteTemp = receiverToExecuteTemp.getClass().getDeclaredMethod(method.getName(), prepareClassArrayParams(objects));

            } catch (IllegalAccessException e) {
                errorWhileRetrievingReceiver = true;
                printStackTrace(e);
            } catch (InvocationTargetException e) {
                errorWhileRetrievingReceiver = true;
                printStackTrace(e);
            } catch (NoSuchMethodException e) {
                errorWhileRetrievingReceiver = true;
                printStackTrace(e);
            }

        } else {
            methodToExecuteTemp = method;
            receiverToExecuteTemp = callbackObject;
        }

        if (!errorWhileRetrievingReceiver) {
            if (mHandler != null) {
                final Method methodToExecute = methodToExecuteTemp;
                final Object receiverToExecute = receiverToExecuteTemp;

                final Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            methodToExecute.invoke(receiverToExecute, objects);
                        } catch (IllegalAccessException e) {
                            printStackTrace(e);
                        } catch (InvocationTargetException e) {
                            printStackTrace(e);
                        }
                    }
                };

                if(mDelivery == Lifecycle.Delivery.DEFAULT || !CallbackProxyManager.hasSavedInstanceState(context)) {
                    mHandler.post(r);
                } else {
                    CallbackProxyManager.enqueueForRelease(context, r);
                }

            } else {
                methodToExecuteTemp.invoke(receiverToExecuteTemp, objects);
            }
        }
        return null;
    }

    /**
     * Utility method for preparing execution method's input parameters
     * @param objects
     * @return
     */
    private Class<?>[] prepareClassArrayParams(Object[] objects) {
        Class<?>[] classArray = null;
        if (objects != null && objects.length > 0) {
            classArray = new Class<?>[objects.length];
            for (int i = 0; i < objects.length; i++) {
                classArray[i] = objects[i].getClass();
            }

        } else {
            classArray = new Class<?>[0];
        }
        return classArray;
    }

    /**
     * Prints the exception stack trace
     * @param exception
     */
    private void printStackTrace(Exception exception) {
        if(Lifecycle.LOG_ENABLED) {
            exception.printStackTrace();
        }
    }
}

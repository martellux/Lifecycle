package com.martellux.lifecycle;

import android.os.Handler;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by alessandromartellucci on 25/02/16.
 */
class CallbackProxyManager {

    /**
     * Handler for
     */
    private static Handler mHandler;
    /**
     * Contains context objects. Association context name - context object
     */
    private static final Map<String, Object> CONTEXTS = new LinkedHashMap<String, Object>();
    /**
     * Contains context objects. Association context name - context object
     */
    private static final Map<Object, Runnable> SAVED_INSTANCE_STATE = new HashMap<Object, Runnable>();
    /**
     * Contains all callback interfaces regarding each context object
     */
    private static final Map<String, Map<Class, Object>> DATA = new HashMap<String, Map<Class, Object>>();


    /**
     * Adds context object to map
     *
     * @param context
     */
    static void bind(Object context) {
        CONTEXTS.put(retrieveContextReferenceName(context), context);
        if(!DATA.containsKey(retrieveContextReferenceName(context))) {
            DATA.put(retrieveContextReferenceName(context), new HashMap<Class, Object>());
        }
    }

    /**
     * Adds context object to map
     *
     * @param context
     */
    static void bind(Object context, boolean restoredInstanceState) {
        CONTEXTS.put(retrieveContextReferenceName(context), context);
        if(!DATA.containsKey(retrieveContextReferenceName(context))) {
            DATA.put(retrieveContextReferenceName(context), new HashMap<Class, Object>());
        }

        if(restoredInstanceState) {

        }
    }

    /**
     * Enqueues the execution, waiting for the context to restore its instance state
     * @param context
     * @param runnable
     */
    static void enqueueForRelease(Object context, Runnable runnable) {
        SAVED_INSTANCE_STATE.put(context, runnable);
    }

    /**
     * Checks if context object has saved its instance state
     * @param context
     * @return
     */
    static boolean hasSavedInstanceState(Object context) {
        return SAVED_INSTANCE_STATE.containsKey(context);
    }

    /**
     * Removes both context and callback object
     *
     * @param context
     */
    static void unbind(Object context) {
        CONTEXTS.remove(retrieveContextReferenceName(context));
        DATA.remove(retrieveContextReferenceName(context));
        SAVED_INSTANCE_STATE.remove(context);
    }

    /**
     * Retrieves the context object by its identifier
     * @param contextReferenceName
     * @param <T>
     * @return
     */
    static <T> Object retrieveContext(String contextReferenceName) {
        return CONTEXTS.get(contextReferenceName);
    }

    /**
     * Creates the proxy for the callback object
     * @param contextObject
     * @param callback
     * @param delivery
     * @param <T>
     * @return
     */
    private static <T> T createProxy(Object contextObject, T callback, int delivery) {
        final ClassLoader classLoader = callback.getClass().getClassLoader();
        final Class proxiedCallbackClass = (Class) callback.getClass().getGenericInterfaces()[0];
        return (T) Proxy.newProxyInstance(classLoader,
                new Class[]{proxiedCallbackClass},
                new CallbackInvocationHandler(mHandler, contextObject, callback, delivery));
    }

    /**
     * Saves association between context object and callback object
     *
     * @param contextObject
     * @param t
     * @param <T>
     */
    static <T> T hookCallbackToContext(final Object contextObject, final T t) {
        Map<Class, Object> callbackData = DATA.get(retrieveContextReferenceName(contextObject));
        callbackData.put(retrieveProxiedCallbackReferenceName(t), t);

        return createProxy(contextObject, t, Lifecycle.Delivery.DEFAULT);
    }

    /**
     * Saves association between context object and callback object
     *
     * @param contextObject
     * @param t
     * @param <T>
     */
    static <T> T hookCallbackToContext(final Object contextObject, final T t, int delivery) {
        Map<Class, Object> callbackData = DATA.get(retrieveContextReferenceName(contextObject));
        callbackData.put(retrieveProxiedCallbackReferenceName(t), t);

        return createProxy(contextObject, t, delivery);
    }

    /**
     * Sets the context objects restoring its instance state and execute the enqueued execution
     * @param context
     */
    static void restoredInstanceState(Object context) {
        final Object bindedContext = retrieveContext(retrieveContextReferenceName(context));
        if(bindedContext == context) {
            Runnable r = SAVED_INSTANCE_STATE.remove(context);
            if (r != null) {
                mHandler.post(r);
            }
        }
    }

    /**
     * Retrieves the name of the context object
     *
     * @param context
     * @return
     */
    static String retrieveContextReferenceName(Object context) {
        return context.getClass().getName();
    }

    /**
     *
     * Retrieves the callback object
     *
     * @param contextClassNameReferenceName
     * @param callbackInterfaceReferenceName
     * @param <T>
     * @return
     */
    static <T> Object retrieveCallbackObject(String contextClassNameReferenceName, Class<T> callbackInterfaceReferenceName) {
        Map<Class, Object> callbackObjectData = DATA.get(contextClassNameReferenceName);
        if(callbackObjectData != null) {
            return callbackObjectData.get(callbackInterfaceReferenceName);
        }

        return null;
    }

    /**
     * Retrieves the name of the proxied callback
     *
     * @param callbackObject
     * @return
     */
    static Class retrieveProxiedCallbackReferenceName(Object callbackObject) {
        Class callbackClass = (Class) callbackObject.getClass();
        return callbackClass;
    }

    /**
     * Saves handler for pushing the response into associated Looper
     * @param handler
     */
    static void saveHandler(Handler handler) {
        mHandler = handler;
    }

    /**
     * Saves the context object instance state
     * @param context
     */
    static void savedInstanceState(Object context) {
        SAVED_INSTANCE_STATE.put(context, null);
    }
}

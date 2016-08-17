package com.martellux.lifecycle;

import android.os.Handler;

/**
 * Created by alessandromartellucci on 23/01/16.
 *
 */
public final class Lifecycle {

    /**
     * Log status main reference
     */
    static boolean LOG_ENABLED = false;

    /**
     * Execution delivery mode
     */
    public final class Delivery {
        /**
         * Delivers the execution without any constraints
         */
        public static final int DEFAULT = 0;
        /**
         * Don't deliver the execution if context object has saved its instance state.
         * It will deliver the enqueued execution when the context will have restored its instance state
         */
        public static final int DONT_DELIVER_ON_SAVED_INSTANCE = 1;
    }

    /**
     * Adds caller object to map
     *
     * @param caller
     * @deprecated in version 0.3.0. Use @annotation LifecycleBinder
     */
    @Deprecated
    public static void bind(Object caller) {
        CallbackProxyManager.bind(caller);
    }

    /**
     * Adds caller object to map
     *
     * @param caller
     * @param restoredInstanceState
     * @deprecated in version 0.3.0. Use @annotation LifecycleBinder
     */
    @Deprecated
    public static void bind(Object caller, boolean restoredInstanceState) {
        CallbackProxyManager.bind(caller, restoredInstanceState);
    }

    /**
     * Collect callback object and creates the proxy
     *
     * @param contextObject
     * @param t
     * @param <T>
     * @return
     */
    public static <T> T hook(Object contextObject, final T t) {
        return hook(contextObject, t, null, Delivery.DEFAULT);
    }

    /**
     * Collect callback object and creates the proxy
     *
     * @param contextObject
     * @param callback
     * @param handler
     * @param delivery
     * @return
     */
    public static <T> T hook(Object contextObject, T callback, Handler handler, int delivery) {
        CallbackProxyManager.saveHandler(handler);
        switch (delivery) {
            case Delivery.DEFAULT:
                return CallbackProxyManager.hookCallbackToContext(contextObject, callback);

            case Delivery.DONT_DELIVER_ON_SAVED_INSTANCE:
                return CallbackProxyManager.hookCallbackToContext(contextObject, callback, delivery);
        }

        throw new IllegalStateException("Unsupported delivery method!");
    }

    /**
     * Marks the caller's instance state as saved
     * @param caller
     * @deprecated in version 0.3.0. Use @annotation LifecycleBinder
     */
    @Deprecated
    public static void restoredInstanceState(Object caller) {
        CallbackProxyManager.restoredInstanceState(caller);
    }

    /**
     * Marks the caller's instance state as restored
     * @param caller
     * @deprecated in version 0.3.0. Use @annotation LifecycleBinder
     */
    @Deprecated
    public static void savedInstanceState(Object caller) {
        CallbackProxyManager.savedInstanceState(caller);
    }

    /**
     * Sets the log status. "true" for enabling and "false" false for disabling
     * @param enabled
     */
    public static void setLogEnabled(boolean enabled) {
        LOG_ENABLED = enabled;
    }

    /**
     * Removes both caller and callback object
     *
     * @param caller
     * @deprecated in version 0.3.0. Use @annotation LifecycleBinder
     */
    @Deprecated
    public static void unbind(Object caller) {
        CallbackProxyManager.unbind(caller);
    }
}

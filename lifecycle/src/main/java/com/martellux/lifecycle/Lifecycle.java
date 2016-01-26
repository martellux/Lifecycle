package com.martellux.lifecycle;

import android.os.Handler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by alessandromartellucci on 23/01/16.
 */
public final class Lifecycle {

    /**
     * Handler for
     */
    private static Handler mHandler;
    /**
     * Contains context objects. Association context name - context object
     */
    private static final Map<String, Object> CONTEXTS = new LinkedHashMap<String, Object>();
    /**
     * Contains all callback interfaces regarding each context object
     */
    private static final Map<String, Map<Class, Object>> DATA = new HashMap<String, Map<Class, Object>>();

    /**
     * Creates the proxy for the callback interface
     * @param contextObject
     * @param t
     * @param <T>
     * @return
     */
    private static <T> T createProxy(final Object contextObject, final T t) {
        final ClassLoader classLoader = t.getClass().getClassLoader();
        final String contextReferenceName = retrieveContextReferenceName(contextObject);
        final Class<T> proxiedCallbackReferenceName = retrieveProxiedCallbackReferenceName(t);
        final Class proxiedCallbackClass = (Class) t.getClass().getGenericInterfaces()[0];

        return (T) Proxy.newProxyInstance(classLoader, new Class[]{proxiedCallbackClass}, new InvocationHandler() {

            /**
             * @param objects
             * @return
             */
            private Class<?>[] prepareClassArrayParams(Object[] objects) {
                Class<?>[] classArray = null;
                if(objects != null && objects.length > 0) {
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
             * @param o
             * @param method
             * @param objects
             * @return
             * @throws Throwable
             */
            @Override
            public Object invoke(Object o, final Method method, final Object[] objects) throws Throwable {
                boolean errorWhileRetrievingReceiver = false;
                Method methodToExecuteTemp = null;
                Object receiverToExecuteTemp = null;
                Object context = retrieveContext(contextReferenceName);

                if (context == null) {
                    return null;
                }

                Object callbackObject = retrieveCallbackObject(contextReferenceName, proxiedCallbackReferenceName);
                if (callbackObject == null) {
                    try {
                        Constructor<?> ctor = t.getClass().getDeclaredConstructor(CONTEXTS.get(contextReferenceName).getClass());
                        ctor.setAccessible(true);
                        receiverToExecuteTemp = ctor.newInstance(CONTEXTS.get(contextReferenceName));
                        methodToExecuteTemp = receiverToExecuteTemp.getClass().getDeclaredMethod(method.getName(), prepareClassArrayParams(objects));

                    } catch (IllegalAccessException e) {
                        errorWhileRetrievingReceiver = true;
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        errorWhileRetrievingReceiver = true;
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        errorWhileRetrievingReceiver = true;
                        e.printStackTrace();
                    }

                } else {
                    methodToExecuteTemp = method;
                    receiverToExecuteTemp = callbackObject;
                }

                if(!errorWhileRetrievingReceiver) {
                    if (mHandler != null) {
                        final Method methodToExecute = methodToExecuteTemp;
                        final Object receiverToExecute = receiverToExecuteTemp;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    methodToExecute.invoke(receiverToExecute, objects);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } else {
                        methodToExecuteTemp.invoke(receiverToExecuteTemp, objects);
                    }
                }
                return null;
            }
        });
    }

    /**
     * Saves association between context object and callback object
     *
     * @param contextObject
     * @param t
     * @param <T>
     */
    private static <T> void hookCallbackToCaller(final Object contextObject, final T t) {
        Map<Class, Object> callbackData = DATA.get(retrieveContextReferenceName(contextObject));
        callbackData.put(retrieveProxiedCallbackReferenceName(t), t);
    }

    /**
     * Retrieves the name of the context object
     *
     * @param caller
     * @return
     */
    private static String retrieveContextReferenceName(Object caller) {
        return caller.getClass().getName();
    }

    /**
     *
     * Retrieves the callback object
     *
     * @param callerClassNameReferenceName
     * @param callbackInterfaceReferenceName
     * @param <T>
     * @return
     */
    private static <T> Object retrieveCallbackObject(String callerClassNameReferenceName, Class<T> callbackInterfaceReferenceName) {
        Map<Class, Object> callbackObjectData = DATA.get(callerClassNameReferenceName);
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
    private static Class retrieveProxiedCallbackReferenceName(Object callbackObject) {
        Class callbackClass = (Class) callbackObject.getClass();
        return callbackClass;
    }

    /**
     *
     * @param callerReferenceName
     * @param <T>
     * @return
     */
    private static <T> Object retrieveContext(String callerReferenceName) {
        return CONTEXTS.get(callerReferenceName);
    }

    /**
     * Saves handler for pushing the response into associated Looper
     * @param handler
     */
    private static void saveHandler(Handler handler) {
        mHandler = handler;
    }

    /**
     * Adds caller object to map
     *
     * @param caller
     */
    public static void bind(Object caller) {
        CONTEXTS.put(retrieveContextReferenceName(caller), caller);
        if(!DATA.containsKey(retrieveContextReferenceName(caller))) {
            DATA.put(retrieveContextReferenceName(caller), new HashMap<Class, Object>());
        }
    }

    /**
     * Removes both caller and callback object
     *
     * @param caller
     */
    public static void unbind(Object caller) {
        CONTEXTS.remove(retrieveContextReferenceName(caller));
        DATA.remove(retrieveContextReferenceName(caller));
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
        return hook(contextObject, t, null);
    }

    /**
     * Collect callback object and creates the proxy
     *
     * @param contextObject
     * @param t
     * @param handler
     * @param <T>
     * @return
     */
    public static <T> T hook(Object contextObject, T t, Handler handler) {
        saveHandler(handler);
        hookCallbackToCaller(contextObject, t);
        return createProxy(contextObject, t);
    }
}

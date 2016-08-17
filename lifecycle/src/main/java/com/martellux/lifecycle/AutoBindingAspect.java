package com.martellux.lifecycle;

import android.os.Bundle;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Modifier;

/**
 * Created by alessandromartellucci on 16/08/16.
 */
@Aspect
public class AutoBindingAspect {

    /**
     * Defines pointcut for methods annotated with LifecycleBinder annotation
     */
    @Pointcut("execution(@com.martellux.lifecycle.annotation.LifecycleBinder * *(..))")
    public void methodAnnotatedWithLifecycle() {}

    /**
     * Defines joinpoint for methods annotated with LifecycleBinder annotation
     *
     * @param joinPoint
     * @return the execution of th emethod annotated with LifecycleBinder annotation
     * @throws Throwable
     */
    @Around("methodAnnotatedWithLifecycle()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        if(isValidOnCreateMethod(methodSignature)) {
            CallbackProxyManager.bind(joinPoint.getTarget());

        } else if(isValidOnDestroyMethod(methodSignature)) {
            CallbackProxyManager.unbind(joinPoint.getTarget());

        } else if(isValidOnStartMethod(methodSignature)) {
            CallbackProxyManager.restoredInstanceState(joinPoint.getTarget());

        } else if(isValidOnStopMethod(methodSignature)) {
            CallbackProxyManager.savedInstanceState(joinPoint.getTarget());
        }

        Object result = joinPoint.proceed();
        return result;
    }

    /**
     * Checks if the current method is a valid Activity.onCreate method or Fragment.onCreate method
     * @param methodSignature
     * @return true if the current method is a valid Activity.onCreate method or Fragment.onCreate method. False otherwise
     */
    private boolean isValidOnCreateMethod(MethodSignature methodSignature) {
        final boolean activityModifier = Modifier.isProtected(methodSignature.getModifiers());
        final boolean fragmentModifier = Modifier.isPublic(methodSignature.getModifiers());
        final boolean returningType = methodSignature.getReturnType() == void.class;
        final boolean name = "onCreate".equalsIgnoreCase(methodSignature.getName());
        final boolean parameterTypes = methodSignature.getParameterTypes() != null && methodSignature.getParameterTypes().length == 1 && methodSignature.getParameterTypes()[0] == Bundle.class;

        if((activityModifier || fragmentModifier) && returningType && name && parameterTypes) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the current method is a valid Activity.onDestroy method or Fragment.onDestroy method
     * @param methodSignature
     * @return true if the current method is a valid Activity.onDestroy method or Fragment.onDestroy method. False otherwise
     */
    private boolean isValidOnDestroyMethod(MethodSignature methodSignature) {
        final boolean activityModifier = Modifier.isProtected(methodSignature.getModifiers());
        final boolean fragmentModifier = Modifier.isPublic(methodSignature.getModifiers());
        final boolean returningType = methodSignature.getReturnType() == void.class;
        final boolean name = "onDestroy".equalsIgnoreCase(methodSignature.getName());
        final boolean parameterTypes = methodSignature.getParameterTypes() == null || methodSignature.getParameterTypes().length == 0;

        if((activityModifier || fragmentModifier) && returningType && name && parameterTypes) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the current method is a valid Activity.onStart method or Fragment.onStart method
     * @param methodSignature
     * @return true if the current method is a valid Activity.onStart method or Fragment.onStart method. False otherwise
     */
    private boolean isValidOnStartMethod(MethodSignature methodSignature) {
        final boolean activityModifier = Modifier.isProtected(methodSignature.getModifiers());
        final boolean fragmentModifier = Modifier.isPublic(methodSignature.getModifiers());
        final boolean returningType = methodSignature.getReturnType() == void.class;
        final boolean name = "onStart".equalsIgnoreCase(methodSignature.getName());
        final boolean parameterTypes = methodSignature.getParameterTypes() == null || methodSignature.getParameterTypes().length == 0;

        if((activityModifier || fragmentModifier) && returningType && name && parameterTypes) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the current method is a valid Activity.onStop method or Fragment.onStop method
     * @param methodSignature
     * @return true if the current method is a valid Activity.onStop method or Fragment.onStop method. False otherwise
     */
    private boolean isValidOnStopMethod(MethodSignature methodSignature) {
        final boolean activityModifier = Modifier.isProtected(methodSignature.getModifiers());
        final boolean fragmentModifier = Modifier.isPublic(methodSignature.getModifiers());
        final boolean returningType = methodSignature.getReturnType() == void.class;
        final boolean name = "onStop".equalsIgnoreCase(methodSignature.getName());
        final boolean parameterTypes = methodSignature.getParameterTypes() == null || methodSignature.getParameterTypes().length == 0;

        if((activityModifier || fragmentModifier) && returningType && name && parameterTypes) {
            return true;
        } else {
            return false;
        }
    }
}

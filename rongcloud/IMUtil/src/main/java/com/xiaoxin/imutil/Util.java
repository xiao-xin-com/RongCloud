package com.xiaoxin.imutil;

import android.annotation.SuppressLint;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

class Util {

    static void invokeMethod(String className, String name, Class<?>[] parameterTypes, Object obj, Object... args) {
        try {
            Class<?> clazz = Class.forName(className);
            invokeMethod(clazz, name, parameterTypes, obj, args);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    static void invokeMethod(Class<?> clazz, String name, Class<?>[] parameterTypes, Object obj, Object... args) {
        try {
            Method mMethod = clazz.getDeclaredMethod(name, parameterTypes);
            if (!Modifier.isPublic(mMethod.getModifiers())) {
                mMethod.setAccessible(true);
            }
            mMethod.invoke(obj, args);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    static void setField(String className, String field, Object obj, Object value) {
        try {
            Class<?> clazz = Class.forName(className);
            setField(clazz, field, obj, value);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    static void setField(Class<?> clazz, String field, Object obj, Object value) {
        try {
            Field mField = clazz.getDeclaredField(field);
            if (!Modifier.isPublic(mField.getModifiers())) {
                mField.setAccessible(true);
            }
            mField.set(obj, value);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /* 获取魅族系统操作版本标识*/
    public static boolean isMeizuFlymeOS() {
        String meizuFlymeOSFlag = getSystemProperty("ro.build.display.id", "");
        if (meizuFlymeOSFlag == null) {
            return false;
        } else {
            return meizuFlymeOSFlag.contains("flyme") ||
                    meizuFlymeOSFlag.toLowerCase().contains("flyme");
        }
    }

    private static String getSystemProperty(String key, String defaultValue) {
        try {
            @SuppressLint("PrivateApi") Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String) get.invoke(clz, key, defaultValue);
        } catch (Exception e) {
            return null;
        }
    }

}

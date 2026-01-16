package com.youtyan.apoex.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeUtil {
    private static final Unsafe UNSAFE;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Could not get Unsafe", e);
        }
    }

    public static void setField(Object object, Field field, Object value) {
        try {
            long offset = UNSAFE.objectFieldOffset(field);
            UNSAFE.putObject(object, offset, value);
        } catch (Exception e) {
            throw new RuntimeException("Could not set field " + field.getName(), e);
        }
    }
}

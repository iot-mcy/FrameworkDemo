package com.mcy.framework.utils;

import java.lang.reflect.Field;

public class ReflectionUtil {
    /**
     * 获取类里面的指定对象，如果该类没有则从父类查询
     */
    public static Field getField(Class clazz, String name) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            try {
                field = clazz.getField(name);
            } catch (NoSuchFieldException e1) {
                if (clazz.getSuperclass() == null) {
                    return field;
                } else {
                    field = getField(clazz.getSuperclass(), name);
                }
            }
        }
        if (field != null) {
            field.setAccessible(true);
        }
        return field;
    }
}

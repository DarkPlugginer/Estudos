package me.dark.packets;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class ClassReflection {

    public static Object newInstance(Class<?> clazz, Object... param)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        final Class<?>[] clazzArr = new Class[2];
        for (int i = 0; i < 2; i++) {
            clazzArr[i] = param[i].getClass();
        }
        return getConstructor(clazz, clazzArr).newInstance(param);
    }

    public static Object getField(String fieldName, Object value, int iteration)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        return declaredField(fieldName, value.getClass(), iteration).get(value);
    }

    public static void setField(String fieldName, Object value, Object clazz, int iteration)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        declaredField(fieldName, clazz.getClass(), iteration).set(clazz, value);
    }

    private static Constructor<?> getConstructor(Class<?> clazz, Class<?>... clazzArr)
            throws NoSuchMethodException, SecurityException {
        final Constructor<?> declaredConstructor = clazz.getDeclaredConstructor(clazzArr);
        declaredConstructor.setAccessible(true);
        return declaredConstructor;
    }

    private static Field declaredField(String fieldName, Class<?> clazz, int iteration)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        final Field declaredField = superclassIteration(clazz, iteration).getDeclaredField(fieldName);
        setPrivateField(declaredField);
        return declaredField;
    }

    private static Class<?> superclassIteration(Class<?> clazz, int toRepeat) {
        for (int i = 0; i < toRepeat; i++) {
            clazz = clazz.getSuperclass();
        }
        return clazz;
    }

    private static void setPrivateField(Field field)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        field.setAccessible(true);
        if (Modifier.isFinal(field.getModifiers())) {
            final Field modifierField = Field.class.getDeclaredField("modifiers");
            modifierField.setAccessible(true);
            modifierField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        }
    }

}

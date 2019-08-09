package me.mikusjelly.zerolib.util;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import dalvik.system.DexClassLoader;

public class Reflect {

    private static ArrayList<DexClassLoader> mDexClassLoaders = null;

    public static void setDexClassLoaders(final ArrayList<DexClassLoader> dexClassLoaders) {
        mDexClassLoaders = dexClassLoaders;
    }

    public static Class<?> loadClass(String className) throws ClassNotFoundException {
        Class<?> clz = null;
        for (DexClassLoader dcl : mDexClassLoaders) {
            clz = dcl.loadClass(className);
        }

        return clz;
    }

    /**
     * 无参实例化
     *
     * @param clazz
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static Object newInstance(Class<?> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return clazz.getDeclaredConstructor().newInstance();
    }

    // TODO 多参数，实例化
//    public static Object newInstance(Class<?> clazz, Object... args) {
//        /**
//         * 需要两个参数
//         * 1。 参数类型
//         * 2。 参数的实例化对象
//         */
//        try {
//            clazz.getConstructor().newInstance(args);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * 实例化
     *
     * @param instance  实例化的类
     * @param fieldName field名称
     * @return 对应的值 TODO 需要转换为对应的类型
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static Object getField(Object instance, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = getField(instance.getClass(), fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }

    private static Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
        if (clazz == null) {
            throw new NoSuchFieldException("Error!");
        }
        return clazz.getDeclaredField(fieldName);
    }

    /**
     * 方法的参数
     */
    class Parameter {
        Class<?> parameterType;
        Object parameter;

        public Parameter(Object data) {
            parameter = data;
            if (data == null) {
                parameterType = null;
            } else if (data instanceof Integer) {
                parameterType = Integer.class;
            } else if (data instanceof Float) {
                parameterType = Float.class;
            } else if (data instanceof Double) {
                parameterType = Double.class;
            } else if (data instanceof Long) {
                parameterType = Long.class;
            } else if (data instanceof String) {
                parameterType = String.class;
            } else if (data instanceof Boolean) {
                parameterType = Boolean.class;
            } else if (data instanceof byte[]) {
                parameterType = byte[].class;
            } else if (data instanceof Object[]) {
                parameterType = Object[].class;
            }
        }

        public Parameter(Class clazz, Object obj) {
            parameterType = clazz;
            parameter = obj;
        }
    }

    public static Object jsonArray2Array(JSONArray jsonArray, String aType) throws JSONException {
        int len = jsonArray.length();
        System.out.println(aType);
        if (aType.equals("byte[]")) {
            System.out.println("BBBBBB");
            byte[] bytes = new byte[len];
            for (int i = 0; i < len; i++) {
                bytes[i] = (byte) jsonArray.getInt(i);
            }

            return bytes;
        }

        return null;
    }

    public static Class<?> toType(Object data) {
        if (data instanceof Integer) {
            return int.class;
        }
        if (data instanceof Float) {
            return Float.class;
        }
        if (data instanceof Double) {
            return Double.class;
        }
        if (data instanceof Long) {
            return Long.class;
        }
        if (data instanceof Character) {
            return Character.class;
        }
        if (data instanceof String) {
            return String.class;
        }
        if (data instanceof JSONArray) {
            return JSONArray.class;
        }
        if (data instanceof Boolean) {
            return Boolean.class;
        }
        if (data instanceof byte[]) {
            return byte[].class;
        }
        if (data instanceof Byte[]) {
            return Byte[].class;
        }
        if (data instanceof char[]) {
            return char[].class;
        }
        if (data instanceof String[]) {
            return String[].class;
        }
        if (data instanceof Object[]) {
            return Object[].class;
        }

        return Object.class;
    }

    /**
     * 这里调用的方法都是针对基本类型
     */

    /**
     * 这里遇到一个问题，就是参数不好传
     * private Class<?>[] parameterTypes;
     * private Object[] parameters;
     *
     * @param methodName
     * @param jsonArray
     * @return
     */
    public static Object invokeMethod(String methodName, JSONArray jsonArray) {
        Class<?>[] parameterTypes = null;
        Object[] parameters = null;

//        for (Object obj : objects) {
//            System.out.println(toType(obj));
//        }
        return null;
    }

    /**
     * 调用静态方法
     * @param clazz
     * @param methodName
     * @param args
     * @param argTypes
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("JavadocReference")
    public static Object invokeStaticMethod(Class<?> clazz, String methodName, JSONArray args, JSONArray argTypes) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int len = args.length();
        if (len == 0) {
            // 无参
            return null;
        }

        Class<?>[] parameterTypes = new Class[len];
        Object[] parameters = new Object[len];

        try {
            Object one = args.get(0);
            System.out.println(one);
            System.out.println(one.getClass());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < args.length(); i++) {
            try {
                Object parameter = args.get(i);
                Class<?> aType = toType(args.get(i));
                if (aType == JSONArray.class) {
                    System.out.println("???????");
                    System.out.println(aType);
                    System.out.println(argTypes.getString(i).getClass());
                    Object arrayObj = jsonArray2Array((JSONArray) parameter, argTypes.getString(i));
                    System.out.println(arrayObj);
                    parameterTypes[i] = toType(arrayObj);
                    parameters[i] = arrayObj;
                } else {
                    parameterTypes[i] = aType;
                    parameters[i] = parameter;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return getMethod(clazz, methodName, parameterTypes).invoke(clazz, parameters);
    }

    /**
     * 调用非静态方法
     *
     * @param instance
     * @param methodName
     * @param jsonArray
     * @return
     */
    public static Object invokeMethod(Object instance, String methodName, JSONArray jsonArray) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int len = jsonArray.length();
        if (len == 0) {
            // 无参
            return null;
        }

        Class<?>[] parameterTypes = new Class[len];
        Object[] parameters = new Object[len];

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                System.out.println(toType(jsonArray.get(i)));
                parameters[i] = jsonArray.get(i);
                parameterTypes[i] = toType(jsonArray.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return getMethod(instance, methodName, parameterTypes).invoke(instance, parameters);
    }


    // 静态调用
    private static Method getMethod(Class<?> clz, String methodName, Class<?>[] parameterTypes) throws NoSuchMethodException {
        if (clz == null) {
            throw new NoSuchMethodException("Could not find the method");
        }
        Method mtd = null;
        try {
            System.out.println(methodName);
            mtd = clz.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return mtd;
    }

    // 实例化调用
    public static Method getMethod(Object instance, String methodName, Class<?>[] parameterTypes) throws NoSuchMethodException {
        Method method = getMethod(instance.getClass(), methodName, parameterTypes);
        if (method == null) {
            return null;
        }
        method.setAccessible(true);
        return method;
    }

}

package mikusjelly.zero;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;

import me.mikusjelly.zerolib.dl.PluginManager;
import me.mikusjelly.zerolib.rpc.JsonBuilder;
import me.mikusjelly.zerolib.rpc.JsonRpcResult;
import me.mikusjelly.zerolib.rpc.JsonRpcServer;
import me.mikusjelly.zerolib.util.Global;
import me.mikusjelly.zerolib.util.Reflect;

public class MyRPC extends JsonRpcServer {
    PluginManager mPluginManager;

    public MyRPC(Context context) {
        super(context);
        mPluginManager = PluginManager.getInstance(context);
//      先将dex推送到该目录
        mPluginManager.initPlugins("/data/local/tmp/plugins");
        Reflect.setDexClassLoaders(mPluginManager.getClassLoaders());
    }

    @Override
    public JSONObject processData(int id, String method, JSONArray params) throws JSONException {
        if (method.equals("test")) {
            return JsonRpcResult.result(id, "TEST.");
        }

        if (method.equals("hello")) {
            return JsonRpcResult.result(id, "Hello World!");
        }

        if (method.equals("GetFieldValue")) {
            Log.d(Global.TAG, "调用静态参数");
            String className = params.getJSONObject(0).getString("className");
            String fieldName = params.getJSONObject(0).getString("fieldName");

            Object result = null;
            try {
                Class<?> aClass1 = Reflect.loadClass(className);
                Object obj = Reflect.newInstance(aClass1);

                result = Reflect.getField(obj, fieldName);
                System.out.println(result.getClass());
                System.out.println(JsonBuilder.build(result));
                System.out.println(Byte[].class);
                if (result.getClass().equals(byte[].class)) {
                    Log.d(Global.TAG, "类型一样");
                }

//                Reflect.invokeMethod("", new Object[]{1, 'a', "String"});
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            return JsonRpcResult.result(id, result);
        }

        if (method.equals("InvokeStaticMethod")) {
            Log.d(Global.TAG, "调用静态方法" + params);
            String className = params.getJSONObject(0).getString("className");
            String methodName = params.getJSONObject(0).getString("methodName");
            JSONArray arguments = params.getJSONObject(0).getJSONArray("arguments");
            JSONArray argumentTypes = null;
            try {
                argumentTypes = params.getJSONObject(0).getJSONArray("argumentTypes");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Class<?> aClass1 = null;

            try {
                aClass1 = Reflect.loadClass(className);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            Object result = null;
            try {
                result = Reflect.invokeStaticMethod(aClass1, methodName, arguments, argumentTypes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                System.out.println(e.getLocalizedMessage());
                System.out.println(e.getMessage());
                return JsonRpcResult.error(id, e.getCause());
            } catch (IllegalAccessException e) {
                return JsonRpcResult.error(id, e.getCause());
            } catch (InvocationTargetException e) {
                return JsonRpcResult.error(id, e.getCause());
            }
            return JsonRpcResult.result(id, result);
        }

        if (method.equals("InvokeMethod")) {
            Log.e(Global.TAG, params.toString());
//            String className = params.getJSONObject(0).getString("className");
//            String methodName = params.getJSONObject(0).getString("methodName");
//            JSONArray args = params.getJSONObject(0).getJSONArray("args");
//            for (int i = 0; i < args.length(); i++) {
//                System.out.println(Reflect.invokeMethod("", args));
//            }
        }

        Log.e(Global.TAG, "Not Supported Method!");
        return JsonRpcResult.result(id, "Not supported method " + method);


        //            JSONObject returnValue = mRpcUtil.invokeRpc(method, params, id, UID);
    }
}

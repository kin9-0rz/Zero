/*
 * Copyright (C) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package me.mikusjelly.zerolib.rpc;


import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import me.mikusjelly.zerolib.util.Log;

/**
 * A JSON RPC server that forwards RPC calls to a specified receiver object.
 * <p>
 */
public class JsonRpcServer extends SimpleServer {
    private Context mContext;

    public JsonRpcServer(Context context) {
        mContext = context;
    }

    @Override
    protected void handleRPCConnection(
            Socket sock, Integer UID, BufferedReader reader, PrintWriter writer) throws Exception {
        Log.d("UID " + UID);
        String data;
        while ((data = reader.readLine()) != null) {
            Log.v("JsonRpcServer handleRPCConnection, Session " + UID + " Received: " + data);

            JSONObject request = new JSONObject(data);
            int id = request.getInt("id");
            String method = request.getString("method");
            JSONArray params = request.getJSONArray("params");

            JSONObject returnValue = processData(id, method, params);
            send(writer, returnValue, UID);

        }
    }

    /**
     * 数据的处理需要自行处理
     * @param id id
     * @param method RPC方法
     * @param params 具体需要操作的数据
     * @return 请使用 {@link JsonRpcResult}，生成返回结果
     * @throws JSONException
     */
    @SuppressWarnings("JavaDoc")
    public JSONObject processData(int id, String method, JSONArray params) throws JSONException {
        String result = "Please OVERRIDE the processData method!";
        Log.w(result);
        return JsonRpcResult.result(id, result);

    }

    private void send(PrintWriter writer, JSONObject result, int UID) {
        writer.write(result + "\n");
        writer.flush();
        Log.v("Session " + UID + " Sent: " + result);
    }

    @Override
    protected void handleConnection(Socket socket) throws Exception {
    }
}

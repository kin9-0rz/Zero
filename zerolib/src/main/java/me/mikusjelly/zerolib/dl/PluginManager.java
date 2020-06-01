/*
 * MIT License
 *
 * Copyright (c) 2017 mikusjelly
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.mikusjelly.zerolib.dl;

import android.content.Context;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import dalvik.system.DexClassLoader;


public class PluginManager {
    private static WeakReference<PluginManager> weakReferenceInstance;
    private static ArrayList<DexClassLoader> mDexClassLoaders = new ArrayList<>();
    private Context mContext;
    private String mNativeLibDir = null;

    private PluginManager(Context context) {
        mContext = context.getApplicationContext();
        mNativeLibDir = mContext.getDir("plugins", Context.MODE_PRIVATE).getAbsolutePath();
    }

    public static PluginManager getInstance(Context context) {
        if (weakReferenceInstance == null || weakReferenceInstance.get() == null) {
            weakReferenceInstance = new WeakReference<>(new PluginManager(context));
        }
        return weakReferenceInstance.get();
    }

    /**
     * 初始化Dex插件
     *
     * @param pluginPath 插件路径
     */
    public void initPlugins(String pluginPath) {
        File file = new File(pluginPath);
        File[] plugins = file.listFiles();
        if (plugins == null) {
            return;
        }
        for (File plugin : plugins) {
            this.loadApk(plugin.getAbsolutePath());
        }
    }

    /**
     * Load a apk. Before start a plugin Activity, we should do this first.<br/>
     * NOTE : will only be called by host apk.
     *
     * @param dexPath APK 路径
     */
    public void loadApk(String dexPath) {
        // when loadApk is called by host apk, we assume that plugin is invoked
        // by host.
        loadApk(dexPath, true);
    }

    /**
     * @param dexPath  plugin path
     * @param hasSoLib whether exist so lib in plugin
     * @return void
     */
    private void loadApk(final String dexPath, boolean hasSoLib) {
        mDexClassLoaders.add(createDexClassLoader(dexPath));

//        if (hasSoLib) {
//            copySoLib(dexPath);
//        }

    }

    private DexClassLoader createDexClassLoader(String dexPath) {
        File dexOutputDir = mContext.getDir("dex", Context.MODE_PRIVATE);
        String dexOutputPath = dexOutputDir.getAbsolutePath();
        return new DexClassLoader(dexPath, dexOutputPath, mNativeLibDir, mContext.getClassLoader());
    }

    /**
     * copy .so file to pluginlib dir.
     *
     * @param dexPath dex path
     */
    private void copySoLib(String dexPath) {
        // TODO: copy so lib async will lead to bugs maybe, waiting for
        // resolved later.

        // TODO : use wait and signal is ok ? that means when copying the
        // .so files, the main thread will enter waiting status, when the
        // copy is done, send a signal to the main thread.
        // new Thread(new CopySoRunnable(dexPath)).start();

        SoLibManager.getSoLoader().copyPluginSoLib(mContext, dexPath, mNativeLibDir);
    }

    public ArrayList<DexClassLoader> getClassLoaders() {
        return mDexClassLoaders;
    }

    public Class<?> loadClass(String className) throws ClassNotFoundException {
        Class<?> clz = null;
        for (DexClassLoader dcl : mDexClassLoaders) {
            clz = dcl.loadClass(className);
        }

        return clz;
    }
}

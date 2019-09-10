package cn.nexus6p.QQMusicNotify.Base;

import android.content.Context;

import base.BasicParam;

public abstract class BasicInit implements HookInterface {

    public BasicParam basicParam = new BasicParam();
    public ClassLoader classLoader;
    public String packageName;

    @Override
    public final BasicInit setPackageName(String mPackageName) {
        packageName = mPackageName;
        return this;
    }

    @Override
    public final BasicInit setClassLoader(ClassLoader mClassLoader) {
        classLoader = mClassLoader;
        return this;
    }

    @Override
    public final BasicInit setContext(Context mContext) {
        basicParam.setContext(mContext);
        return this;
    }
}

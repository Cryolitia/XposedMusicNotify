package cn.nexus6p.QQMusicNotify.Base;

import android.content.Context;

public abstract class BasicInit implements HookInterface {

    public Context context;
    public ClassLoader classLoader;

    @Override
    public final BasicInit setClassLoader(ClassLoader mClassLoader) {
        classLoader = mClassLoader;
        return this;
    }

    @Override
    public final BasicInit setContext(Context mContext) {
        context = mContext;
        return this;
    }
}

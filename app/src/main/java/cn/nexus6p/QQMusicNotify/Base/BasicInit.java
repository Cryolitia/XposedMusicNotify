package cn.nexus6p.QQMusicNotify.Base;

import android.content.Context;

public abstract class BasicInit implements HookInterface {

    public BasicParam basicParam = new BasicParam();
    public ClassLoader classLoader;

    @Override
    public abstract void init();

    @Override
    public void initBefore() {
        init();
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

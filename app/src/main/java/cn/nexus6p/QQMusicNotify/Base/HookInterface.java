package cn.nexus6p.QQMusicNotify.Base;

import android.content.Context;

public interface HookInterface {

    void init();

    void initBefore();

    HookInterface setClassLoader(ClassLoader classLoader);

    HookInterface setContext(Context context);
}

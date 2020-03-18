package cn.nexus6p.QQMusicNotify.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Keep;

@Keep
public class HookStatue {
    public static Boolean isEnabled() {
        Log.d("XposedMusicNotify", "模块未激活");
        return false;
    }

    public static int isExpModuleActive(Context context) {

        int isExp = 0;
        if (context == null) {
            throw new IllegalArgumentException("context must not be null!!");
        }

        try {
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = Uri.parse("content://me.weishu.exposed.CP/");
            Bundle result = null;
            try {
                result = contentResolver.call(uri, "active", null, null);
            } catch (RuntimeException e) {
                // TaiChi is killed, try invoke
                try {
                    Intent intent = new Intent("me.weishu.exp.ACTION_ACTIVE");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Throwable e1) {
                    return 0;
                }
            }
            if (result == null) {
                result = contentResolver.call(uri, "active", null, null);
            }

            if (result == null) {
                return 0;
            }
            isExp = !result.getBoolean("active", false) ? 1 : 2;
        } catch (Throwable ignored) {
        }
        return isExp;
    }
}

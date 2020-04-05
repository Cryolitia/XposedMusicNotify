package cn.nexus6p.QQMusicNotify.Hook;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.session.MediaSession;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.Keep;

import cn.nexus6p.QQMusicNotify.Base.BasicViewNotification;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

@Keep
public class comsingclient extends BasicViewNotification {

    private static MediaSession.Token mTOKEN;

    @Override
    public void init() {

        Class songClazz = XposedHelpers.findClass("com.sing.client.model.Song", classLoader);
        XposedHelpers.findAndHookMethod("com.kugou.common.player.manager.b", classLoader, "h", songClazz, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                oldNotification = (Notification) XposedHelpers.getObjectField(param.thisObject, "b");
                basicParam.setContext((Context) XposedHelpers.getObjectField(param.thisObject, "a"));
                basicParam.setIconID(0x7f0207f1);
                titleID = 0x7f100bf8;
                textID = 0x7f100bf9;
                bitmapID = 0x7f100bf7;
                RemoteViews remoteViews = (RemoteViews) XposedHelpers.getObjectField(oldNotification,"bigContentView");
                View view = remoteViews.apply(basicParam.getContext(), null);
                try {
                    basicParam.setBitmap(((BitmapDrawable) ((ImageView) view.findViewById(bitmapID)).getDrawable()).getBitmap());
                    basicParam.setTextString(((TextView) view.findViewById(titleID)).getText());
                    basicParam.setTextString(((TextView) view.findViewById(textID)).getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mTOKEN == null)
                    mTOKEN = new MediaSession(basicParam.getContext(), "MediaSessionHelper").getSessionToken();
                basicParam.setToken(mTOKEN);
                playIntent = new Intent("com.sing.client.click_action_pause");
                preSongIntent = new Intent("com.sing.client.click_action_pre");
                nextSongIntent = new Intent("com.sing.client.click_action");
                hasExtraAction = false;
                extraActionIcon = 0x7f020207;
                extraActionIntent = new Intent("com.sing.client.click_action_lyric");
                contentIntent = new Intent().putExtra("isFrom", "isFromPlay").setClass(basicParam.getContext(), XposedHelpers.findClass("com.sing.client.play.ui.PlayerActivity", classLoader));
                intentRequestID = 5;
                XposedHelpers.setObjectField(param.thisObject, "b", build());
            }
        });
    }

}

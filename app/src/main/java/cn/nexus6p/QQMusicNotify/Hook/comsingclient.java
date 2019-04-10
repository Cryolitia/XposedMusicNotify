package cn.nexus6p.QQMusicNotify.Hook;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.session.MediaSession;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import cn.nexus6p.QQMusicNotify.BasicViewNotification;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class comsingclient extends BasicViewNotification {

    private static MediaSession.Token mTOKEN;

    @Override
    public void init() {

        Class songClazz = XposedHelpers.findClass("com.sing.client.model.Song",classLoader);
        XposedHelpers.findAndHookMethod("com.kugou.common.player.manager.b", classLoader, "h",songClazz, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                oldNotification = (Notification) XposedHelpers.getObjectField(param.thisObject,"b");
                context = (Context) XposedHelpers.getObjectField(param.thisObject,"a");
                iconID = 0x7f0207f1;
                titleID = 0x7f100bf8;
                textID = 0x7f100bf9;
                bitmapID = 0x7f100bf7;
                View view = oldNotification.bigContentView.apply(context,null);
                try {
                    bitmap = ((BitmapDrawable)((ImageView) view.findViewById(bitmapID)).getDrawable()).getBitmap();
                    titleString = ((TextView) view.findViewById(titleID)).getText();
                    textString = ((TextView) view.findViewById(textID)).getText();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mTOKEN==null) mTOKEN = new MediaSession(context,"MediaSessionHelper").getSessionToken();
                token = mTOKEN;
                playIntent = new Intent("com.sing.client.click_action_pause");
                preSongIntent = new Intent("com.sing.client.click_action_pre");
                nextSongIntent = new Intent("com.sing.client.click_action");
                hasExtraAction = false;
                extraActionIcon = 0x7f020207;
                extraActionIntent = new Intent("com.sing.client.click_action_lyric");
                contentIntent = new Intent().putExtra("isFrom","isFromPlay").setClass(context,XposedHelpers.findClass("com.sing.client.play.ui.PlayerActivity",classLoader));
                intentRequestID = 5;
                XposedHelpers.setObjectField(param.thisObject,"b",build());
            }
        });
    }

}

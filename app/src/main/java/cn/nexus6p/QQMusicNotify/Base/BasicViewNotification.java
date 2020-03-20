package cn.nexus6p.QQMusicNotify.Base;

import android.app.Notification;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public abstract class BasicViewNotification extends BasicNotification {

    public Notification oldNotification;
    public int titleID;
    public int textID;
    public int bitmapID;
    public String className = "";
    public String methodName = "";

    protected BasicViewNotification() {
    }

    public final Notification viewBuild() {
        if (oldNotification == null) {
            Log.e("QQMusicNotify", "oldNotification should not be null!");
            return null;
        }
        RemoteViews remoteViews = (RemoteViews) XposedHelpers.getObjectField(oldNotification,"bigContentView");
        View view = remoteViews.apply(basicParam.getContext(), null);
        /*if (view==null) {
            Log.e("XposedMusicNotify", "RemoteView shoud not be bull");
            return null;
        }*/
        TextView titleTextView = view.findViewById(titleID);
        TextView textTextView = view.findViewById(textID);
        ImageView imageView = view.findViewById(bitmapID);
        basicParam.setTitleString(titleTextView.getText());
        basicParam.setTextString(textTextView.getText());
        basicParam.setBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelID = oldNotification.getChannelId();
        }
        return this.build();
    }

}

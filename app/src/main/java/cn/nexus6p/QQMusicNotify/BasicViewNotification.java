package cn.nexus6p.QQMusicNotify;

import android.app.Notification;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

abstract class BasicViewNotification extends BasicNotification {

    Notification oldNotification;
    int titleID;
    int textID;
    int bitmapID;
    BasicViewNotification(ClassLoader mClassLoader) {
        super(mClassLoader);
    }

    protected BasicViewNotification() {
    }

    Notification viewBuild () {
        if (oldNotification==null) {
            Log.e("QQMusicNotify","oldNotification should not be null!");
            return null;
        }
        RemoteViews remoteViews = oldNotification.bigContentView;
        View view = remoteViews.apply(context,null);
        TextView titleTextView = view.findViewById(titleID);
        TextView textTextView = view.findViewById(textID);
        ImageView imageView = view.findViewById(bitmapID);
        bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        titleString = titleTextView.getText();
        textString = textTextView.getText();
        return this.build();
    }

}

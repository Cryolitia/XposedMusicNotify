package cn.nexus6p.QQMusicNotify;

import android.app.Notification;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import com.oasisfeng.nevo.sdk.MutableStatusBarNotification;
import com.oasisfeng.nevo.sdk.NevoDecoratorService;

import soptqs.medianotification.utils.ImageUtils;
import soptqs.medianotification.utils.PaletteUtils;

public class nevoDecorators extends NevoDecoratorService {

    String appName;
    String title;
    String subtitle;
    Bitmap largeIcon;
    Bitmap smallIcon;

    @Override public boolean apply(final MutableStatusBarNotification evolving) {
        Notification notification = evolving.getNotification();
        Log.d("MusicNotification", notification.toString());
        if (isMediaNotification(notification)) {
            Bundle extras = NotificationCompat.getExtras(notification);
            Log.d("MusicNotification", extras.toString());
            title = extras.get(NotificationCompat.EXTRA_TITLE).toString();
            subtitle = extras.get(NotificationCompat.EXTRA_TEXT).toString();
            title = title == null || title.equals("") ? "未知音乐" : title;
            subtitle = subtitle == null || subtitle.equals("") ? "未知艺术家" : subtitle;
            largeIcon = notification.largeIcon;
            /*List<NotificationCompat.Action> actions = new ArrayList<>();
            List<Bitmap> actionIcons = new ArrayList<>();
            int actionCount = NotificationCompat.getActionCount(notification);
            for (int i = 0; i < actionCount; i++) {
                NotificationCompat.Action action = NotificationCompat.getAction(notification, i);
                actions.add(action);
            }*/

            notification.contentView = getContentView(true,evolving.getPackageName());
            //notification.bigContentView = getContentView(false);

            return true;
        }
        else return false;
    }


    private boolean isMediaNotification(Notification notification){

        if (notification.extras.containsKey(NotificationCompat.EXTRA_MEDIA_SESSION)){
            return true;
        } else if (!TextUtils.isEmpty(notification.extras.getString(Notification.EXTRA_TEMPLATE))) {
            return Notification.MediaStyle.class.getName().equals(notification.extras.getString(Notification.EXTRA_TEMPLATE));
        }
        return false;
    }


    private Bitmap getBitmap(Drawable vectorDrawable){
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    private RemoteViews getContentView(boolean isCollapsed, String packageName) {
        RemoteViews remoteViews = new RemoteViews(packageName, isCollapsed ? R.layout.layout_notification_collapsed : R.layout.layout_notification_expanded);
        remoteViews = remoteViewSetting(remoteViews);
        return remoteViews;
    }

    private RemoteViews remoteViewSetting(RemoteViews remoteViews) {
        //remoteViews.setTextViewText(R.id.appName, appName);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.subtitle, subtitle);

        remoteViews.setViewVisibility(R.id.largeIcon, View.VISIBLE);
        remoteViews.setImageViewBitmap(R.id.largeIcon, largeIcon);
        Palette palette = PaletteUtils.getPalette(largeIcon);
        Palette.Swatch swatch = PaletteUtils.getSwatch(palette);

        int color = PaletteUtils.getTextColor(palette, swatch);
        remoteViews.setInt(R.id.image, "setBackgroundColor", swatch.getRgb());
        remoteViews.setInt(R.id.foregroundImage, "setColorFilter", swatch.getRgb());
        //remoteViews.setInt(R.id.arrow, "setColorFilter", color);
        remoteViews.setImageViewBitmap(R.id.smallIcon, ImageUtils.setBitmapColor(smallIcon, color));
        remoteViews.setTextColor(R.id.appName, color);
        remoteViews.setTextColor(R.id.title, color);
        remoteViews.setTextColor(R.id.subtitle, color);

        /*TypedArray typedArray = appContext.obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
        int selectableItemBackground = typedArray.getResourceId(0, 0);
        typedArray.recycle();

        remoteViews.setInt(R.id.content, "setBackgroundResource", selectableItemBackground);

        for (int i = 0; i < 5; i++) {
            int id = -1;
            switch (i) {
                case 0:
                    id = R.id.first;
                    break;
                case 1:
                    id = R.id.second;
                    break;
                case 2:
                    id = R.id.third;
                    break;
                case 3:
                    id = R.id.fourth;
                    break;
                case 4:
                    id = R.id.fifth;
                    break;
            }

            NotificationCompat.Action action;
            if (i >= actions.size()) {
                remoteViews.setViewVisibility(id, View.GONE);
                continue;
            } else action = actions.get(i);

            remoteViews.setViewVisibility(id, View.VISIBLE);
            remoteViews.setImageViewBitmap(id, actionIcons.get(i));
            remoteViews.setInt(id, "setBackgroundResource", selectableItemBackground);
            remoteViews.setInt(id,"setColorFilter", color);
            remoteViews.setOnClickPendingIntent(id, action.getActionIntent());
        }*/
        return remoteViews;
    }

}

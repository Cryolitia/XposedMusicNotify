package soptqs.medianotification.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.request.target.Target;

import java.util.List;

import cn.nexus6p.QQMusicNotify.R;
import soptqs.medianotification.utils.BlurUtils;
import soptqs.medianotification.utils.ImageUtils;
import soptqs.medianotification.utils.PaletteUtils;
import soptqs.medianotification.utils.PreferenceUtils;

import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;
import static cn.nexus6p.QQMusicNotify.GeneralUtils.getMoudleContext;
import static cn.nexus6p.QQMusicNotify.PreferenceUtil.getXSharedPreference;

public class NotificationService {

    private NotificationManager notificationManager;
    private String packageName;
    private String appName;
    private Bitmap smallIcon;
    private String title;
    private String subtitle;
    private Bitmap largeIcon;
    private PendingIntent contentIntent;
    private List<NotificationCompat.Action> actions;
    private List<Bitmap> actionIcons;
    private boolean isPlaying;
    private Context context;

    public NotificationService setParam(String mPackageName, Bitmap mSmallIcon, String mTitle, String mSubtitle, Bitmap mLargeIcon, PendingIntent mContentIntent, List<NotificationCompat.Action> mActions, List<Bitmap> mActionIcons, boolean mIsPlaying, Context mContext) {
        packageName = mPackageName;
        smallIcon = mSmallIcon;
        title = mTitle;
        subtitle = mSubtitle;
        largeIcon = mLargeIcon;
        contentIntent = mContentIntent;
        actions = mActions;
        actionIcons = mActionIcons;
        isPlaying = mIsPlaying;
        context = mContext;
        return this;
    }

    public Notification updateNotification() {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "music")
                .setSmallIcon(R.drawable.ic_music)
                .setContentTitle(title)
                .setContentText(subtitle)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                //.setDeleteIntent(PendingIntent.getService(this, 0, deleteIntent, 0))
//                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle())
                .setOngoing(isPlaying || getXSharedPreference().getBoolean(PreferenceUtils.PREF_ALWAYS_DISMISSIBLE, false))
                .setVisibility(VISIBILITY_PUBLIC);

        if (contentIntent != null)
            builder.setContentIntent(contentIntent);
        else {
            if (packageName != null) {
                try {
                    Intent contentIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                    builder.setContentIntent(PendingIntent.getActivity(context, 0, contentIntent, 0));
                } catch (Exception ignored) {
                }
            }
        }
        try {
            appName = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA).loadLabel(context.getPackageManager()).toString();
        } catch (Exception ignored) {
        }
        if (appName == null)
            appName = context.getString(R.string.app_name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            builder.setPriority(NotificationManager.IMPORTANCE_MAX);
        else builder.setPriority(Notification.PRIORITY_MAX);

        for (NotificationCompat.Action action : actions) {
            builder.addAction(action);
        }

        if (smallIcon == null)
            smallIcon = ImageUtils.getVectorBitmap(getMoudleContext(context), R.drawable.ic_music);

        builder.setCustomContentView(getContentView(true));
        if (actions.size() > 0)
            builder.setCustomBigContentView(getContentView(false));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("music", "Music", NotificationManager.IMPORTANCE_HIGH));
            builder.setChannelId("music");
        }

        return builder.build();
    }

    private RemoteViews getContentView(boolean isCollapsed) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), isCollapsed ? R.layout.layout_notification_collapsed : R.layout.layout_notification_expanded);
            remoteViews = remoteViewSetting(remoteViews);
            return remoteViews;
    }

    private RemoteViews remoteViewSetting(RemoteViews remoteViews) {
        remoteViews.setTextViewText(R.id.appName, appName + " \u2022 " + (isPlaying ? getMoudleContext(context).getResources().getString(R.string.isplaying) : getMoudleContext(context).getResources().getString(R.string.ispause)));
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.subtitle, subtitle);

        remoteViews.setViewVisibility(R.id.largeIcon, View.VISIBLE);
        remoteViews.setImageViewBitmap(R.id.largeIcon, largeIcon);
        Palette palette = PaletteUtils.getPalette(getMoudleContext(context), largeIcon);
        Palette.Swatch swatch = PaletteUtils.getSwatch(getMoudleContext(context), palette);

        int color = PaletteUtils.getTextColor(getMoudleContext(context), palette, swatch);
        remoteViews.setInt(R.id.image, "setBackgroundColor", swatch.getRgb());
        remoteViews.setInt(R.id.foregroundImage, "setColorFilter", swatch.getRgb());
        remoteViews.setInt(R.id.arrow, "setColorFilter", color);
        remoteViews.setImageViewBitmap(R.id.smallIcon, ImageUtils.setBitmapColor(smallIcon, color));
        remoteViews.setTextColor(R.id.appName, color);
        remoteViews.setTextColor(R.id.title, color);
        remoteViews.setTextColor(R.id.subtitle, color);

        TypedArray typedArray = context.obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
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
            remoteViews.setOnClickPendingIntent(id, action.getActionIntent());
        }
        return remoteViews;
    }


    /*private int getActionIconRes(int i, int actionCount, String... names) {
        for (String name : names) {
            if (contains(name, "play"))
                return contains(name, "pause") ? (isPlaying ? R.drawable.ic_pause : R.drawable.ic_play) : R.drawable.ic_play;
            else if (contains(name, "pause"))
                return R.drawable.ic_pause;
            else if (contains(name, "prev"))
                return R.drawable.ic_skip_previous;
            else if (contains(name, "next"))
                return R.drawable.ic_skip_next;
            else if (contains(name, "stop"))
                return R.drawable.ic_stop;
            else if (contains(name, "down") || contains(name, "dislike") || contains(name, "unfavorite") || contains(name, "un-favorite"))
                return R.drawable.ic_thumb_down;
            else if (contains(name, "up") || contains(name, "like") || contains(name, "favorite"))
                return R.drawable.ic_thumb_up;
            else if (contains(name, "add"))
                return R.drawable.ic_add;
            else if (contains(name, "added") || contains(name, "check") || contains(name, "new"))
                return R.drawable.ic_check;
        }

        if (actionCount == 5) {
            if (i == 0)
                return R.drawable.ic_thumb_up;
            else if (i == 1)
                return R.drawable.ic_skip_previous;
            else if (i == 2)
                return isPlaying ? R.drawable.ic_pause : R.drawable.ic_play;
            else if (i == 3)
                return R.drawable.ic_skip_next;
            else if (i == 4)
                return R.drawable.ic_thumb_down;
        } else if (actionCount == 4) {
            if (i == 0)
                return R.drawable.ic_skip_previous;
            else if (i == 1)
                return R.drawable.ic_stop;
            else if (i == 2)
                return isPlaying ? R.drawable.ic_pause : R.drawable.ic_play;
            else if (i == 3)
                return R.drawable.ic_skip_next;
        } else if (actionCount == 3) {
            if (i == 0)
                return R.drawable.ic_skip_previous;
            else if (i == 1)
                return isPlaying ? R.drawable.ic_pause : R.drawable.ic_play;
            else if (i == 2)
                return R.drawable.ic_skip_next;
        }

        return R.drawable.ic_music;
    }*/

}

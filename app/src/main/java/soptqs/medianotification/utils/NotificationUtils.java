package soptqs.medianotification.utils;

import android.app.AndroidAppHelper;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import java.util.List;

import cn.nexus6p.QQMusicNotify.Base.BasicParam;
import cn.nexus6p.QQMusicNotify.BuildConfig;
import cn.nexus6p.QQMusicNotify.R;
import cn.nexus6p.QQMusicNotify.Utils.PreferenceUtil;
import de.robv.android.xposed.XSharedPreferences;

import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;
import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.getContext;
import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.getMoudleContext;

public class NotificationUtils {

    private NotificationManager notificationManager;
    private String appName;
    private Bitmap smallIcon;
    private int iconID;
    private String title;
    private String subtitle;
    private Bitmap largeIcon;
    private PendingIntent contentIntent;
    private List<NotificationCompat.Action> actions;
    private List<Bitmap> actionIcons;
    private boolean isPlaying;
    private Context moduleContext;
    private Context appContext;
    private PendingIntent deleteIntent;

    public NotificationUtils setParam(BasicParam basicParam, List<NotificationCompat.Action> mActions, List<Bitmap> mIcons) {
        title = basicParam.getTitleString().toString();
        subtitle = basicParam.getTextString().toString();
        largeIcon = basicParam.getBitmap();
        isPlaying = basicParam.getStatue();
        actions = mActions;
        actionIcons = mIcons;
        contentIntent = basicParam.getContentIntent();
        deleteIntent = basicParam.getDeleteIntent();
        iconID = basicParam.getIconID();
        appContext = getContext();
        moduleContext = getMoudleContext();
        /*if (largeIcon==null) {
            GradientDrawable drawable = (GradientDrawable) getMoudleContext().getDrawable(R.drawable.color_drawable);
            drawable.setColor(Color.parseColor(new XSharedPreferences(BuildConfig.APPLICATION_ID).getString("customColor","#000000")));
            largeIcon = ImageUtils.drawableToBitmap(drawable);
        }*/
        try {
            smallIcon = ((BitmapDrawable) appContext.getResources().getDrawable(basicParam.getIconID(),null)).getBitmap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public Notification updateNotification() {
        notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext, "music")
                .setSmallIcon(iconID)
                .setContentTitle(title)
                .setContentText(subtitle)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                //.setDeleteIntent(PendingIntent.getService(this, 0, deleteIntent, 0))
//                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle())
                .setOngoing(isPlaying || PreferenceUtil.getPreference().getBoolean(PreferenceUtils.PREF_ALWAYS_DISMISSIBLE, false))
                .setVisibility(VISIBILITY_PUBLIC);

        if (deleteIntent!=null) builder.setDeleteIntent(deleteIntent);
        if (contentIntent != null)
            builder.setContentIntent(contentIntent);
        /*else {
            if (packageName != null) {
                try {
                    Intent contentIntent = appContext.getPackageManager().getLaunchIntentForPackage(packageName);
                    builder.setContentIntent(PendingIntent.getActivity(appContext, 0, contentIntent, 0));
                } catch (Exception ignored) {
                }
            }
        }*/
        try {
            appName = appContext.getPackageManager().getApplicationLabel(AndroidAppHelper.currentApplicationInfo()).toString();
        } catch (Exception ignored) {
        }
        if (appName == null)
            appName = moduleContext.getString(R.string.app_name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            builder.setPriority(NotificationManager.IMPORTANCE_DEFAULT);
        else builder.setPriority(Notification.PRIORITY_MAX);

        for (NotificationCompat.Action action : actions) {
            builder.addAction(action);
        }

        if (smallIcon == null)
            smallIcon = ImageUtils.getVectorBitmap(moduleContext, R.drawable.ic_music);

        if (actions.size() > 0) builder.setCustomBigContentView(getContentView(false));
        builder.setCustomContentView(getContentView(true));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("music1", "音乐通知", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableVibration(false);
            notificationChannel.setSound(null,null);
            notificationManager.deleteNotificationChannel("music");
            notificationManager.createNotificationChannel(notificationChannel);
            builder.setChannelId("music1");
        }

        return builder.build();
    }

    private RemoteViews getContentView(boolean isCollapsed) {
            RemoteViews remoteViews = new RemoteViews(moduleContext.getPackageName(), isCollapsed ? R.layout.layout_notification_collapsed : R.layout.layout_notification_expanded);
            remoteViews = remoteViewSetting(remoteViews, isCollapsed);
            return remoteViews;
    }

    private RemoteViews remoteViewSetting(RemoteViews remoteViews,boolean isCollapsed) {
        remoteViews.setTextViewText(R.id.appName, appName);
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

        TypedArray typedArray = appContext.obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
        int selectableItemBackground = typedArray.getResourceId(0, 0);
        typedArray.recycle();

        remoteViews.setInt(R.id.content, "setBackgroundResource", selectableItemBackground);

        if (!isCollapsed) for (int i = 0; i < 5; i++) {
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
        }
        return remoteViews;
    }

}

package cn.nexus6p.QQMusicNotify.Base;

import android.app.AndroidAppHelper;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.session.MediaSession;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.List;

import cn.nexus6p.QQMusicNotify.BuildConfig;
import cn.nexus6p.QQMusicNotify.GeneralUtils;
import de.robv.android.xposed.XSharedPreferences;
import me.qiwu.MusicNotification.ColorUtil;
import cn.nexus6p.QQMusicNotify.R;

import static android.app.Notification.FLAG_FOREGROUND_SERVICE;
import static android.app.Notification.FLAG_NO_CLEAR;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
import static cn.nexus6p.QQMusicNotify.GeneralUtils.getMoudleContext;

public abstract class BasicNotification extends BasicInit {

    public CharSequence titleString;
    public CharSequence textString;
    public int iconID;
    public boolean statue = true;
    public MediaSession.Token token;
    public Intent preSongIntent;
    public Intent nextSongIntent;
    public Intent playIntent;
    public Bitmap bitmap;
    public Intent contentIntent;
    public int intentRequestID = 0;
    public Boolean hasExtraAction = false;
    public Intent extraActionIntent;
    public int extraActionIcon;
    public String channelID;
    private List<Notification.Action> actions = new ArrayList<>();

    public abstract void init();

    public final Notification build() {
        Notification.Action previousAction = new Notification.Action.Builder(
                //Icon.createWithBitmap(BitmapFactory.decodeResource(getMoudleContext().getResources(), R.drawable.note_btn_pre))
                android.R.drawable.ic_media_previous
                , "后退", PendingIntent.getBroadcast(context, 0, preSongIntent, PendingIntent.FLAG_UPDATE_CURRENT)).build();
        Notification.Action playAction = new Notification.Action.Builder(
                statue ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play, statue ? "暂停" : "播放"
                , PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)).build();
        Notification.Action nextAction = new Notification.Action.Builder(
                android.R.drawable.ic_media_next
                , "前进", PendingIntent.getBroadcast(context, 0, nextSongIntent, PendingIntent.FLAG_UPDATE_CURRENT)).build();
        actions.add(previousAction);
        actions.add(playAction);
        actions.add(nextAction);

        if (hasExtraAction) {
            Notification.Action extraAction = new Notification.Action.Builder(extraActionIcon, "桌面歌词", PendingIntent.getBroadcast(context, 0, extraActionIntent, PendingIntent.FLAG_UPDATE_CURRENT)).build();
            actions.add(extraAction);
        }
        if (new XSharedPreferences("cn.nexus6p.QQMusicNotify").getBoolean("styleModify",false)) {
            RemoteViews remoteViews = getContentView(titleString.toString(),textString.toString());
            return GeneralUtils.buildMusicNotificationWithoutAction(context,iconID,titleString,textString,statue,remoteViews,PendingIntent.getActivity(context, intentRequestID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT),channelID,null);
        } else {
            Notification.Builder builder = new Notification.Builder(context)
                    .setContentTitle(titleString)
                    .setContentText(textString)
                    .setSmallIcon(iconID)
                    .setOngoing(statue)
                    .setCategory(Notification.CATEGORY_STATUS)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .addAction(previousAction)
                    .addAction(playAction)
                    .addAction(nextAction)
                    .setStyle(new Notification.MediaStyle()
                            .setMediaSession(token)
                            .setShowActionsInCompactView(0, 1, 2)
                    )
                    .setLargeIcon(bitmap);
            if (hasExtraAction)
                builder.addAction(actions.get(3));
            if (bitmap == null) builder.setColor(Color.BLACK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O&&channelID!=null) {
                builder.setChannelId(channelID);
            }
            Notification notification = builder.build();
            if (statue) notification.flags = FLAG_FOREGROUND_SERVICE | FLAG_NO_CLEAR;
            contentIntent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            notification.contentIntent = PendingIntent.getActivity(context, intentRequestID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            return notification;
        }
    }

    private RemoteViews getContentView(String title,String subtitle){
        int backgroundColor = Color.BLACK;
        int textColor = Color.WHITE;
        if (bitmap!=null){
            int[] colors = ColorUtil.getColor(bitmap);
            backgroundColor = colors[0];
            textColor = colors[1];
        }
        RemoteViews remoteViews = new RemoteViews(getMoudleContext(context).getPackageName(),R.layout.notifition_layout);

        remoteViews.setTextViewText(R.id.appName,context.getPackageManager().getApplicationLabel(AndroidAppHelper.currentApplicationInfo()));
        remoteViews.setTextViewText(R.id.title,title);
        remoteViews.setTextViewText(R.id.subtitle,subtitle);
        //remoteViews.setImageViewIcon(R.id.smallIcon,notification.getSmallIcon());


        remoteViews.setImageViewBitmap(
                R.id.smallIcon,iconID != -1
                        ? getBitmap(context.getDrawable(iconID))
                        //        :null
                        : getBitmap(getMoudleContext(context).getDrawable(R.drawable.ic_music))
        );

        remoteViews.setTextColor(R.id.appName,textColor);
        remoteViews.setTextColor(R.id.title,textColor);
        remoteViews.setTextColor(R.id.subtitle,textColor);
        remoteViews.setImageViewBitmap(R.id.largeIcon,bitmap);
        remoteViews.setInt(R.id.smallIcon,"setColorFilter",textColor);
        remoteViews.setInt(R.id.foregroundImage,"setColorFilter", backgroundColor);
        remoteViews.setInt(R.id.background, "setBackgroundColor", backgroundColor);
        TypedArray typedArray = context.obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
        int selectableItemBackground = typedArray.getResourceId(0, 0);
        typedArray.recycle();
        int actionIconID=-1;
        for (int i = 0;i<=2;i++) {
            switch (i) {
                case 0:
                    actionIconID = android.R.drawable.ic_media_previous;
                    break;
                case 1:
                    actionIconID = (!statue) ? android.R.drawable.ic_media_play : android.R.drawable.ic_media_pause;
                    break;
                case 2:
                    actionIconID = android.R.drawable.ic_media_next;
                    break;
            }
            int id = getMoudleContext(context).getResources().getIdentifier("ic_" + i, "id", BuildConfig.APPLICATION_ID);
            Notification.Action action = actions.get(i);
            remoteViews.setViewVisibility(id, View.VISIBLE);
            remoteViews.setImageViewBitmap(id, BitmapFactory.decodeResource(context.getResources(), actionIconID));
            remoteViews.setOnClickPendingIntent(id, action.actionIntent);
            remoteViews.setInt(id, "setColorFilter", textColor);
            remoteViews.setInt(id, "setBackgroundResource", selectableItemBackground);
            // XposedBridge.log("资源："+action.getIcon());
        }
        if (hasExtraAction) {
            int id = getMoudleContext(context).getResources().getIdentifier("ic_" + String.valueOf(3), "id", BuildConfig.APPLICATION_ID);
            Notification.Action action = actions.get(3);
            remoteViews.setViewVisibility(id, View.VISIBLE);
            remoteViews.setImageViewBitmap(id, BitmapFactory.decodeResource(context.getResources(), extraActionIcon));
            remoteViews.setOnClickPendingIntent(id, action.actionIntent);
            remoteViews.setInt(id, "setColorFilter", textColor);
            remoteViews.setInt(id, "setBackgroundResource", selectableItemBackground);
        }
        return remoteViews;
    }

    private Bitmap getBitmap(Drawable vectorDrawable){
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

}
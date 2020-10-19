package cn.nexus6p.QQMusicNotify.Base;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

import cn.nexus6p.QQMusicNotify.R;
import cn.nexus6p.QQMusicNotify.Utils.PreferenceUtil;
import soptqs.medianotification.utils.NotificationUtils;

import static android.app.Notification.FLAG_FOREGROUND_SERVICE;
import static android.app.Notification.FLAG_NO_CLEAR;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.getModuleContext;

public abstract class BasicNotification extends BasicInit {

    public Intent preSongIntent;
    public Intent nextSongIntent;
    public Intent playIntent;
    public Intent contentIntent;
    public int intentRequestID = 0;
    public Boolean hasExtraAction = false;
    public Intent extraActionIntent;
    public int extraActionIcon;
    public String channelID;
    private List<NotificationCompat.Action> actions = new ArrayList<>();
    private List<Bitmap> actionIcons = new ArrayList<>();

    @Override
    public abstract void init();

    @Override
    public final void initBefore() {
        init();
    }

    public final Notification build() {
        NotificationCompat.Action previousAction = new NotificationCompat.Action.Builder(
                //Icon.createWithBitmap(BitmapFactory.decodeResource(getMoudleContext().getResources(), R.drawable.ic_skip_previous))
                android.R.drawable.ic_media_previous
                , "后退", PendingIntent.getBroadcast(basicParam.getContext(), 0, preSongIntent, PendingIntent.FLAG_UPDATE_CURRENT)).build();
        NotificationCompat.Action playAction = new NotificationCompat.Action.Builder(
                basicParam.getStatue() ?
                        android.R.drawable.ic_media_pause :
                        //Icon.createWithBitmap(BitmapFactory.decodeResource(getMoudleContext().getResources(), R.drawable.ic_pause)) :
                        android.R.drawable.ic_media_play
                //Icon.createWithBitmap(BitmapFactory.decodeResource(getMoudleContext().getResources(), R.drawable.ic_play))
                , basicParam.getStatue() ? "暂停" : "播放"
                , PendingIntent.getBroadcast(basicParam.getContext(), 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)).build();
        NotificationCompat.Action nextAction = new NotificationCompat.Action.Builder(
                android.R.drawable.ic_media_next
                //Icon.createWithBitmap(BitmapFactory.decodeResource(getMoudleContext().getResources(), R.drawable.ic_skip_next))
                , "前进", PendingIntent.getBroadcast(basicParam.getContext(), 0, nextSongIntent, PendingIntent.FLAG_UPDATE_CURRENT)).build();
        actions.clear();
        actions.add(previousAction);
        actions.add(playAction);
        actions.add(nextAction);

        if (hasExtraAction) {
            NotificationCompat.Action extraAction = new NotificationCompat.Action.Builder(extraActionIcon, "桌面歌词", PendingIntent.getBroadcast(basicParam.getContext(), 0, extraActionIntent, PendingIntent.FLAG_UPDATE_CURRENT)).build();
            actions.add(extraAction);
        }
        if (PreferenceUtil.getPreference(basicParam.getContext()).getBoolean("styleModify", false)) {
            basicParam.setContentIntent(PendingIntent.getActivity(basicParam.getContext(), intentRequestID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT));
            actionIcons.clear();
            actionIcons.add(BitmapFactory.decodeResource(getModuleContext().getResources(), R.drawable.ic_skip_previous));
            actionIcons.add(basicParam.getStatue() ?
                    //android.R.drawable.ic_media_pause :
                    BitmapFactory.decodeResource(getModuleContext().getResources(), R.drawable.ic_pause) :
                    //android.R.drawable.ic_media_play
                    BitmapFactory.decodeResource(getModuleContext().getResources(), R.drawable.ic_play));
            actionIcons.add(BitmapFactory.decodeResource(getModuleContext().getResources(), R.drawable.ic_skip_next));
            if (hasExtraAction)
                actionIcons.add(BitmapFactory.decodeResource(basicParam.getContext().getResources(), extraActionIcon));
            return new NotificationUtils().setParam(basicParam, actions, actionIcons).updateNotification();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(basicParam.getContext(), channelID)
                    .setContentTitle(basicParam.getTitleString())
                    .setContentText(basicParam.getTextString())
                    .setSmallIcon(basicParam.getIconID())
                    .setOngoing(basicParam.getStatue())
                    .setCategory(Notification.CATEGORY_STATUS)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .addAction(previousAction)
                    .addAction(playAction)
                    .addAction(nextAction)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(MediaSessionCompat.Token.fromToken(basicParam.getToken()))
                            .setShowActionsInCompactView(0, 1, 2)
                    )
                    .setLargeIcon(basicParam.getBitmap());
            if (hasExtraAction)
                builder.addAction(actions.get(3));
            if (basicParam.getBitmap() == null) builder.setColor(Color.BLACK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && channelID != null) {
                builder.setChannelId(channelID);
            }
            Notification notification = builder.build();
            if (basicParam.getStatue())
                notification.flags = FLAG_FOREGROUND_SERVICE | FLAG_NO_CLEAR;
            contentIntent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            notification.contentIntent = PendingIntent.getActivity(basicParam.getContext(), intentRequestID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            return notification;
        }
    }

}
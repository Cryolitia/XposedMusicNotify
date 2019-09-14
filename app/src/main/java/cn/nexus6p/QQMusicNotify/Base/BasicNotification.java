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
import android.graphics.drawable.Icon;
import android.media.session.MediaSession;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

import cn.nexus6p.QQMusicNotify.Base.BasicParam;
import cn.nexus6p.QQMusicNotify.BuildConfig;
import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils;
import de.robv.android.xposed.XSharedPreferences;
import me.qiwu.MusicNotification.ColorUtil;
import cn.nexus6p.QQMusicNotify.R;
import soptqs.medianotification.utils.NotificationUtils;

import static android.app.Notification.FLAG_FOREGROUND_SERVICE;
import static android.app.Notification.FLAG_NO_CLEAR;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
import static cn.nexus6p.QQMusicNotify.Utils.GeneralUtils.getMoudleContext;

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

    public abstract void init();

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
        if (new XSharedPreferences("cn.nexus6p.QQMusicNotify").getBoolean("styleModify",false)) {
            /*if (basicParam.getBitmap()==null) {
                RemoteViews remoteViews = getContentView(basicParam.getTitleString().toString(),basicParam.getTextString().toString());
                return GeneralUtils.buildMusicNotificationWithoutAction(basicParam,remoteViews,PendingIntent.getActivity(basicParam.getContext(), intentRequestID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT),channelID,null);
            }*/
            basicParam.setContentIntent(PendingIntent.getActivity(basicParam.getContext(), intentRequestID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT));
            actionIcons.add(BitmapFactory.decodeResource(getMoudleContext().getResources(), R.drawable.ic_skip_previous));
            actionIcons.add(basicParam.getStatue() ?
                    //android.R.drawable.ic_media_pause :
                    BitmapFactory.decodeResource(getMoudleContext().getResources(), R.drawable.ic_pause) :
                    //android.R.drawable.ic_media_play
                    BitmapFactory.decodeResource(getMoudleContext().getResources(), R.drawable.ic_play));
            actionIcons.add(BitmapFactory.decodeResource(getMoudleContext().getResources(), R.drawable.ic_skip_next));
            return new NotificationUtils().setParam(basicParam,actions,actionIcons).updateNotification();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(basicParam.getContext(),channelID)
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O&&channelID!=null) {
                builder.setChannelId(channelID);
            }
            Notification notification = builder.build();
            if (basicParam.getStatue()) notification.flags = FLAG_FOREGROUND_SERVICE | FLAG_NO_CLEAR;
            contentIntent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            notification.contentIntent = PendingIntent.getActivity(basicParam.getContext(), intentRequestID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            return notification;
        }
    }

    private RemoteViews getContentView(String title,String subtitle){
        int backgroundColor = Color.parseColor(new XSharedPreferences(BuildConfig.APPLICATION_ID).getString("customColor","#000000"));
        int textColor = Color.WHITE;
        if (basicParam.getBitmap()!=null){
            int[] colors = ColorUtil.getColor(basicParam.getBitmap());
            backgroundColor = colors[0];
            textColor = colors[1];
        }
        RemoteViews remoteViews = new RemoteViews(getMoudleContext(basicParam.getContext()).getPackageName(),R.layout.notifition_layout);

        remoteViews.setTextViewText(R.id.appName,basicParam.getContext().getPackageManager().getApplicationLabel(AndroidAppHelper.currentApplicationInfo()));
        remoteViews.setTextViewText(R.id.title,title);
        remoteViews.setTextViewText(R.id.subtitle,subtitle);
        //remoteViews.setImageViewIcon(R.id.smallIcon,notification.getSmallIcon());


        remoteViews.setImageViewBitmap(
                R.id.smallIcon,basicParam.getIconID() != -1
                        ? getBitmap(basicParam.getContext().getDrawable(basicParam.getIconID()))
                        //        :null
                        : getBitmap(getMoudleContext(basicParam.getContext()).getDrawable(R.drawable.ic_music))
        );

        remoteViews.setTextColor(R.id.appName,textColor);
        remoteViews.setTextColor(R.id.title,textColor);
        remoteViews.setTextColor(R.id.subtitle,textColor);
        remoteViews.setImageViewBitmap(R.id.largeIcon,basicParam.getBitmap());
        remoteViews.setInt(R.id.smallIcon,"setColorFilter",textColor);
        remoteViews.setInt(R.id.foregroundImage,"setColorFilter", backgroundColor);
        remoteViews.setInt(R.id.background, "setBackgroundColor", backgroundColor);
        TypedArray typedArray = basicParam.getContext().obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
        int selectableItemBackground = typedArray.getResourceId(0, 0);
        typedArray.recycle();
        int actionIconID=-1;
        for (int i = 0;i<=2;i++) {
            switch (i) {
                case 0:
                    actionIconID = //android.R.drawable.ic_media_previous;
                            R.drawable.ic_skip_previous;
                    break;
                case 1:
                    actionIconID = (!basicParam.getStatue()) ? //android.R.drawable.ic_media_play
                            R.drawable.ic_play
                            : //android.R.drawable.ic_media_pause;
                            R.drawable.ic_pause;
                    break;
                case 2:
                    actionIconID = //android.R.drawable.ic_media_next;
                            R.drawable.ic_skip_next;
                    break;
            }
            int id = getMoudleContext(basicParam.getContext()).getResources().getIdentifier("ic_" + i, "id", BuildConfig.APPLICATION_ID);
            NotificationCompat.Action action = actions.get(i);
            remoteViews.setViewVisibility(id, View.VISIBLE);
            remoteViews.setImageViewBitmap(id, BitmapFactory.decodeResource(getMoudleContext(basicParam.getContext()).getResources(), actionIconID));
            remoteViews.setOnClickPendingIntent(id, action.actionIntent);
            remoteViews.setInt(id, "setColorFilter", textColor);
            remoteViews.setInt(id, "setBackgroundResource", selectableItemBackground);
            // XposedBridge.log("资源："+action.getIcon());
        }
        if (hasExtraAction) {
            int id = getMoudleContext(basicParam.getContext()).getResources().getIdentifier("ic_" + 3, "id", BuildConfig.APPLICATION_ID);
            NotificationCompat.Action action = actions.get(3);
            remoteViews.setViewVisibility(id, View.VISIBLE);
            remoteViews.setImageViewBitmap(id, BitmapFactory.decodeResource(basicParam.getContext().getResources(), extraActionIcon));
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
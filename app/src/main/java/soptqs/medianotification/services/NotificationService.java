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
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.jayway.jsonpath.JsonPath;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import cn.nexus6p.QQMusicNotify.PreferenceUtil;
import cn.nexus6p.QQMusicNotify.R;
import soptqs.medianotification.utils.BlurUtils;
import soptqs.medianotification.utils.ImageUtils;
import soptqs.medianotification.utils.PaletteUtils;
import soptqs.medianotification.utils.PreferenceUtils;

import static android.content.ContentValues.TAG;
import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;
import static cn.nexus6p.QQMusicNotify.GeneralUtils.getMoudleContext;

public class NotificationService {

    private Target imageTarget;
    private NotificationManager notificationManager;
    private String packageName;
    private String appName;
    private Bitmap smallIcon;
    private String title;
    private String subtitle;
    private Bitmap largeIcon;
    private Bitmap defautIcon;
    private PendingIntent contentIntent;
    private List<NotificationCompat.Action> actions;
    private List<Bitmap> actionIcons;
    private boolean isPlaying;
    private Context context;
    private PreferenceUtil prefs;

    public void updateNotification() {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "music")
                .setSmallIcon(R.drawable.ic_music)
                .setContentTitle(title)
                .setContentText(subtitle)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                //.setDeleteIntent(PendingIntent.getService(this, 0, deleteIntent, 0))
//                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle())
                .setOngoing(isPlaying || prefs.getBoolean(PreferenceUtils.PREF_ALWAYS_DISMISSIBLE, false))
                .setVisibility(VISIBILITY_PUBLIC);

        if (contentIntent != null)
            builder.setContentIntent(contentIntent);
        else {
            packageName = prefs.getString(PreferenceUtils.PREF_DEFAULT_MUSIC_PLAYER, null);
            if (packageName != null) {
                try {
                    Intent contentIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                    builder.setContentIntent(PendingIntent.getActivity(context, 0, contentIntent, 0));
                } catch (Exception ignored) {
                }

                try {
                    appName = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA).loadLabel(context.getPackageManager()).toString();
                } catch (Exception ignored) {
                }
            }
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

        //notificationManager.notify(948, builder.build());
    }

    private RemoteViews getContentView(boolean isCollapsed) {
        if (!prefs.getBoolean(PreferenceUtils.PREF_NOTIFICATION_STYLE2, false)) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), isCollapsed ? R.layout.layout_notification_collapsed : R.layout.layout_notification_expanded);
            remoteViews = remoteViewSettign(remoteViews, PreferenceUtils.PREF_NOTIFICATION_STYLE1);
            return remoteViews;
        } else {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), isCollapsed ? R.layout.layout_notification_collapsed_stlye2 : R.layout.layout_notification_expanded_stlye2);
            remoteViews = remoteViewSettign(remoteViews, PreferenceUtils.PREF_NOTIFICATION_STYLE2);
            return remoteViews;
        }

    }

    private RemoteViews remoteViewSettign(RemoteViews remoteViews, String style) {
        remoteViews.setTextViewText(R.id.appName, appName + " \u2022 " + (isPlaying ? getMoudleContext(context).getResources().getString(R.string.isplaying) : getMoudleContext(context).getResources().getString(R.string.ispause)));
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.subtitle, subtitle);

        remoteViews.setViewVisibility(R.id.largeIcon, prefs.getBoolean(PreferenceUtils.PREF_SHOW_ALBUM_ART, true) ? View.VISIBLE : View.GONE);
        remoteViews.setImageViewBitmap(R.id.largeIcon, largeIcon);
        Palette palette = PaletteUtils.getPalette(getMoudleContext(context), largeIcon);
        Palette.Swatch swatch = PaletteUtils.getSwatch(getMoudleContext(context), palette);

        int color = PaletteUtils.getTextColor(getMoudleContext(context), palette, swatch);
        remoteViews.setInt(R.id.image, "setBackgroundColor", swatch.getRgb());
        if (style == PreferenceUtils.PREF_NOTIFICATION_STYLE1)
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

        boolean useNotificationIcons = !prefs.getBoolean(PreferenceUtils.PREF_FORCE_MD_ICONS, false) && actionIcons.size() == actions.size();

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
            remoteViews.setImageViewBitmap(id, ImageUtils.setBitmapColor(useNotificationIcons ? actionIcons.get(i) : ImageUtils.getVectorBitmap(getMoudleContext(context), action.getIcon()), color));
            remoteViews.setInt(id, "setBackgroundResource", selectableItemBackground);
            remoteViews.setOnClickPendingIntent(id, action.getActionIntent());
        }
        return remoteViews;
    }


    private int getActionIconRes(int i, int actionCount, String... names) {
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
    }

    private boolean contains(String container, String containee) {
        return container != null && containee != null && container.toLowerCase().contains(containee.toLowerCase());
    }

    private void getAlbumArt(String albumName, String artistName) {
        if (imageTarget != null)
            Glide.with(context).clear(imageTarget);

        String baseUrl;

        try {
            baseUrl = "http://ws.audioscrobbler.com/2.0/?method=album.getInfo"
                    //+ "&api_key=" + getString(R.string.last_fm_api_key)
                    + "&album=" + URLEncoder.encode(albumName, "UTF-8")
                    + "&artist=" + URLEncoder.encode(artistName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return;
        }

        new LastFmImageThread(this, baseUrl, context).start();

    }

    private void getAlbumArtByTencent(String albumName, String artistName) {
        if (imageTarget != null)
            Glide.with(context).clear(imageTarget);

        String baseUrl;

        try {
            baseUrl = "http://s.music.qq.com/fcgi-bin/music_search_new_platform?t=0&n=1&aggr=0&cr=0&loginUin=0&format=json&inCharset=GB2312&outCharset=utf-8&notice=0&platform=jqminiframe.json&needNewCode=0&p=1&catZhida=0&remoteplace=sizer.newclient.next_song&w="
                    + URLEncoder.encode(albumName, "UTF-8")
                    + "|"
                    + URLEncoder.encode(artistName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return;
        }
        Log.e("baseurl", "getAlbumArtByTencent: " + baseUrl);

        new TencentMusicImageThread(this, baseUrl, context).start();
    }

    public void largeIconProcess(Bitmap bkp) {

        if (prefs.getBoolean(PreferenceUtils.PREF_ENABLE_RENDERSCRIPT, false)) {
            if (!prefs.getBoolean(PreferenceUtils.PREF_NOTIFICATION_STYLE2, false)) {
                largeIcon = ImageUtils.centerSquareScaleBitmap(bkp);
            } else if (prefs.getBoolean(PreferenceUtils.PREF_ENABLE_BLUR, false)) {
                largeIcon = BlurUtils.fastblur(defautIcon, 0.4f, 15);
            } else largeIcon = ImageUtils.centerSquareScaleBitmap(bkp);
        } else {
            if (!prefs.getBoolean(PreferenceUtils.PREF_NOTIFICATION_STYLE2, false)) {
                largeIcon = ImageUtils.centerSquareScaleBitmap(bkp);
            } else if (prefs.getBoolean(PreferenceUtils.PREF_ENABLE_BLUR, false)) {
                largeIcon = BlurUtils.blur(context, ImageUtils.centerSquareScaleBitmap(bkp), 15);
            } else largeIcon = ImageUtils.centerSquareScaleBitmap(bkp);
        }
    }

    private static class LastFmImageThread extends Thread {

        private WeakReference<Context> mContext;
        private WeakReference<NotificationService> serviceReference;
        private String url;

        public LastFmImageThread(NotificationService service, String url, Context context) {
            serviceReference = new WeakReference<>(service);
            mContext = new WeakReference<Context>(context);
            this.url = url;
        }

        @Override
        public void run() {
            String image = null;

            try {
                HttpURLConnection request = (HttpURLConnection) new URL(url).openConnection();
                request.connect();

                BufferedReader r = new BufferedReader(new InputStreamReader((InputStream) request.getContent()));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line).append('\n');
                }

                String source = total.toString();
                if (source.contains("<lfm status=\"failed\">")) {
                    NotificationService service = serviceReference.get();
                    if (service != null)
                        service.largeIcon = null;
                } else {
                    int startIndex = source.indexOf("<image size=\"large\">") + 20;
                    image = source.substring(startIndex, source.indexOf("<", startIndex));
                }
            } catch (Exception ignored) {
            }

            final String imageUrl = image;


            new Handler(Looper.getMainLooper()).post(() -> {
                NotificationService service = serviceReference.get();
                if (service != null) {
                    if (imageUrl != null) {
                        service.imageTarget = Glide.with(mContext.get()).asBitmap().load(imageUrl).into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                resource = ImageUtils.centerSquareScaleBitmap(resource);
                                NotificationService service = serviceReference.get();
                                if (service != null) {
                                    try {
                                        service.largeIconProcess(resource);
                                        service.updateNotification();
                                    } catch (Exception e) {
                                        Log.e(TAG, "onResourceReady: trying to use a recycled bitmap");
                                    }
                                }
                            }
                        });
                    } else service.updateNotification();
                }
            });
        }
    }

    private static class TencentMusicImageThread extends Thread {

        private WeakReference<NotificationService> serviceReference;
        private WeakReference<Context> mContext;
        private String url;

        public TencentMusicImageThread(NotificationService service, String url, Context context) {
            serviceReference = new WeakReference<>(service);
            mContext = new WeakReference<>(context);
            this.url = url;
        }

        @Override
        public void run() {
            String image = null;


            try {
                HttpURLConnection request = (HttpURLConnection) new URL(url).openConnection();
                request.connect();

                BufferedReader r = new BufferedReader(new InputStreamReader((InputStream) request.getContent()));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line).append('\n');
                }

                String source = total.toString();
                List<String> f = JsonPath.parse(source).read("$.data.song.list[*].f");
                String id = f.get(0);
                Log.e("iostream", "id: " + id);
                String[] desperate = id.split("\\|", 6);
                String imageid = desperate[4];

                image = "http://imgcache.qq.com/music/photo/album/"
                        + (Integer.parseInt(imageid) % 100)
                        + "/albumpic_"
                        + imageid
                        + "_0.jpg";
                Log.e("image", "run: " + image);

            } catch (Exception ignored) {
                Log.e("Tencent Music exception", "exception: " + ignored);
            }

            final String imageUrl = image;

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    NotificationService service = serviceReference.get();
                    if (service != null) {
                        if (imageUrl != null) {
                            service.imageTarget = Glide.with(mContext.get()).asBitmap().load(imageUrl).into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                    resource = ImageUtils.centerSquareScaleBitmap(resource);
                                    NotificationService service = serviceReference.get();
                                    if (service != null) {
                                        try {
                                            service.largeIconProcess(resource);
                                            service.updateNotification();
                                        } catch (Exception e) {
                                            Log.e(TAG, "onResourceReady: trying to use a recycled bitmap");
                                        }
                                    }
                                }
                            });
                        } else service.updateNotification();
                    }
                }
            });
        }
    }

}

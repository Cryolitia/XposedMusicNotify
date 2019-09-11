package cn.nexus6p.QQMusicNotify.Base;

import android.app.Notification;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import org.json.JSONObject;

import cn.nexus6p.QQMusicNotify.Utils.GeneralUtils;

public abstract class BasicViewNotification extends BasicNotification {

    public Notification oldNotification;
    public int titleID;
    public int textID;
    public int bitmapID;
    public String className = "";
    public String methodName = "";
    protected BasicViewNotification() { }

    public final void initWithJSON (String packageName) {
        try {
            JSONObject jsonObject = new JSONObject(GeneralUtils.getAssetsString(packageName));
            className = jsonObject.getString("class");
            methodName = jsonObject.getString("method");
            titleID = Integer.parseInt(jsonObject.getString("titleID"),16);
            textID = Integer.parseInt(jsonObject.getString("textID"),16);
            bitmapID = Integer.parseInt(jsonObject.getString("bitmapID"),16);
            basicParam.setIconID(Integer.parseInt(jsonObject.getString("iconID"),16));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final Notification viewBuild () {
        if (oldNotification==null) {
            Log.e("QQMusicNotify","oldNotification should not be null!");
            return null;
        }
        RemoteViews remoteViews = oldNotification.bigContentView;
        View view = remoteViews.apply(basicParam.getContext(),null);
        TextView titleTextView = view.findViewById(titleID);
        TextView textTextView = view.findViewById(textID);
        ImageView imageView = view.findViewById(bitmapID);
        basicParam.setBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap());
        basicParam.setTitleString(titleTextView.getText());
        basicParam.setTextString(textTextView.getText());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelID = oldNotification.getChannelId();
        }
        return this.build();
    }

}

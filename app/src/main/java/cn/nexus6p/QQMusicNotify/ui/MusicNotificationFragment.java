package cn.nexus6p.QQMusicNotify.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;

import cn.nexus6p.QQMusicNotify.GeneralUtils;
import cn.nexus6p.QQMusicNotify.R;

import static android.content.Context.MODE_WORLD_READABLE;

public class MusicNotificationFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
        addPreferencesFromResource(R.xml.music_notification);

        //GeneralUtils.bindPreference(this,"styleModify","always_show");

        GeneralUtils.jumpToLink(this,"magiskMoudle","feed/11103560",true);
        GeneralUtils.jumpToLink(this,"magiskMoudle2","feed/11103560",true);
        GeneralUtils.jumpToLink(this,"github2","https://github.com/Qiwu2542284182/MusicNotification",false);

        findPreference("alipay").setOnPreferenceClickListener(preference1 -> {
            Intent localIntent = new Intent();
            localIntent.setAction("android.intent.action.VIEW");
            localIntent.setData(Uri.parse("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + "https://qr.alipay.com/fkx00337aktbgg6hgq64ae2?t=1542355035868"));
            if (localIntent.resolveActivity(getActivity().getPackageManager()) != null)
            {
                startActivity(localIntent);
                return true;
            }
            localIntent.setData(Uri.parse("https://qr.alipay.com/fkx00337aktbgg6hgq64ae2?t=1542355035868".toLowerCase()));
            startActivity(localIntent);
            return true;
        });

    }
}

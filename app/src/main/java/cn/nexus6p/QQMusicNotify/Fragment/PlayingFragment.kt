package cn.nexus6p.QQMusicNotify.Fragment

import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import cn.nexus6p.QQMusicNotify.ContentProvider
import cn.nexus6p.QQMusicNotify.SharedPreferences.ContentProviderPreference
import cn.nexus6p.QQMusicNotify.SimpleAlcatrazInteractiveCardView
import soptqs.medianotification.utils.PaletteUtils

private lateinit var linearLayout: LinearLayout

class PlayingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        return linearLayout
    }

    override fun onResume() {
        super.onResume()
        linearLayout.removeAllViews()
        val packageManager = requireContext().packageManager
        val hashMap: HashMap<String,MediaSession.Token>? = ContentProviderPreference.getBundle(ContentProvider.GET_ALL_SESSION_TOKEN,null,requireContext())?.getSerializable(ContentProvider.BUNDLE_KEY_MEDIA_SESSION_TOKEN) as HashMap<String,MediaSession.Token>?
        if (hashMap==null) {
            Toast.makeText(requireContext(), "获取失败", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("XposedMusicNotify","get hashMap $hashMap")
        for (entry in hashMap) {
            val cardView = SimpleAlcatrazInteractiveCardView(requireContext())
            cardView.titleLine = try {
                packageManager.getApplicationLabel(packageManager.getApplicationInfo(entry.key,0)).toString()
            } catch (e:Exception) {
                entry.key
            }
            try{
                val metaData = MediaController(requireContext(),entry.value).metadata ?: continue
                Log.d("XposedMusicNotify",metaData.keySet().toTypedArray().joinToString())
                var title = metaData.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
                if (title.isNullOrBlank()) title = metaData.getString(MediaMetadata.METADATA_KEY_TITLE)
                cardView.firstLine = title
                cardView.secondLine = metaData.getString(MediaMetadata.METADATA_KEY_ARTIST)
                var bitmap = metaData.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON)
                if (bitmap==null) bitmap = metaData.getBitmap(MediaMetadata.METADATA_KEY_ART)
                if (bitmap==null) bitmap = metaData.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                var icon = packageManager.getApplicationIcon(entry.key)
                if (bitmap!=null) {
                    icon = BitmapDrawable(resources,bitmap)
                } else if (icon is BitmapDrawable) {
                    bitmap = icon.bitmap
                }
                cardView.iconDrawable = icon
                if (bitmap!=null) {
                    cardView.color = PaletteUtils.getSwatch(PaletteUtils.getPalette(bitmap),requireContext()).rgb
                }
                linearLayout.addView(cardView)
            }catch (e:Exception) {
                e.printStackTrace()
            }
        }
    }

}
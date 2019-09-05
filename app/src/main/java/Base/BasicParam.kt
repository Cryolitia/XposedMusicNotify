package base

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.media.session.MediaSession

class BasicParam {

    constructor()
    constructor(mContext:Context, mIcon:Int, mTitle:CharSequence, mText:CharSequence, mBitmap:Bitmap?, mStatue:Boolean, mToken:MediaSession.Token?=null, mIntent:PendingIntent){
        context = mContext
        iconID = mIcon
        titleString =mTitle
        textString = mText
        bitmap = mBitmap
        statue = mStatue
        token = mToken
        contentIntent = mIntent
    }
    constructor(mContext:Context, mIcon:Int, mTitle:CharSequence, mText:CharSequence, mBitmap:Bitmap?, mStatue:Boolean, mToken:MediaSession.Token?=null){
        context = mContext
        iconID = mIcon
        titleString =mTitle
        textString = mText
        bitmap = mBitmap
        statue = mStatue
        token = mToken
    }

    lateinit var titleString : CharSequence
    lateinit var textString : CharSequence
    var iconID : Int = -1
    var statue : Boolean = true
    var token : MediaSession.Token? = null
    var bitmap : Bitmap? = null
    var context : Context? = null
    var contentIntent : PendingIntent? = null
    var deleteIntent : PendingIntent? = null

}
package base

import android.content.Context
import android.graphics.Bitmap
import android.media.session.MediaSession
import android.provider.MediaStore

class BasicParam {

    constructor()
    constructor(mContext:Context,mIconID:Int,mTitle:CharSequence,mText:CharSequence,mBitmap:Bitmap?,mStatue:Boolean,mToken:MediaSession.Token?=null){
        context = mContext
        iconID = mIconID
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
    lateinit var context : Context

}
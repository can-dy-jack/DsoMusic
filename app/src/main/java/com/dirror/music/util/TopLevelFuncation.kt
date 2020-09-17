package com.dirror.music.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.dirror.music.MyApplication
import com.dirror.music.cloudmusic.ArtistData


/**
 * 顶层函数类
 */
val CLOUD_MUSIC_API = "https://musicapi.leanapp.cn"

/**
 * 设置状态栏图标颜色
 * @param dark true 为黑色，false 为白色
 */
fun setStatusBarIconColor(activity: Activity, dark: Boolean) {
    StatusbarColorUtils.setStatusBarDarkIcon(activity, dark)
}

/**
 * 全局 toast
 */
fun toast(msg: String) {
    runOnMainThread {
        Toast.makeText(MyApplication.context, msg, Toast.LENGTH_SHORT).show()
    }
}

/**
 * 全局 log
 */
fun loge(msg: String) {
    runOnMainThread {
        Log.e("Dirror 音乐", "【$msg】")
    }
}

// 运行在主线程，更新 UI
fun runOnMainThread(runnable: Runnable) {
    Handler(Looper.getMainLooper()).post(runnable)
}

// dp 转 px
fun dp2px(dp: Float): Float = dp * MyApplication.context.resources.displayMetrics.density

// http 转 https
fun http2https(http: String): String {
    return http.replace("http", "https")
}

fun getCurrentTime() : Long {
    return System.currentTimeMillis()
}

fun parseArtist(artistList: List<ArtistData>): String {
    var artist = ""
    for (artistName in 0..artistList.lastIndex) {
        if (artistName != 0) {
            artist += " / "
        }
        artist += artistList[artistName].name
    }
    return artist
}

/**
 * 通过浏览器打开网页
 */
fun openUrlByBrowser(context: Context, url: String) {
    val intent = Intent()
    intent.action = "android.intent.action.VIEW"
    val contentUrl = Uri.parse(url)
    intent.data = contentUrl
    startActivity(context, intent, Bundle())
}

// 毫秒转日期
fun msTimeToFormatDate(msTime: Long): String {
    return TimeUtil.msTimeToFormatDate(msTime)
}

// 获取状态栏高度
@SuppressLint("PrivateApi")
fun getStatusBarHeight(window: Window, context: Context): Int {
    val localRect = Rect()
    window.decorView.getWindowVisibleDisplayFrame(localRect)
    var mStatusBarHeight = localRect.top
    if (0 == mStatusBarHeight) {
        try {
            val localClass = Class.forName("com.android.internal.R\$dimen")
            val localObject = localClass.newInstance()
            val i5 =
                localClass.getField("status_bar_height")[localObject].toString().toInt()
            mStatusBarHeight = context.resources.getDimensionPixelSize(i5)
        } catch (var6: ClassNotFoundException) {
            var6.printStackTrace()
        } catch (var7: IllegalAccessException) {
            var7.printStackTrace()
        } catch (var8: InstantiationException) {
            var8.printStackTrace()
        } catch (var9: NumberFormatException) {
            var9.printStackTrace()
        } catch (var10: IllegalArgumentException) {
            var10.printStackTrace()
        } catch (var11: SecurityException) {
            var11.printStackTrace()
        } catch (var12: NoSuchFieldException) {
            var12.printStackTrace()
        }
    }
    if (0 == mStatusBarHeight) {
        val resourceId: Int =
            context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            mStatusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        }
    }
    return mStatusBarHeight
}

fun getNavigationBarHeight(activity: Activity): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        val display = activity.windowManager.defaultDisplay
        val size = Point()
        val realSize = Point()
        display.getSize(size)
        display.getRealSize(realSize)
        val resources: Resources = activity.resources
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val height: Int = resources.getDimensionPixelSize(resourceId)
        //超出系统默认的导航栏高度以上，则认为存在虚拟导航
        if (realSize.y - size.y > height - 10) {
            height
        } else 0
    } else {
        val menu = ViewConfiguration.get(activity).hasPermanentMenuKey()
        val back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
        if (menu || back) {
            0
        } else {
            val resources: Resources = activity.resources
            val resourceId: Int =
                resources.getIdentifier("navigation_bar_height", "dimen", "android")
            resources.getDimensionPixelSize(resourceId)
        }
    }
}


fun setNavigationBarColor(activity: Activity, color: Int) {
    val window = activity.window
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.navigationBarColor = color
        return
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    }
}
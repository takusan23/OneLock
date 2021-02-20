package io.github.takusan23.onelock

import android.app.PendingIntent
import android.app.admin.DevicePolicyManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Icon
import android.widget.ImageView
import android.widget.RemoteViews
import androidx.preference.PreferenceManager

/**
 * Implementation of App Widget functionality.
 */
class SleepButtonWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.getStringExtra("sleep") == "on") {
            val devicePolicyManager =
                context?.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            devicePolicyManager.lockNow()
        }
    }

}

fun updateAppWidget(context: Context) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.sleep_button_widget)
    // BroadCast
    val intent = Intent(context, SleepButtonWidget::class.java).apply {
        putExtra("sleep", "on")
    }
    val pendingIntent =
        PendingIntent.getBroadcast(context, 114, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    views.setOnClickPendingIntent(R.id.sleep_imageview, pendingIntent)
    // アイコンの色
    val prefSetting = PreferenceManager.getDefaultSharedPreferences(context)
    val iconColor = if (prefSetting.getBoolean("widget_color_black", true)) {
        Color.BLACK
    } else {
        Color.WHITE
    }
    // アイコンを設定
    val icon = Icon.createWithResource(context, R.drawable.ic_sleep_droid_icon)
    icon.setTintList(ColorStateList.valueOf(iconColor))
    views.setImageViewIcon(R.id.sleep_imageview, icon)
    // Contextあれば更新できる！
    val componentName = ComponentName(context, SleepButtonWidget::class.java)
    val manager = AppWidgetManager.getInstance(context)
    val ids = manager.getAppWidgetIds(componentName)
    ids.forEach { id ->
        // 更新
        manager.updateAppWidget(id, views)
    }
}
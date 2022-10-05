package com.killerypa.widgetstatus.receiver;

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.killerypa.widgetstatus.StatusWidget
import com.killerypa.widgetstatus.updateAppWidget


class DataReceiver : BroadcastReceiver() {
    internal var data: Boolean = false

    override fun onReceive(context: Context, intent: Intent) {

        val appWidgetManager = AppWidgetManager.getInstance(context);
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(
                context,
                StatusWidget::class.java
            )
        )

        intent.action?.let { act ->
            if (act.matches("android.net.conn.CONNECTIVITY_CHANGE".toRegex())) {
                Log.e(TAG, act)
//                val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
//                cm?.run {
//                    cm.getNetworkCapabilities(cm.activeNetwork)?.run {
//                        data = when {
//                            hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//                            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
//                            hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
//                            else -> false
//                        }
//                    }
//                }

                val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                Log.e(TAG, act)
                val activeNetwork = cm.activeNetworkInfo
                data = activeNetwork != null && activeNetwork.isConnectedOrConnecting
            }
        }

        for (appWidgetId in appWidgetIds) {
//            updateAppWidgetSensors(context, appWidgetManager, appWidgetId, "DATA" ,data)
            updateAppWidget(context, appWidgetManager, appWidgetId, "DATA" ,data)
        }
    }


    companion object {
        private val TAG = "BlueReceiver"
    }
}
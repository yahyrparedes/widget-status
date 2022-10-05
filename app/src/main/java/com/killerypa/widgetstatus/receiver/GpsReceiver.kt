package com.killerypa.widgetstatus.receiver;

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.util.Log
import com.killerypa.widgetstatus.StatusWidget
import com.killerypa.widgetstatus.updateAppWidget

class GpsReceiver : BroadcastReceiver() {
    internal var gps: Boolean = false

    override fun onReceive(context: Context, intent: Intent) {

        val appWidgetManager = AppWidgetManager.getInstance(context);
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(
                context,
                StatusWidget::class.java
            )
        )

        intent.action?.let { act ->
            if (act.matches("android.location.PROVIDERS_CHANGED".toRegex())) {
                Log.e(TAG, act)
                val locationManager =
                    context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                val isNetworkEnabled =
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                gps = isGpsEnabled || isNetworkEnabled
            }
        }

        for (appWidgetId in appWidgetIds) {
//            updateAppWidgetSensors(context, appWidgetManager, appWidgetId, "GPS", gps)
            updateAppWidget(context, appWidgetManager, appWidgetId, "GPS", gps)
        }
    }

    companion object {
        private val TAG = "GpsReceiver"
    }
}
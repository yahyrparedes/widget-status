package com.killerypa.widgetstatus

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.killerypa.widgetstatus.receiver.BlueReceiver
import com.killerypa.widgetstatus.receiver.DataReceiver
import com.killerypa.widgetstatus.receiver.GpsReceiver
import com.killerypa.widgetstatus.R

internal var gpsGlobal: Boolean = false
internal var blueGlobal: Boolean = false
internal var dataGlobal: Boolean = false


/**
 * Implementation of App Widget functionality.
 */
class StatusWidget : AppWidgetProvider() {

    private var TAG: String = StatusWidget::class.simpleName.toString()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        Log.e(TAG, "onUpdate")
//        getStatusAll(context)
//        registerReceiverAll(context)
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(
                context = context,
                appWidgetManager = appWidgetManager,
                appWidgetId = appWidgetId
            )
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        Log.e(TAG, "onEnabled")
        getStatusAll(context)
        registerReceiverAll(context)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        Log.e(TAG, "onDisabled")
        unregisterReceiverAll(context)
    }

    private fun getSizeWidget(context: Context): Int {
        val appWidgetManager = AppWidgetManager.getInstance(context);
        return appWidgetManager.getAppWidgetIds(
            ComponentName(
                context,
                StatusWidget::class.java
            )
        ).size
    }

    private fun unregisterReceiverAll(context: Context) {
        unregisterReceiverBlue(context)
        unregisterReceiverData(context)
        unregisterReceiverGps(context)
    }

    private fun unregisterReceiverBlue(context: Context) {
        try {
            context.applicationContext.unregisterReceiver(BlueReceiver())
        } catch (e: Exception) {
            Log.e(TAG, "unregisterReceiverBlue => " + e.message.toString())
        }
    }

    private fun unregisterReceiverData(context: Context) {
        try {
            context.applicationContext.unregisterReceiver(DataReceiver())
        } catch (e: Exception) {
            Log.e(TAG, "unregisterReceiverData => " + e.message.toString())
        }
    }

    private fun unregisterReceiverGps(context: Context) {
        try {
            context.applicationContext.unregisterReceiver(GpsReceiver())
        } catch (e: Exception) {
            Log.e(TAG, "unregisterReceiverGps => " + e.message.toString())
        }
    }

    private fun registerReceiverBlue(context: Context) {
        try {
            context.applicationContext.registerReceiver(
                BlueReceiver(),
                IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
            )
        } catch (e: Exception) {
            Log.e(TAG, "registerReceiverBlue => " + e.message.toString())
        }
    }

    private fun registerReceiverData(context: Context) {
        try {
            context.applicationContext.registerReceiver(
                DataReceiver(),
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        } catch (e: Exception) {
            Log.e(TAG, "registerReceiverData => " + e.message.toString())
        }

    }

    private fun registerReceiverGps(context: Context) {
        try {
            context.applicationContext.registerReceiver(
                GpsReceiver(),
                IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
            )
        } catch (e: Exception) {
            Log.e(TAG, "registerReceiverGps => " + e.message.toString())
        }
    }

    private fun registerReceiverAll(context: Context) {
        registerReceiverBlue(context);
        registerReceiverGps(context);
        registerReceiverData(context);
    }

    private fun getStatusGps(context: Context) {
        try {
            val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            val statusOfGPS = manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            gpsGlobal = statusOfGPS
        } catch (e: Exception) {
            Log.e(TAG, "getStatusGPS => " + e.message.toString())
        }
    }

    private fun getStatusBlue(context: Context) {
        try {
            val bluetoothManager =
                context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val mBluetoothAdapter = bluetoothManager.adapter
//            val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            blueGlobal = mBluetoothAdapter.isEnabled
        } catch (e: Exception) {
            Log.e(TAG, "getStatusBlue => " + e.message.toString())
        }
    }

    private fun getStatusData(context: Context) {
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            dataGlobal = activeNetwork?.isConnectedOrConnecting == true
        } catch (e: Exception) {
            Log.e(TAG, "getStatusData => " + e.message.toString())
        }
    }

    private fun getStatusAll(context: Context) {
        getStatusBlue(context)
        getStatusGps(context)
        getStatusData(context)
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    type: String? = null,
    status: Boolean = false
) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.status_widget)
    views.setTextViewText(R.id.appwidget_text, widgetText)


    val intent = Intent(context, MainActivity::class.java)
    val pedingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    when (type) {
        "GPS" -> {
            gpsGlobal = status
        }
        "DATA" -> {
            dataGlobal = status
        }
        "BLUE" -> {
            blueGlobal = status
        }
    }
    views.setOnClickPendingIntent(R.id.kwemaWidget, pedingIntent);

    if (gpsGlobal && dataGlobal && blueGlobal) {
        views.setTextViewText(R.id.appwidget_text, "Covered")
        views.setTextColor(R.id.appwidget_text, ContextCompat.getColor(context, R.color.colorGreen))
        views.setImageViewResource(R.id.isotype, R.drawable.ic_widget_shield_on)
    } else {
        views.setTextViewText(R.id.appwidget_text, "Not Covered")
        views.setTextColor(R.id.appwidget_text, ContextCompat.getColor(context, R.color.colorRed))
        views.setImageViewResource(R.id.isotype, R.drawable.ic_widget_shield_off)
    }

    if (blueGlobal) {
        views.setImageViewResource(R.id.blue, R.drawable.ic_widget_blue_on)
        views.setTextColor(R.id.txtBlue, ContextCompat.getColor(context, R.color.colorMainDarkText))
    } else {
        views.setImageViewResource(R.id.blue, R.drawable.ic_widget_blue_off)
        views.setTextColor(R.id.txtBlue, ContextCompat.getColor(context, R.color.colorRed))
    }

    if (gpsGlobal) {
        views.setImageViewResource(R.id.gps, R.drawable.ic_widget_gps_on)
        views.setTextColor(R.id.txtGps, ContextCompat.getColor(context, R.color.colorMainDarkText))
    } else {
        views.setImageViewResource(R.id.gps, R.drawable.ic_widget_gps_off)
        views.setTextColor(R.id.txtGps, ContextCompat.getColor(context, R.color.colorRed))
    }

    if (dataGlobal) {
        views.setImageViewResource(R.id.data, R.drawable.ic_widget_data_on)
        views.setTextColor(R.id.txtData, ContextCompat.getColor(context, R.color.colorMainDarkText))
    } else {
        views.setImageViewResource(R.id.data, R.drawable.ic_widget_data_off)
        views.setTextColor(R.id.txtData, ContextCompat.getColor(context, R.color.colorRed))
    }

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}
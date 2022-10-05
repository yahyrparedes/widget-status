package com.killerypa.widgetstatus.receiver;

import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver;
import android.content.ComponentName
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.killerypa.widgetstatus.StatusWidget
import com.killerypa.widgetstatus.updateAppWidget

class BlueReceiver : BroadcastReceiver() {
    internal var blue: Boolean = false

    override fun onReceive(context: Context, intent: Intent) {

        val appWidgetManager = AppWidgetManager.getInstance(context);
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(
                context,
                StatusWidget::class.java
            )
        )

        intent.action?.let { act ->
            if (act.matches("android.bluetooth.adapter.action.STATE_CHANGED".toRegex())) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                Log.e(TAG, act)
                when (state) {
                    BluetoothAdapter.STATE_OFF -> {
                        blue = false
                    }
                    BluetoothAdapter.STATE_ON -> {
                        blue = true
                    }
                }
            }
        }

        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, "BLUE" , blue)
        }
    }

    companion object {
        private val TAG = "BlueReceiver"
    }
}
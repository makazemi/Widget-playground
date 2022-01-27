package com.makazemi.bitcoinwidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.makazemi.bitcoinwidget.R
import com.makazemi.bitcoinwidget.model.BitcoinModel
import com.makazemi.bitcoinwidget.repository.DataState
import com.makazemi.bitcoinwidget.repository.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class BitcoinAppWidget : AppWidgetProvider() {

    @Inject
    lateinit var repository: MainRepository

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        for (appWidgetId in appWidgetIds) {
            coroutineScope.launch {
                repository.getConvertedCurrencyToBitcoin().collect {
                    updateAppWidget(context, appWidgetManager, appWidgetId, it)
                }
            }
        }


    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        job.cancel()
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    data: DataState<BitcoinModel>
) {
    val views = RemoteViews(context.packageName, R.layout.bitcoin_app_widget)
    views.setViewVisibility(
        R.id.progressBar,
        if (data.loading.isLoading) View.VISIBLE else View.GONE
    )
    val value = data.data?.peekContent()?.value
    value?.let {
        views.setTextViewText(R.id.txtValueInDollar, context.getString(R.string.value_dollar, it))
    }
    val error = data.error?.peekContent()?.message
    error?.let {
        views.setTextViewText(R.id.txtValueInDollar, error)
    }
    val intentUpdate = Intent(context, BitcoinAppWidget::class.java).apply {
        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
    }
    val pendingUpdate = PendingIntent.getBroadcast(
        context,
        appWidgetId,
        intentUpdate,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    views.setOnClickPendingIntent(R.id.btnRefresh, pendingUpdate)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}
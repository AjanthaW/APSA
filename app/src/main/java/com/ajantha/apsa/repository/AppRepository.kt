package com.ajantha.apsa.repository

import android.content.Context
import com.ajantha.apsa.ml.RiskPredictor
import com.ajantha.apsa.model.AppUiModel
import com.ajantha.apsa.model.InstalledApp
import com.ajantha.apsa.util.AppScanner
import com.ajantha.apsa.util.RiskMapper
import com.ajantha.apsa.util.toFeatureVector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(context: Context) {

    private val scanner = AppScanner(context.applicationContext)
    private val predictor = RiskPredictor(context.applicationContext)

    suspend fun getApps(): List<AppUiModel> = withContext(Dispatchers.IO) {
        scanner.getInstalledApps().map { app ->
            val score = predictor.predict(app.toFeatureVector())
            app.toUiModel(score)
        }
    }

    suspend fun getAppByPackageName(packageName: String): AppUiModel? =
        withContext(Dispatchers.IO) {
            val app = scanner.getInstalledApps().firstOrNull { it.packageName == packageName }
                ?: return@withContext null
            val score = predictor.predict(app.toFeatureVector())
            app.toUiModel(score)
        }

    private fun InstalledApp.toUiModel(score: Float): AppUiModel {
        return AppUiModel(
            app = this,
            riskPercent = (score * 100).toInt(),
            riskLevel = RiskMapper.fromScore(score)
        )
    }

}
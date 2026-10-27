package com.devpupu.pixelclock.service

import org.json.JSONObject
import java.net.URL

object WeatherFetcher {
    @Volatile var text = ""
    fun fetch(lat: Double = 18.48, lon: Double = -69.94) {
        Thread {
            try {
                val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,weather_code"
                val json = URL(url).readText()
                val cur = JSONObject(json).getJSONObject("current")
                val temp = cur.getDouble("temperature_2m")
                text = "${temp.toInt()}°"
            } catch (e: Exception) { text = "" }
        }.start()
    }
}
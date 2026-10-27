package com.devpupu.pixelclock.service

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClockWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine = ClockEngine()

    inner class ClockEngine : Engine() {
        private val paint = Paint().apply {
            color = Color.argb(90, 255, 255, 255)
            textSize = 200f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        @Volatile private var running = false

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            running = true
            drawLoop(holder)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            running = false
            super.onSurfaceDestroyed(holder)
        }

        private fun drawLoop(holder: SurfaceHolder?) {
            val h = holder ?: return
            Thread {
                var tick = 0
                while (running) {
                    if (tick % 600 == 0) com.devpupu.pixelclock.service.WeatherFetcher.fetch()
                    tick++
                    val canvas: Canvas? = h.lockCanvas()
                    canvas?.let {
                        val hasNotifs = com.devpupu.pixelclock.service.NotifListener.count > 0
                        val prefs = applicationContext.getSharedPreferences("clock", Context.MODE_PRIVATE)
                        val baseAlpha = prefs.getInt("opacity", 35).coerceIn(0, 100) * 2 + 20
                        val alpha = if (hasNotifs) baseAlpha / 2 else baseAlpha
                        val sizeScale = prefs.getInt("size", 50).coerceIn(0, 100) / 50f
                        val colorIdx = prefs.getInt("colorIdx", 0)
                        val colors = intArrayOf(Color.WHITE, Color.CYAN, Color.YELLOW, Color.GREEN)
                        val color = colors.getOrElse(colorIdx) { Color.WHITE }
                        paint.color = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
                        paint.textSize = 200f * sizeScale
                        val bgUriStr = prefs.getString("bgUri", null)
                        if (bgUriStr != null) {
                            try {
                                val uri = android.net.Uri.parse(bgUriStr)
                                val bmp = android.provider.MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, uri)
                                it.drawBitmap(bmp, null, android.graphics.Rect(0,0,it.width,it.height), null)
                            } catch (e: Exception) { it.drawColor(Color.BLACK) }
                        } else {
                            it.drawColor(Color.BLACK)
                        }
                        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                        it.drawText(time, it.width / 2f, it.height / 2f, paint)
                        paint.textSize = 60f
                        it.drawText(com.devpupu.pixelclock.service.WeatherFetcher.text, it.width / 2f, it.height / 2f + 120f, paint)
                        h.unlockCanvasAndPost(it)
                    }
                    Thread.sleep(1000)
                }
            }.start()
        }
    }
}

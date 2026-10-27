package com.devpupu.pixelclock.service

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
        private var running = false

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
                while (running) {
                    val canvas: Canvas? = h.lockCanvas()
                    canvas?.let {
                        val hasNotifs = com.devpupu.pixelclock.service.NotifListener.count > 0
                        val prefs = applicationContext.getSharedPreferences("clock", Context.MODE_PRIVATE)
                        val baseAlpha = prefs.getInt("opacity", 35) * 2 + 20  // 20..220
                        val alpha = if (hasNotifs) baseAlpha / 2 else baseAlpha
                        paint.color = Color.argb(alpha, 255, 255, 255)
                        val size = if (hasNotifs) 120f else 200f
                        paint.textSize = size
                        it.drawColor(Color.BLACK)
                        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                        it.drawText(time, it.width / 2f, it.height / 2f, paint)
                        h.unlockCanvasAndPost(it)
                    }
                    Thread.sleep(1000)
                }
            }.start()
        }
    }
}

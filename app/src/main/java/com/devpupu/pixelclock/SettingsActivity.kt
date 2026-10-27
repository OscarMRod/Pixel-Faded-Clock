package com.devpupu.pixelclock

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.*

class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("clock", Context.MODE_PRIVATE)
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(32,32,32,32) }

        fun addLabel(t: String) = TextView(this).apply { text = t; textSize = 16f }.also { layout.addView(it) }
        fun addSeek(label: String, key: String, def: Int, max: Int = 100) {
            addLabel(label)
            val seek = SeekBar(this).apply {
                this.max = max
                progress = prefs.getInt(key, def).coerceIn(0, max)
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) { prefs.edit().putInt(key, p).apply() }
                    override fun onStartTrackingTouch(s: SeekBar?) {}
                    override fun onStopTrackingTouch(s: SeekBar?) {}
                })
            }
            layout.addView(seek)
        }

        addSeek("Opacity", "opacity", 35)
        addSeek("Size", "size", 50)
        addLabel("Color")
        val colors = listOf("White" to Color.WHITE, "Cyan" to Color.CYAN, "Yellow" to Color.YELLOW, "Green" to Color.GREEN)
        val spinner = Spinner(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colors.map { it.first })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(prefs.getInt("colorIdx", 0).coerceIn(0, colors.size - 1))
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: android.view.View?, pos: Int, id: Long) { prefs.edit().putInt("colorIdx", pos).apply() }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
        layout.addView(spinner)

        addLabel("Preview")
        val preview = TextView(this).apply {
            text = "9:41"
            textSize = 48f
            setTextColor(colors[prefs.getInt("colorIdx", 0).coerceIn(0, colors.size - 1)].second)
        }
        layout.addView(preview)

        addLabel("Background image")
        val imgBtn = Button(this).apply { text = "Pick image"; setOnClickListener {
            val i = Intent(Intent.ACTION_OPEN_DOCUMENT).apply { addCategory(Intent.CATEGORY_OPENABLE); type = "image/*" }
            startActivityForResult(i, 100)
        } }
        layout.addView(imgBtn)

        setContentView(layout)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            getSharedPreferences("clock", Context.MODE_PRIVATE).edit().putString("bgUri", uri.toString()).apply()
        }
    }
}
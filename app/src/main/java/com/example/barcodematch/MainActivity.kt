package com.example.barcodematch

import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var code1 = ""
    private var code2 = ""
    private var tts: TextToSpeech? = null
    private var isFirstScanner = true

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val value = result.contents
            val tvCode1 = findViewById<TextView>(R.id.tvCode1)
            val tvCode2 = findViewById<TextView>(R.id.tvCode2)

            if (isFirstScanner) {
                code1 = value
                tvCode1.text = "ค่าที่ 1: $code1"
            } else {
                code2 = value
                tvCode2.text = "ค่าที่ 2: $code2"
            }
            checkMatch()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tts = TextToSpeech(this, this)

        val btnScan1 = findViewById<Button>(R.id.btnScan1)
        val btnScan2 = findViewById<Button>(R.id.btnScan2)
        val btnClear = findViewById<Button>(R.id.btnClear)

        btnScan1.setOnClickListener {
            isFirstScanner = true
            launchScanner()
        }

        btnScan2.setOnClickListener {
            isFirstScanner = false
            launchScanner()
        }

        // ฟังก์ชันปุ่ม Clear
        btnClear.setOnClickListener {
            resetApp()
        }
    }

    private fun launchScanner() {
        val options = ScanOptions()
        options.setPrompt("วางบาร์โค้ดในกรอบ (แนวตั้ง)")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true) // เปลี่ยนจาก false เป็น true
        options.setCaptureActivity(com.journeyapps.barcodescanner.CaptureActivity::class.java)
        barcodeLauncher.launch(options)
    }

    private fun checkMatch() {
        val tvResult = findViewById<TextView>(R.id.tvResult)
        val cardResult = findViewById<CardView>(R.id.cardResult)

        if (code1.isNotEmpty() && code2.isNotEmpty()) {
            if (code1 == code2) {
                tvResult.text = "MATCH ✅"
                tvResult.setTextColor(Color.WHITE)
                cardResult.setCardBackgroundColor(Color.parseColor("#4CAF50")) // สีเขียว
                speakOut("Match")
            } else {
                tvResult.text = "UNMATCH ❌"
                tvResult.setTextColor(Color.WHITE)
                cardResult.setCardBackgroundColor(Color.parseColor("#F44336")) // สีแดง
                speakOut("Unmatch")
            }
        }
    }

    private fun resetApp() {
        code1 = ""
        code2 = ""
        findViewById<TextView>(R.id.tvCode1).text = "ค่าที่ 1: -"
        findViewById<TextView>(R.id.tvCode2).text = "ค่าที่ 2: -"
        findViewById<TextView>(R.id.tvResult).text = "รอการสแกน..."
        findViewById<TextView>(R.id.tvResult).setTextColor(Color.parseColor("#999999"))
        findViewById<CardView>(R.id.cardResult).setCardBackgroundColor(Color.WHITE)
        Toast.makeText(this, "รีเซ็ตค่าเรียบร้อย", Toast.LENGTH_SHORT).show()
    }

    private fun speakOut(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) tts?.language = Locale.ENGLISH
    }

    override fun onDestroy() {
        tts?.shutdown()
        super.onDestroy()
    }
}
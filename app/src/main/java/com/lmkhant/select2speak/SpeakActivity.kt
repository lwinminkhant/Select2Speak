package com.lmkhant.select2speak

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.lmkhant.select2speak.databinding.ActivitySpeakBinding
import java.util.*


class SpeakActivity : Activity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private lateinit var binding: ActivitySpeakBinding
    private lateinit var selectedText: String
    private lateinit var viewGroup: ViewGroup
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpeakBinding.inflate(layoutInflater)

        setContentView(binding.root)
        tts = TextToSpeech(this, this)

        selectedText = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()


        applyTheme()
        setUpLayout()

    }

    private fun setUpLayout(){
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        viewGroup = getRelativeLayout(applicationContext) as ViewGroup
        binding.tvSpeak.text = selectedText
        binding.btnClose.setOnClickListener {
            Toast.makeText(this,"end",Toast.LENGTH_SHORT).show()
            endDialog()
        }
        binding.btnStop.setOnClickListener {
            stopSpecking()
        }
        binding.btnRepeat.setOnClickListener {
            speakOut(selectedText)
        }
        binding.bg.setOnClickListener {
            endDialog()
        }
        windowManager.addView(viewGroup,getCustomParams())
    }

    fun identifyLanguage(text: String):Locale{
        var locale: Locale = Locale.getDefault()
        val languageIdentifier = LanguageIdentification.getClient(
            LanguageIdentificationOptions.Builder()
                .setConfidenceThreshold(0.34f)
                .build()
        )
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                if (languageCode == "und") {
                    Toast.makeText(this, "Can't identify the language", Toast.LENGTH_SHORT).show()
                } else {
                    locale = Locale.forLanguageTag(languageCode)
                    Log.d("SpeakActivity", locale.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to identify the language", Toast.LENGTH_SHORT).show()
            }
        Toast.makeText(this, locale.toString(), Toast.LENGTH_SHORT).show()
        return locale
    }
    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(identifyLanguage(selectedText))

            if(result == TextToSpeech.LANG_MISSING_DATA
                || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(this, "Language specified is not supported", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show()
        }

        speakOut(selectedText)

    }

    private fun speakOut(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun stopSpecking() {
        if (tts != null) {
            tts?.stop()
        }
    }
    private fun applyTheme(){
        val tintColor = R.color.primaryColor
        binding.btnClose.setColorFilter(tintColor)
        binding.btnStop.setColorFilter(tintColor)
        binding.btnRepeat.setColorFilter(tintColor)
    }
    private fun getRelativeLayout(context: Context): RelativeLayout? {
        return object : RelativeLayout(context) {
            override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                    endDialog()
                    return true
                }
                return super.dispatchKeyEvent(event)
            }
        }
    }
    fun endDialog() {
        try {
            tts?.shutdown()
            windowManager.removeView(viewGroup)
            finish()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun getCustomParams(): WindowManager.LayoutParams {
        val params = WindowManager.LayoutParams()
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        //params.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        params.format = PixelFormat.TRANSLUCENT
        window.attributes = params
        return params
    }
}
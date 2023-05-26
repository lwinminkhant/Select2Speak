package com.lmkhant.select2speak

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.lmkhant.select2speak.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityMainBinding
    private var tts: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val text = binding.etTextSpeak.text
        delegate.isHandleNativeActionModesEnabled = false

        tts = TextToSpeech(this, this)


        binding.btnSpeak.setOnClickListener {
            speakOut(text.toString())
        }
        binding.btnStop.setOnClickListener {
            stopSpeck()
        }
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.US)

            if(result == TextToSpeech.LANG_MISSING_DATA
                || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(this, "Language specified is not supported", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speakOut(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun stopSpeck() {
        if (tts != null) {
            tts?.stop()
            //tts?.shutdown()
        }
    }
}
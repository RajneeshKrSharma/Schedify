package com.unique.schedify.core.util.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.unique.schedify.R

class VoiceRecognizerHelper(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit = {},
    private val onSpeechStateChanged: (Boolean) -> Unit = {}
) {
    private var speechRecognizer: SpeechRecognizer? = null

    fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition not available")
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle?) {
                    onSpeechStateChanged(false) // ensure we stop the "speaking" state
                    val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    data?.firstOrNull()?.let(onResult)
                }

                override fun onError(error: Int) {
                    val message = when (error) {
                        SpeechRecognizer.ERROR_AUDIO ->
                            context.getString(R.string.there_was_a_problem_recording_your_voice_please_try_again)
                        SpeechRecognizer.ERROR_CLIENT ->
                            context.getString(R.string.something_went_wrong_please_try_again)
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                            context.getString(R.string.microphone_permission_is_missing_please_enable_it_in_settings)
                        SpeechRecognizer.ERROR_NETWORK ->
                            context.getString(R.string.network_error_please_check_your_internet_connection)
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                            context.getString(R.string.network_timeout_please_try_again)
                        SpeechRecognizer.ERROR_NO_MATCH ->
                            context.getString(R.string.couldn_t_understand_try_speaking_more_clearly)
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
                            context.getString(R.string.voice_recognition_is_busy_please_wait_a_moment)
                        SpeechRecognizer.ERROR_SERVER ->
                            context.getString(R.string.server_error_please_try_again_later)
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                            context.getString(R.string.didn_t_catch_that_please_speak_louder_or_try_again)
                        else ->
                            context.getString(R.string.unknown_error_occurred_code, error.toString())
                    }

                    onSpeechStateChanged(false)
                    onError(message)
                }


                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {
                    onSpeechStateChanged(true)
                }

                override fun onEndOfSpeech() {
                    onSpeechStateChanged(false)
                }

                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }

        speechRecognizer?.startListening(intent)
    }

    fun stop() {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
    }
}


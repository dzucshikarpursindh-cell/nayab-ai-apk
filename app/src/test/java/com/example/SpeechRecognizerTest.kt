package com.example

import android.content.Context
import android.speech.RecognizerIntent
import androidx.test.core.app.ApplicationProvider
import com.example.audio.SpeechManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SpeechRecognizerTest {

    @Test
    fun testSindhiSpeechIntentConfiguration() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val speechManager = SpeechManager(context)

        val intent = speechManager.createSpeechIntent("sd")
        assertEquals(RecognizerIntent.ACTION_RECOGNIZE_SPEECH, intent.action)
        assertEquals(RecognizerIntent.LANGUAGE_MODEL_FREE_FORM, intent.getStringExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL))
        assertEquals("sd-PK", intent.getStringExtra(RecognizerIntent.EXTRA_LANGUAGE))
        assertEquals("sd-PK", intent.getStringExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE))
        assertTrue(intent.getBooleanExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false))

        val additionalLangs = intent.getStringArrayExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES")
        assertNotNull(additionalLangs)
        assertTrue(additionalLangs!!.contains("sd-PK"))
        assertTrue(additionalLangs.contains("ur-PK"))
    }

    @Test
    fun testUrduSpeechIntentConfiguration() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val speechManager = SpeechManager(context)

        val intent = speechManager.createSpeechIntent("ur")
        assertEquals("ur-PK", intent.getStringExtra(RecognizerIntent.EXTRA_LANGUAGE))
        assertEquals("ur-PK", intent.getStringExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE))
    }

    @Test
    fun testEnglishSpeechIntentConfiguration() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val speechManager = SpeechManager(context)

        val intent = speechManager.createSpeechIntent("en")
        assertEquals("en-US", intent.getStringExtra(RecognizerIntent.EXTRA_LANGUAGE))
        assertEquals("en-US", intent.getStringExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE))
    }

    @Test
    fun testSpeechManagerInitialStates() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val speechManager = SpeechManager(context)

        assertFalse(speechManager.isListening.value)
        assertFalse(speechManager.isSpeaking.value)
        assertEquals(0.0f, speechManager.rmsLevel.value, 0.001f)
        assertEquals(null, speechManager.speechError.value)
    }

    @Test
    fun testCleanTextForSpeech() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val speechManager = SpeechManager(context)

        val rawText = "**سنڌي ٻولي:** سنڌ جي قديم ثقافت آهي! #تاريخ"
        val cleaned = speechManager.cleanTextForSpeech(rawText)
        assertFalse(cleaned.contains("**"))
        assertTrue(cleaned.contains("سنڌي ٻولي:"))
        assertTrue(cleaned.contains("ثقافت آهي!"))
    }
}

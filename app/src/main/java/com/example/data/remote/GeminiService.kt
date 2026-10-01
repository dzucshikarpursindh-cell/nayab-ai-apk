package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * GeminiService - Google Gemini API ke sath rabta qayam karne ki service
 * Nayab AI ka dimagh jo Sindhi, Urdu aur English mein jawabat tayar karta hai.
 */
class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    // Model name: gemini-3.5-flash as per Gemini API guidelines
    private val modelName = "gemini-3.5-flash"

    /**
     * Gemini API ko message bhejna aur response hasil karna
     * @param userPrompt User ka bheja gaya sawal ya baat
     * @param conversationHistory Pehle ki guftagu ka record (context ke liye)
     * @param userApiKey Agar user ne settings mein custom API key di ho
     * @param targetLanguage Preferred language hint ("sd", "ur", "en")
     */
    suspend fun generateResponse(
        userPrompt: String,
        conversationHistory: List<Pair<String, String>> = emptyList(),
        userApiKey: String = "",
        targetLanguage: String = "sd"
    ): String = withContext(Dispatchers.IO) {
        // API key tarteeb: pehle user ki custom key, phir BuildConfig.GEMINI_API_KEY
        val effectiveApiKey = when {
            userApiKey.isNotBlank() -> userApiKey
            BuildConfig.GEMINI_API_KEY.isNotBlank() -> BuildConfig.GEMINI_API_KEY
            else -> ""
        }

        if (effectiveApiKey.isBlank() || effectiveApiKey == "MY_GEMINI_API_KEY") {
            Log.w("GeminiService", "API Key nahi mili, smart local fallback istemal ho raha hai.")
            return@withContext getSmartLocalFallback(userPrompt, targetLanguage)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$effectiveApiKey"

            // System instructions jo Nayab AI ki shakhsiyat tay karti hain
            val systemInstructionText = """
                You are Nayab AI, a warm, culturally respectful, and intelligent Sindhi AI Assistant (Aap Ka Apna Sindhi AI Assistant).
                You fluently speak and write in Sindhi (سنڌي), Urdu (اردو), and English.
                Rules:
                1. Always respond in the EXACT same language that the user uses:
                   - If the user communicates in Sindhi (سنڌي رسم الخط ya Roman Sindhi), respond in fluent, beautiful Sindhi.
                   - If the user communicates in Urdu (اردو رسم الخط ya Roman Urdu), respond in polite Urdu.
                   - If the user communicates in English, respond in clear English.
                2. Be polite, friendly, helpful, and knowledgeable about Sindh's culture, heritage, history, literature (like Shah Abdul Latif Bhittai, Sachal Sarmast), geography, as well as general technology, science, and everyday questions.
                3. Keep responses concise, well-structured, and easy to read.
            """.trimIndent()

            // JSON Request body banana
            val rootJson = JSONObject()

            // System instruction add karna
            val systemInstructionJson = JSONObject().apply {
                val partsArray = JSONArray().apply {
                    put(JSONObject().apply { put("text", systemInstructionText) })
                }
                put("parts", partsArray)
            }
            rootJson.put("systemInstruction", systemInstructionJson)

            // Contents array (context + current message)
            val contentsArray = JSONArray()

            // Akhri 6 messages ka context shamil karna
            val recentHistory = conversationHistory.takeLast(6)
            for ((sender, text) in recentHistory) {
                val role = if (sender == "user") "user" else "model"
                val contentItem = JSONObject().apply {
                    put("role", role)
                    val parts = JSONArray().apply {
                        put(JSONObject().apply { put("text", text) })
                    }
                    put("parts", parts)
                }
                contentsArray.put(contentItem)
            }

            // Mojooda prompt add karna
            val currentMessage = JSONObject().apply {
                put("role", "user")
                val parts = JSONArray().apply {
                    put(JSONObject().apply { put("text", userPrompt) })
                }
                put("parts", parts)
            }
            contentsArray.put(currentMessage)
            rootJson.put("contents", contentsArray)

            // Generation config
            val generationConfig = JSONObject().apply {
                put("temperature", 0.7)
                put("topP", 0.95)
                put("topK", 40)
            }
            rootJson.put("generationConfig", generationConfig)

            val requestBody = rootJson.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) {
                Log.e("GeminiService", "API Error: ${response.code} -> $responseBody")
                return@withContext getSmartLocalFallback(userPrompt, targetLanguage)
            }

            val responseJson = JSONObject(responseBody)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text")
                    if (text.isNotBlank()) {
                        return@withContext text.trim()
                    }
                }
            }

            return@withContext getSmartLocalFallback(userPrompt, targetLanguage)
        } catch (e: Exception) {
            Log.e("GeminiService", "Exception during Gemini call", e)
            return@withContext getSmartLocalFallback(userPrompt, targetLanguage)
        }
    }

    /**
     * Smart local fallback generator:
     * Agar internet kamzor ho ya API key enter na ki gayi ho,
     * to Nayab AI foran Sindhi, Urdu aur English mein maaloomaat faraham karta hai.
     */
    private fun getSmartLocalFallback(prompt: String, lang: String): String {
        val lower = prompt.lowercase()
        val isSindhiScript = prompt.any { it in '\u0600'..'\u06FF' } && (lang == "sd" || lower.contains("سنڌ") || lower.contains("ڀٽائي") || lower.contains("آهيان"))
        val isUrduScript = prompt.any { it in '\u0600'..'\u06FF' } && (lang == "ur" || lower.contains("کیسے") || lower.contains("کون") || lower.contains("اردو"))

        // Greetings
        if (lower.contains("salam") || lower.contains("سلام") || lower.contains("hello") || lower.contains("hi")) {
            return when {
                isSindhiScript || lang == "sd" ->
                    "وعليڪم السلام! مان ناياب AI آهيان، توهان جو پنهنجو سنڌي AI مددگار. مان سنڌي، اردو ۽ انگريزي ۾ ڳالهائي سگهان ٿو. اڄ توهان کي ڇا ڄاڻڻو آهي؟"
                isUrduScript || lang == "ur" ->
                    "وعلیکم السلام! میں نایاب AI ہوں، آپ کا اپنا سندھی AI اسسٹنٹ۔ میں سندھی، اردو اور انگریزی سمجھتا اور بولتا ہوں۔ فرمائیے، میں آپ کی کیا مدد کر سکتا ہوں؟"
                else ->
                    "Wa Alaikum Assalam! I am Nayab AI, your personal Sindhi AI Assistant. I can assist you in Sindhi, Urdu, and English. How can I help you today?"
            }
        }

        // Sindh / Sindhi Culture queries
        if (lower.contains("sindh") || lower.contains("سنڌ") || lower.contains("سندھ") || lower.contains("ajrak") || lower.contains("اجرک")) {
            return when {
                isSindhiScript || lang == "sd" ->
                    "سنڌ محبت، امن ۽ صوفين جي سرزمين آهي. سنڌ جي تاريخ موهن جو دڙو کان وٺي هزارين سال پراڻي آهي. اجرڪ ۽ سنڌي ٽوپي اسان جي عظيم ثقافت ۽ سڃاڻپ جو نشان آهن. شاهه لطيف ۽ سچل سرمست جهڙن عظيم صوفين سنڌي ٻوليءَ کي پنهنجي شاعريءَ ذريعي امر بڻايو."
                isUrduScript || lang == "ur" ->
                    "سندھ امن، محبت اور صوفیاء کی سرزمین ہے۔ اس کی تہذیب موہنجودڑو کے قدیم آثار سے جڑی ہوئی ہے۔ سندھی اجرک اور ٹوپی محبت اور مہمان نوازی کا لازوال نشان ہیں۔ شاہ عبداللطیف بھٹائی اور سچل سرمست نے سندھ کی دھرتی کو محبت اور آشتی کا پیغام دیا۔"
                else ->
                    "Sindh is a land of peace, Sufism, and millennia of civilization dating back to Mohenjo-daro. The Sindhi Ajrak and Topi represent royal hospitality and pride. Great Sufi poets like Shah Abdul Latif Bhittai spread timeless messages of universal love and tolerance across the Indus valley."
            }
        }

        // Shah Abdul Latif Bhittai queries
        if (lower.contains("bhittai") || lower.contains("ڀٽائي") || lower.contains("بھٹائی") || lower.contains("latif") || lower.contains("لطيف")) {
            return when {
                isSindhiScript || lang == "sd" ->
                    "حضرت شاهه عبداللطيف ڀٽائي رح سنڌ جو سڀ کان عظيم صوفي شاعر آهي. سندن ڪلام 'شاهه جو رسالو' امن، محبت ۽ روحاني سچائيءَ جو خزينو آهي. سر مارئي، سر سسئي، ۽ سر سارنگ سنڌ جي محبت ۽ الله تعاليٰ جي ياد جا خوبصورت سر آهن.\n\n\"سائينم سدائين ڪريين مٿي سنڌ سڪار،\nدوست مٺا دلدار، عالم سڀ آباد ڪرين!\""
                else ->
                    "Hazrat Shah Abdul Latif Bhittai is the great crown of Sindhi classical poetry. His masterpiece 'Shah Jo Risalo' is revered worldwide. His universal prayer remains legendary:\n\n\"O Lord, may You always bestow prosperity upon Sindh,\nO Gracious Friend, make the entire world prosperous and peaceful!\""
            }
        }

        // Who are you / Nayab AI
        if (lower.contains("who are you") || lower.contains("nayab") || lower.contains("ناياب") || lower.contains("کون ہو") || lower.contains("ڪير آهين")) {
            return when {
                isSindhiScript || lang == "sd" ->
                    "مان ناياب AI آهيان—\"توهان جو پنهنجو سنڌي AI مددگار\"! مان لکت ۽ آواز ذريعي سنڌي، اردو ۽ انگريزي ٻولين ۾ توهان جا سوال حل ڪرڻ، مضمون لکڻ، ترجمو ڪرڻ ۽ رهنمائي ڪرڻ لاءِ تيار آهيان. لامحدود سوالن لاءِ توهان سيٽنگس ۾ گوگل جيمينائي API Key به لڳائي سگهو ٿا."
                isUrduScript || lang == "ur" ->
                    "میں نایاب AI ہوں—\"آپ کا اپنا سندھی AI اسسٹنٹ\"! میں آواز اور تحریر دونوں کے ذریعے سندھی، اردو اور انگریزی میں جواب دینے کی صلاحیت رکھتا ہوں۔ لامحدود AI صلاحیتوں کے لیے آپ سیٹنگز میں اپنی Gemini API Key بھی شامل کر سکتے ہیں۔"
                else ->
                    "I am Nayab AI—\"Aap Ka Apna Sindhi AI Assistant\"! I am designed to communicate fluently in Sindhi, Urdu, and English via text and voice. For unlimited generative AI capabilities, you can enter your Google Gemini API Key in the Settings screen."
            }
        }

        // Default supportive reply
        return when {
            isSindhiScript || lang == "sd" ->
                "مهرباني! توهان جو سوال مليو: \"$prompt\"\nمان ناياب AI آهيان. وڌيڪ معلومات، مڪمل تفصيل ۽ تخليقي جواب حاصل ڪرڻ لاءِ، مهرباني ڪري سيٽنگس (Settings) ۾ وڃي پنهنجي Gemini API Key درج فرمايو، يا ٻيو ڪو سوال پڇو!"
            isUrduScript || lang == "ur" ->
                "بہت شکریہ! آپ کا سوال موصول ہوا: \"$prompt\"\nمیں نایاب AI ہوں۔ مزید تفصیلی اور خودکار تخلیقی جوابات حاصل کرنے کے لیے، براہ کرم سیٹنگز (Settings) اسکرین میں اپنی Gemini API Key درج کریں، یا مجھ سے کوئی اور سوال پوچھیں۔"
            else ->
                "Thank you! I received your query: \"$prompt\".\nI am Nayab AI. To unlock full real-time generative responses for all topics, please configure your Gemini API Key in the Settings menu, or ask another question in Sindhi, Urdu, or English!"
        }
    }
}

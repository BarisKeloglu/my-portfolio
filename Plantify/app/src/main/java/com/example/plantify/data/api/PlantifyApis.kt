package com.example.plantify.data.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

object PlantifyApis {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private const val TAG = "PlantifyApis"

    var plantNetApiKey: String = ""
    var geminiApiKey: String = ""


    suspend fun autoIdentifyPlantWithPlAntNet(imageFile: File, userApiKey: String = ""): Pair<String, String>? = withContext(Dispatchers.IO) {
        val activeKey = userApiKey.ifEmpty { plantNetApiKey }
        var detected: Pair<String, String>? = null


        if (activeKey.isNotEmpty() && !activeKey.startsWith("PLACEHOLDER") && activeKey.length > 5) {
            try {
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "images", 
                        imageFile.name, 
                        imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    )
                    .addFormDataPart("organs", "leaf")
                    .build()

                val request = Request.Builder()
                    .url("https://my-api.plantnet.org/v2/identify/all?api-key=$activeKey&lang=tr")
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (!responseBody.isNullOrEmpty()) {
                            val rootJson = JSONObject(responseBody)
                            val resultsArray = rootJson.optJSONArray("results")
                            if (resultsArray != null && resultsArray.length() > 0) {
                                val bestMatch = resultsArray.getJSONObject(0)
                                val species = bestMatch.optJSONObject("species")
                                if (species != null) {
                                    val latinName = species.optString("scientificNameWithoutAuthor")
                                        .ifEmpty { species.optString("scientificName") } ?: "Bilinmiyor"
                                    val commonNames = species.optJSONArray("commonNames")
                                    val commonName = if (commonNames != null && commonNames.length() > 0) commonNames.getString(0) else latinName
                                    detected = Pair(latinName, commonName)
                                }
                            }
                        }
                    } else {
                        Log.w(TAG, "Pl@ntNet API returned error status: ${response.code}")
                    }
                    Unit
                }
            } catch (e: Exception) {
                Log.w(TAG, "Pl@ntNet API request failed", e)
            }
        }

        return@withContext detected
    }

    suspend fun identifyWithGemini(imageFile: File, userApiKey: String = ""): Pair<String, String>? = withContext(Dispatchers.IO) {
        val activeKey = userApiKey.ifEmpty { geminiApiKey }
        if (activeKey.isEmpty() || activeKey.startsWith("PLACEHOLDER")) {
            Log.w(TAG, "Gemini API key is empty or default for identification proxy")
            return@withContext null
        }
        try {
            val bytes = imageFile.readBytes()
            val base64Data = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT or android.util.Base64.NO_WRAP)
            
            val jsonPayload = JSONObject().apply {
                val contentsArray = org.json.JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", org.json.JSONArray().apply {
                            put(JSONObject().apply {
                                put("inlineData", JSONObject().apply {
                                    put("mimeType", "image/jpeg")
                                    put("data", base64Data)
                                })
                            })
                            put(JSONObject().apply {
                                put("text", "Bu resimdeki ev bitkisini teşhis et. Türkçe olarak yanıt ver. Lütfen aşağıdaki JSON biçiminde doğrudan ve sadece JSON olan bir yanıt döndür, önüne arkasına hiçbir açıklama veya markdown işaretlemesi koyma (```json gibi etiketler ekleme):\n{\"scientificName\": \"Bitkinin Latince Adı\", \"commonName\": \"Bitkinin Türkçe Adı\"}")
                            })
                        })
                    })
                }
                put("contents", contentsArray)
            }

            val requestBody = jsonPayload.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$activeKey")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val rootJson = JSONObject(responseBody)
                        val candidates = rootJson.optJSONArray("candidates")
                        if (candidates != null && candidates.length() > 0) {
                            val firstCandidate = candidates.getJSONObject(0)
                            val content = firstCandidate.optJSONObject("content")
                            if (content != null) {
                                val parts = content.optJSONArray("parts")
                                if (parts != null && parts.length() > 0) {
                                    var textResult = parts.getJSONObject(0).optString("text").trim()
                                    if (textResult.startsWith("```json")) {
                                        textResult = textResult.substringAfter("```json").substringBefore("```").trim()
                                    } else if (textResult.startsWith("```")) {
                                        textResult = textResult.substringAfter("```").substringBefore("```").trim()
                                    }
                                    val resultJson = JSONObject(textResult)
                                    val scientificName = resultJson.optString("scientificName", "Bilinmiyor")
                                    val commonName = resultJson.optString("commonName", "Bilinmeyen Bitki")
                                    return@withContext Pair(scientificName, commonName)
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini plant identification failed", e)
        }
        return@withContext null
    }


    suspend fun getPlantInsights(plantName: String, plantType: String, userApiKey: String = ""): String = withContext(Dispatchers.IO) {
        val activeKey = userApiKey.ifEmpty { geminiApiKey }
        
        if (activeKey.isEmpty() || activeKey.startsWith("PLACEHOLDER") || activeKey.length < 5) {
            return@withContext "Yapay zeka analizine ulaşılamıyor: Gemini API anahtarı eksik veya geçersiz. Lütfen geliştiricinize bildirin."
        }


        try {
            val querySubject = plantType.ifEmpty { plantName }
            // JSON Payload for Gemini models
            val jsonPayload = JSONObject().apply {
                val contentsArray = org.json.JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", org.json.JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "Sen profesyonel bir botanikçisin. Sınıfı/Türü \"$querySubject\" olan ev bitkisi hakkında (Özel adı: \"$plantName\") Türkçe olarak 3-4 cümlelik kısa, profesyonel ve ilgi çekici bakım ipuçları ve genel bilgi ver. Sadece metin döndür, formatlama veya markdown yapma.")
                            })
                        })
                    })
                }
                put("contents", contentsArray)
            }

            val requestBody = jsonPayload.toString().toRequestBody("application/json".toMediaTypeOrNull())
            
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$activeKey")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val rootJson = JSONObject(responseBody)
                        val candidates = rootJson.optJSONArray("candidates")
                        if (candidates != null && candidates.length() > 0) {
                            val firstCandidate = candidates.getJSONObject(0)
                            val content = firstCandidate.optJSONObject("content")
                            if (content != null) {
                                val parts = content.optJSONArray("parts")
                                if (parts != null && parts.length() > 0) {
                                    val textResult = parts.getJSONObject(0).optString("text")
                                    if (textResult.isNotEmpty()) {
                                        return@withContext textResult.trim()
                                    }
                                }
                            }
                        }
                    }
                    return@withContext "Yapay zeka analizine ulaşılamıyor: Boş veya geçersiz yanıt alındı."
                } else {
                    return@withContext "Yapay zeka analizine ulaşılamıyor: Sunucu hatası (Hata kodu: ${response.code})."
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini network query failed", e)
            return@withContext "Gemini API sorgusu başarısız oldu: ${e.message ?: "Ağ veya bağlantı sorunu."}"
        }
    }
}

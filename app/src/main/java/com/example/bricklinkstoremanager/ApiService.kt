package com.example.bricklinkstoremanager

import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import se.akerfeldt.okhttp.signpost.SigningInterceptor

class ApiService {

    private val TAG = "ApiRequest"
    private val consumerKey = "3241E3C32EE143A5A58B6C3D22D09661"
    private val consumerSecret = "D203B54BAD50433AB09BFCAF967AAFF4"
    private val tokenValue = "6B89CC444B384970A7DDA1D09BAC21D1"
    private val tokenSecret = "E7ECEE44041E47E69F6115BB3947A92F"
    private val baseURL = "https://api.bricklink.com/api/store/v1"

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun apiItemGetRequest(itemType: String, itemId: String, location: String, queries: Map<String, String>): String {
        Log.d(TAG, "Starting API request for $itemType $itemId")
        val endpoint = "items"


        var url = "$baseURL/$endpoint/$itemType/$itemId"
        if (location != "null"){
            url = "$baseURL/$endpoint/$itemType/$itemId/$location"
        }

        // Add queries to the URL
        if (queries.isNotEmpty()) {
            url += "?"
            for ((key, value) in queries) {
                url += "$key=$value&"
            }
            // Remove the last "&"
            url = url.dropLast(1)
        }
        Log.d(TAG, url)
        return makeGetRequest(url)
    }

    suspend fun apiSimpleGetRequest(endpoint: String, queries: Map<String, String>): String {
        var url = "$baseURL/$endpoint"
        // Add queries to the URL
        if (queries.isNotEmpty()) {
            url += "?"
            for ((key, value) in queries) {
                url += "$key=$value&"
            }
            // Remove the last "&"
            url = url.dropLast(1)
        }
        Log.d(TAG, url)
        return makeGetRequest(url)
    }



    private suspend fun makeGetRequest(url: String): String {
        val consumer = OkHttpOAuthConsumer(consumerKey, consumerSecret)
        consumer.setTokenWithSecret(tokenValue, tokenSecret)
        val client = OkHttpClient.Builder()
            .addInterceptor(SigningInterceptor(consumer))
            .build()
        val request = Request.Builder()
            .url(url)
            .build()

        return withContext(Dispatchers.IO) {

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: "Response body is empty"
            Log.d(TAG, responseBody)
            responseBody
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun apiPostRequest(postBody: String): String {
        Log.d(TAG, "Starting API Post Request")
        val endpoint = "inventories"

        val url = "$baseURL/$endpoint"

        Log.d(TAG, url)
        return makePostRequest(url, postBody)
    }

    private suspend fun makePostRequest(url: String, postBody: String): String {
        val consumer = OkHttpOAuthConsumer(consumerKey, consumerSecret)
        consumer.setTokenWithSecret(tokenValue, tokenSecret)
        val client = OkHttpClient.Builder()
            .addInterceptor(SigningInterceptor(consumer))
            .build()

        Log.d("tags", postBody)
        val requestBody = postBody.toRequestBody()

        val request = Request.Builder()
            .url(url)
            .header("Content-Type", "application/json")
            .post(requestBody)
            .build()
        Log.d("tags", request.headers.toString())

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: "Response body is empty"
            Log.d(TAG, "Response Body: $responseBody")
            responseBody
        }
    }
}
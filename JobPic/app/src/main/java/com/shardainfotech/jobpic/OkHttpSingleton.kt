package com.shardainfotech.jobpic

import com.shardainfotech.jobpic.Constants.POST_JOB
import okhttp3.*
import java.io.File


class OkHttpSingleton private constructor() { // Private so that this cannot be instantiated.

    companion object {

        private var singletonInstance: OkHttpSingleton? = null

        val instance: OkHttpSingleton
            get() {
                if (singletonInstance == null) {
                    singletonInstance = OkHttpSingleton()
                }
                return singletonInstance as OkHttpSingleton
            }
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .build()

    /**
     * hit the API to upload the job data
     */
    public fun postJobData(token: String, name: String, desc: String, address: String, images: ArrayList<String>, callback: Callback) {
        val requestBodyBuilder: MultipartBody.Builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        // add the images as part data
        images.forEach {
            val fileName: String = it.substring(it.lastIndexOf("/"))
            requestBodyBuilder.addFormDataPart(
                "images[]",
                fileName,
                RequestBody.create(MediaType.parse("image/jpeg"), File(it))
            )
        }

        // add the name of the job
        requestBodyBuilder.addFormDataPart("job_name", name)
        // add the job description
        requestBodyBuilder.addFormDataPart("job_description", desc)
        // add the address
        requestBodyBuilder.addFormDataPart("address", address)


        val request = Request.Builder()
            .url(POST_JOB)
            .addHeader(
                "authorization",
                "Bearer $token"
            )
            .post(requestBodyBuilder.build())
            .build()

        client.newCall(request).enqueue(callback)
    }
}
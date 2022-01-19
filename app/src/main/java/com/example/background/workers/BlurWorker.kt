package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI

class BlurWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val appContext = applicationContext
        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        makeStatusNotification("Blurring image", appContext)
        sleep()

        return try {
            if (TextUtils.isEmpty(resourceUri))
                throw IllegalArgumentException("Invalid input uri")
            val resolver = appContext.contentResolver
            val picture =
                BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(resourceUri)))
            val output = blurBitmap(picture, appContext)
            val outputUri = writeBitmapToFile(applicationContext, output)
            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())
            Result.success(outputData)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            Result.failure()
        }
    }
}

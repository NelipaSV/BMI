package io.nelipa.bmi.utils.bitmap

import android.content.Context
import javax.inject.Inject
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import io.nelipa.bmi.BuildConfig
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class ScreenshotUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun takeScreenshot(rootView: View): Bitmap? {
        rootView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(rootView.drawingCache)
        rootView.isDrawingCacheEnabled = false
        return bitmap
    }

    fun saveBitmap(bitmap: Bitmap, localFileName: String?): Uri? {
        val imagePath = File(context.externalCacheDir.toString() + "/screenshot.jpg")
        val fos: FileOutputStream
        return try {
            fos = FileOutputStream(imagePath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            val uri = FileProvider.getUriForFile(
                    context,
            BuildConfig.APPLICATION_ID + "." + localFileName + ".provider",
                imagePath)
            uri
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
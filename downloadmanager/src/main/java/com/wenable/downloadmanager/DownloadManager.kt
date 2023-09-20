import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Picture
import android.os.Environment
import android.provider.MediaStore
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.OutputStream
import java.net.URL

class DownloadManager {

    suspend fun downloadAndSaveImageToGallery(
        context: Context,
        imageUrl: String,
        albumName: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = URL(imageUrl).openStream()

                // Try to decode the image as a bitmap
                val bitmap = BitmapFactory.decodeStream(inputStream)

                if (bitmap != null) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, "image.jpg")
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + albumName)
                    }

                    val resolver = context.contentResolver
                    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                    if (uri != null) {
                        val outputStream: OutputStream? = resolver.openOutputStream(uri)
                        if (outputStream != null) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        }
                        outputStream?.close()
                        true
                    } else {
                        false
                    }
                } else {
                    // If decoding as a bitmap fails, try to handle it as an SVG
                    val svg = try {
                        SVG.getFromInputStream(inputStream)
                    } catch (e: SVGParseException) {
                        null
                    }

                    if (svg != null) {
                        // Create a canvas and draw the SVG onto it
                        val picture = Picture()
                        val canvas = picture.beginRecording(1, 1)
                        svg.renderToCanvas(canvas)
                        picture.endRecording()

                        // Convert the Picture to a Bitmap
                        val svgBitmap = Bitmap.createBitmap(picture.width, picture.height, Bitmap.Config.ARGB_8888)
                        val svgCanvas = Canvas(svgBitmap)
                        svgCanvas.drawPicture(picture)

                        // Save the SVG bitmap to the gallery
                        val contentValues = ContentValues().apply {
                            put(MediaStore.Images.Media.DISPLAY_NAME, "image.jpg")
                            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + albumName)
                        }

                        val resolver = context.contentResolver
                        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                        if (uri != null) {
                            val outputStream: OutputStream? = resolver.openOutputStream(uri)
                            if (outputStream != null) {
                                svgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                            }
                            outputStream?.close()
                            true
                        } else {
                            false
                        }
                    } else {
                        // Handle the case where neither decoding as a bitmap nor SVG works
                        false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}

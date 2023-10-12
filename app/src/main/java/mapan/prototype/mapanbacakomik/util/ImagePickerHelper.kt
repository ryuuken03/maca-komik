package mapan.prototype.mapanbacakomik.util

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.ActivityInfo
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.OnScanCompletedListener
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Contacts.People.openContactPhotoInputStream
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.*
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import mapan.prototype.mapanbacakomik.R
import mapan.prototype.mapanbacakomik.config.Constants
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ImagePickerHelper(context: Context, file_location: String, file_name: String) {
    var file: File? = null
    var context: Context
    var file_location: String
    var file_name: String

    companion object {

        /**
         * A lookup uri (e.g. content://com.android.contacts/contacts/lookup/3570i61d948d30808e537)
         */
        var ID_CONTACTS_LOOKUP = 1

        /**
         * A contact thumbnail uri (e.g. content://com.android.contacts/contacts/38/photo)
         */
        var ID_CONTACTS_THUMBNAIL = 2

        /**
         * A contact uri (e.g. content://com.android.contacts/contacts/38)
         */
        var ID_CONTACTS_CONTACT = 3

        /**
         * A contact display photo (high resolution) uri
         * (e.g. content://com.android.contacts/5/display_photo)
         */
        var ID_CONTACTS_PHOTO = 4

        /**
         * Uri for optimized search of phones by number
         * (e.g. content://com.android.contacts/phone_lookup/232323232
         */
        var ID_LOOKUP_BY_PHONE = 5

        fun withContext(
            context: Context,
            file_location: String,
            file_name: String
        ): ImagePickerHelper {
            return ImagePickerHelper(context, file_location, file_name)
        }

        fun convertBase64(bmp: Bitmap): String {
            val imageBytes0: ByteArray
            val baos0 = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos0)
            imageBytes0 = baos0.toByteArray()
            return Base64.encodeToString(imageBytes0, Base64.DEFAULT)
        }

        fun convertBase64WithFormat(bmp: Bitmap, extension: String? = "jpeg"): String {
            val imageBytes0: ByteArray
            val baos0 = ByteArrayOutputStream()
            if(extension.equals("png")){
                bmp.compress(Bitmap.CompressFormat.PNG, 80, baos0)
            }else{
                bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos0)
            }
            imageBytes0 = baos0.toByteArray()
            return Base64.encodeToString(imageBytes0, Base64.DEFAULT)
        }

        fun getCameraPhotoOrientation(context: Context?, imageUri: Uri?, imagePath: String?): Int {
            var rotate = 0
            try {
//            context.getContentResolver().notifyChange(imageUri, null);
                val imageFile = File(imagePath)
                val exif = ExifInterface(imageFile.absolutePath)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
                }
//                Log.i("RotateImage", "Exif orientation: $orientation")
//                Log.i("RotateImage", "Rotate value: $rotate")
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return rotate
        }

        fun getURIMATCHER(): UriMatcher {
            var URI_MATCHER = UriMatcher(UriMatcher.NO_MATCH)
            URI_MATCHER.addURI(
                ContactsContract.AUTHORITY,
                "contacts/lookup/*/#",
                ID_CONTACTS_LOOKUP
            )
            URI_MATCHER.addURI(ContactsContract.AUTHORITY, "contacts/lookup/*", ID_CONTACTS_LOOKUP)
            URI_MATCHER.addURI(
                ContactsContract.AUTHORITY,
                "contacts/#/photo",
                ID_CONTACTS_THUMBNAIL
            )
            URI_MATCHER.addURI(ContactsContract.AUTHORITY, "contacts/#", ID_CONTACTS_CONTACT)
            URI_MATCHER.addURI(
                ContactsContract.AUTHORITY,
                "contacts/#/display_photo",
                ID_CONTACTS_PHOTO
            )
            URI_MATCHER.addURI(ContactsContract.AUTHORITY, "phone_lookup/*", ID_LOOKUP_BY_PHONE)
            return URI_MATCHER
        }

        @Throws(FileNotFoundException::class, RuntimeException::class)
        private fun loadResourceFromUri(uri: Uri, contentResolver: ContentResolver): InputStream? {
            var URI_MATCHER = getURIMATCHER()
            var uri: Uri? = uri

            var inputStream: InputStream? = null
            when (URI_MATCHER.match(uri)) {
                ID_CONTACTS_CONTACT -> inputStream =
                    openContactPhotoInputStream(contentResolver, uri)
                ID_CONTACTS_LOOKUP, ID_LOOKUP_BY_PHONE -> {
                    // If it was a Lookup uri then resolve it first, then continue loading the contact uri.
                    uri = ContactsContract.Contacts.lookupContact(contentResolver, uri)
                    if (uri == null) {
                        throw FileNotFoundException("Contact cannot be found")
                    }
                    inputStream = openContactPhotoInputStream(contentResolver, uri)
                }
                ID_CONTACTS_THUMBNAIL, ID_CONTACTS_PHOTO, UriMatcher.NO_MATCH -> {
                    try {
                        inputStream = contentResolver.openInputStream(uri!!)
                    } catch (e: FileNotFoundException) {
                    } catch (e: java.lang.Exception) {
                    } catch (e: Exception) {
                    }
                }
                else -> inputStream = contentResolver.openInputStream(uri!!)
            }
            return inputStream
        }

        @Throws(FileNotFoundException::class, IOException::class)
        fun resizeImage(context: Context, uri: Uri): Bitmap? {
            var rotation = getCameraPhotoOrientation(context, uri, getRealPathFromURI(context, uri))

            var input: InputStream? = null
            try {
                input = loadResourceFromUri(uri, context.contentResolver)!!
            } catch (e: FileNotFoundException) {
            } catch (e: Exception) {
            }
            val onlyBoundsOptions = BitmapFactory.Options()
            onlyBoundsOptions.inJustDecodeBounds = true
            onlyBoundsOptions.inDither = true //optional
            onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888 //optional
            BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
            input?.close()

            if (onlyBoundsOptions.outWidth == -1 || onlyBoundsOptions.outHeight == -1) {
                return null
            }

            val originalSize =
                if (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) onlyBoundsOptions.outHeight else onlyBoundsOptions.outWidth

            // 1024 => 1/2 origin size
            // 512 => 1/4 origin size
            val ratio = if (originalSize > 700) (originalSize / 700).toDouble() else 1.0
            val bitmapOptions = BitmapFactory.Options()

            bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio)
            bitmapOptions.inDither = true //optional
            bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888 //

//            input = context.getContentResolver().openInputStream(uri)!!

//            input = loadResourceFromUri(uri,context.contentResolver)!!
            try {
                input = loadResourceFromUri(uri, context.contentResolver)!!
            } catch (e: FileNotFoundException) {
            } catch (e: Exception) {
            }
            val bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions)
            input?.close()

            val matrix = Matrix()
            matrix.postRotate(rotation.toFloat())

            if(bitmap != null){
                val rotatedBitmap =
                    Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)
                return rotatedBitmap
            }

            return bitmap
        }

        private fun getPowerOfTwoForSampleRatio(ratio: Double): Int {
            val k = Integer.highestOneBit(Math.floor(ratio).toInt())
            return if (k == 0) 1 else k
        }

        fun getRealPathFromURI(context: Context, contentURI: Uri): String? {
            var result: String? = null
            val cursor: Cursor? =
                context.getContentResolver().query(contentURI, null, null, null, null)
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath()!!
            } else {
                try {
                    cursor.moveToFirst()
                    val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    result = cursor.getString(idx)
                    cursor.close()
                }catch (e: Exception){
                    result = null
                    cursor.close()
                }
            }
            return result
        }

        fun getExtensionFile(context: Context, uri: Uri): String {
            var extension = ""
            var path = getRealPathFromURI(context, uri)
            if(path != null){
                extension = path.substring(path.lastIndexOf(".") + 1)
            }

            return extension
        }

        fun convertBitmapFromBase64(base64: String): Bitmap {
            val imageBytes = Base64.decode(base64, 0)
            val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            return image
        }

        fun galleryAddPic(context: Context, uri: Uri) {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
//            val f = File(currentPhotoPath)
//            val contentUri = Uri.fromFile(f)
//            mediaScanIntent.data = contentUri
            mediaScanIntent.data = uri
            context.sendBroadcast(mediaScanIntent)
        }

        fun generateFileName(): String? {
            val timeStamp: String = SimpleDateFormat("yyyy_MM_dd_HH_mm").format(Date())
            return "Ekosis_$timeStamp.jpg"
        }

        fun saveCompressedBitmap(
            context: Context,
            file_location: String,
            file_name: String,
            bitmap: Bitmap
        ) {
            var folder = File(
                context!!.getExternalFilesDir(null)
                    .toString() + File.separator + Constants.ROOT_DIR + File.separator + file_location
            )

            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    Log.e("DIR", "Failed to create directory");
                }
            } else {
                folder.mkdirs()
            }

            var file = File(folder, file_name)

            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("ErrorSave", " 1 => " + e.message.toString())
            }
            try {
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
                Log.e("ErrorSave", " 2 => ")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ERRORSAVE", " 3 => " + e.message.toString())
            }
        }
    }

    init {
        this.context = context
        this.file_location = file_location
        this.file_name = file_name
    }
}

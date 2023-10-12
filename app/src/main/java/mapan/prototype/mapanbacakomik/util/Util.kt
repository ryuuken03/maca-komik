package mapan.prototype.mapanbacakomik.util

import android.app.Activity
import android.content.*
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.*
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.*
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import mapan.prototype.mapanbacakomik.R
import org.sufficientlysecure.htmltextview.HtmlFormatter
import org.sufficientlysecure.htmltextview.HtmlFormatterBuilder
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter
import java.io.*


class Util {

    companion object {

        fun getDisplay(activity: Activity): DisplayMetrics? {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics
        }

        fun convertPxToDp(dp: Int, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(), context.resources.displayMetrics
            ).toInt()
        }

        fun convertPxToDpNew(px: Int, context: Context): Int {
            return (px / context.resources.displayMetrics.density).toInt()
        }

        fun convertDpToPx(dp: Int, context: Context): Int {
            val d = context.resources.displayMetrics.density
            return (dp * d).toInt()
        }

        fun bitmapToByte(bitmap: Bitmap): ByteArray? {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            return stream.toByteArray()
        }

        fun loadImageOriginalSize(
            context: Context,
            uri: Any,
            view: ImageView,
            signature: String) {
            var width = getDisplay(context as BaseActivity)!!.widthPixels-convertDpToPx(6,context)
//            var width = Target.SIZE_ORIGINAL
            var height = Target.SIZE_ORIGINAL
            loadRoundedImage(context, uri, view, signature, width,height)
        }

        fun loadImageSetWH(
            context: Context,
            uri: Any,
            view: ImageView,
            signature: String,
            width: Int,
            height: Int){
            loadRoundedImage(context, uri, view, signature, width,height)

        }

        fun loadRoundedImage(
            context: Context,
            uri: Any,
            view: ImageView,
            signature: String,
            width: Int,
            height: Int
        ) {
            try {
                val isSignature =
                    if (signature == null) false else if (signature == "") false else true
                val GRequest = GlideApp.with(context)

                var src = uri
                if(src is String){
                    if(src.contains("Â")){
                        src = src.replace("Â","")
                    }
                }
                var glideApp = GRequest.load(src)
                if(width != 0 && height != 0){
                    glideApp.override(width,height)
                }
                glideApp.diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                glideApp.apply(RequestOptions().format(DecodeFormat.PREFER_RGB_565))
                if (isSignature) glideApp.signature(ObjectKey(signature))

//                glideApp.error(R.drawable.ic_holder_image)
                glideApp.error(R.color.grey)
                var sizeMultiplier = 0.5f
                glideApp.thumbnail(sizeMultiplier)
                if(height == Target.SIZE_ORIGINAL){
                    glideApp.into(object : CustomTarget<Drawable>(width, 1) {
                        override fun onLoadCleared(placeholder: Drawable?) {
                            // clear resources
                        }
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            view.updateLayoutParams<ConstraintLayout.LayoutParams> {
                                dimensionRatio = "${resource.intrinsicWidth}:${resource.intrinsicHeight}"
                            }
                            view.setImageDrawable(resource)
                        }
                    })
//                    glideApp.into(view)
                }else{
                    glideApp.into(view)
                }
            } catch (e: Exception) {
            }

        }

        fun getPermissionManifestRequired(
            context: Context,
            permissions: Collection<String>,
            function1: () -> Unit,
            function2: () -> Unit,
            description: String
        ) {

            Dexter.withContext(context)
                .withPermissions(permissions)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(p0: MultiplePermissionsReport) {
                        if (p0.areAllPermissionsGranted()) {
                            function1()
                        } else {
                            if (p0.isAnyPermissionPermanentlyDenied) {
                                function2()
                            } else {
                                Toast.makeText(
                                    context,
                                    description + " " + context.getString(R.string.text_permission_needed)
                                        .toLowerCase(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        permissionToken: PermissionToken
                    ) {
                        permissionToken.continuePermissionRequest()
                    }

                }).withErrorListener(object : PermissionRequestErrorListener {
                    override fun onError(dexterError: DexterError) {
                        Toast.makeText(context, "" + dexterError.name, Toast.LENGTH_SHORT).show()
                    }
                }).check()
        }

        fun isTextEmpty(text: String?): Boolean {
            if (text == null) {
                return true
            } else {
                if (text.trim { it <= ' ' }.length == 0) {
                    return true
                } else {
                    if (text.equals("", ignoreCase = true)) {
                        return true
                    } else {
                        if (text.isEmpty()) {
                            return true
                        } else {
                            if(text.equals("null", true)){
                                return true
                            }
                        }
                    }
                }
            }
            return false
        }
        fun isNumeric(toCheck: String): Boolean {
            val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
            return toCheck.matches(regex)
        }

        fun hideKeyboard(activity: BaseActivity) {
            val imm =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create rounded_bg_10dp_elevation_6dp new one, just so we can grab rounded_bg_10dp_elevation_6dp window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }


        fun loadHtmlView(string: String, textView: TextView): CharSequence? {
            return HtmlFormatter.formatHtml(
                HtmlFormatterBuilder().setHtml(string).setImageGetter(HtmlHttpImageGetter(textView))
            )
        }

        fun convertStringISOtoUTF8(sentence : String) : String{
            val charset = Charsets.UTF_8
            val charset2 = Charsets.ISO_8859_1
            val byteArray = sentence.toByteArray(charset2)
            var result = byteArray.toString(charset)
            return result
        }
    }

}
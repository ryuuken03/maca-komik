package mapan.prototype.mapanbacakomik.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.BaseRequestOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import mapan.prototype.mapanbacakomik.R
import mapan.prototype.mapanbacakomik.activity.ListChapterPageActivity
import mapan.prototype.mapanbacakomik.config.Constants
import mapan.prototype.mapanbacakomik.databinding.ItemAdapterChapterPageBinding
import mapan.prototype.mapanbacakomik.util.BaseActivity
import mapan.prototype.mapanbacakomik.util.GlideApp
import mapan.prototype.mapanbacakomik.util.InitializerUi
import mapan.prototype.mapanbacakomik.util.MyAppGlideModule
import mapan.prototype.mapanbacakomik.util.Util


/***
 * Created By Mohammad Toriq on 16/02/2023
 */
open class AdapterChapterPage(context: Context) : AbstractBindingItem<ItemAdapterChapterPageBinding>(),
    InitializerUi {
    var context: Context = context
    var activity: ListChapterPageActivity?= null
    var id: Long? = null
    var src: String? = null
    var wSrc: String? = null
    var hSrc: String? = null
    var title: String? = null
    var isStar = false

    lateinit var binding: ItemAdapterChapterPageBinding

    /** defines the type defining this item. must be unique. preferably an id */
    override val type: Int
        get() = R.id.item_adapter_chapter_page

    override fun bindView(binding: ItemAdapterChapterPageBinding, payloads: List<Any>) {
        this.binding = binding
        initConfig()
    }

    override fun unbindView(binding: ItemAdapterChapterPageBinding) {
        super.unbindView(binding)
//        binding.progress.visibility = View.GONE
        GlideApp.with(context).clear(binding.photo)
        binding.photo.setImageDrawable(null)
        binding.photo.visibility = View.GONE
        binding.number.visibility = View.GONE
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ItemAdapterChapterPageBinding {
        return ItemAdapterChapterPageBinding.inflate(inflater, parent, false)
    }

    override fun initConfig() {
        initUI()
    }

    override fun initUI() {
        binding.photo.layout(0,0,0,0)
        binding.number.visibility = View.VISIBLE
        binding.number.text = id.toString()
        if(isStar){
            binding.star.visibility = View.VISIBLE
        }else{
            binding.star.visibility = View.GONE
        }
//        Util.loadImageOriginalSize(context, src!!, binding.photo,"")
        load()
        setListener()
    }


    fun load(){
        onConnecting()
        //set Listener & start
//        onLoad1()
        onLoad2()
        MyAppGlideModule.expect(src!!, object : MyAppGlideModule.UIonProgressListener {
            override fun onProgress(bytesRead: Long, expectedLength: Long) {
//                if (binding.progress != null) {
                var progress = (100 * bytesRead / expectedLength).toInt()
                binding.progress.setProgress(progress)
//                }
            }

            override val granualityPercentage: Float
                get() = 1.0f
        })

    }

    fun onLoad1(){

        try {

            val GRequest = GlideApp.with(context)

            if(src is String){
                if(src!!.contains("Â")){
                    src = src!!.replace("Â","")
                }
            }
            var glideApp = GRequest.load(src)
            var width = Util.getDisplay(context as BaseActivity)!!.widthPixels- Util.convertDpToPx(
                6,
                context
            )
//            var width = Target.SIZE_ORIGINAL
            var height = Target.SIZE_ORIGINAL
            if(width != 0 && height != 0){
                glideApp.override(width,height)
            }
            glideApp.diskCacheStrategy(DiskCacheStrategy.ALL)
//                .downsample(DownsampleStrategy.CENTER_INSIDE)
                .priority(Priority.HIGH)
                .skipMemoryCache(true)
            glideApp.apply(RequestOptions().format(DecodeFormat.PREFER_RGB_565))

            glideApp.placeholder(R.color.grey)
            glideApp.error(R.color.grey)
            glideApp.fallback(R.color.grey)
            var sizeMultiplier = 0.5f
            glideApp.thumbnail(sizeMultiplier)
            if(height == Target.SIZE_ORIGINAL){
                glideApp.into(object : CustomTarget<Drawable>(width, 1) {
                    override fun onLoadFailed(errorDrawable: Drawable?) {
//                        super.onLoadFailed(errorDrawable)
//                        MyAppGlideModule.forget(src!!)
                        binding.photo.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            dimensionRatio = "${errorDrawable?.intrinsicWidth}:${errorDrawable?.intrinsicHeight}"
                        }
                        binding.photo.setImageResource(R.color.grey)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
//                        MyAppGlideModule.forget(src!!)
                        binding.photo.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            dimensionRatio = "${placeholder?.intrinsicWidth}:${placeholder?.intrinsicHeight}"
                        }
                        onFinished()
                    }
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
//                        MyAppGlideModule.forget(src!!)
                        onFinished()
                        activity?.loadAllData(src!!)
                        binding.photo.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            dimensionRatio = "${resource.intrinsicWidth}:${resource.intrinsicHeight}"
                        }
                        binding.photo.setImageDrawable(resource)
                    }
                })
            }else{
                glideApp.into(binding.photo)
            }
        } catch (e: Exception) {
        }
    }

    fun onLoad2(){
//        (context as BaseActivity).getWindowManager().getDefaultDisplay()
//            .getMetrics(DisplayMetrics())

//        try {
            val GRequest = GlideApp.with(context).asBitmap()

            var glideApp = GRequest.load(src)

            var reqOptions = RequestOptions() as BaseRequestOptions<*>
            reqOptions.dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .timeout(Constants.IMG_READ_TIMEOUT.toInt())
                .override(Integer.MIN_VALUE)
                .format(DecodeFormat.PREFER_RGB_565)
            glideApp.apply(reqOptions)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.color.grey)
                .dontAnimate()
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(e: GlideException?,
                                              model: Any?,
                                              target: Target<Bitmap>?,
                                              isFirstResource: Boolean
                    ): Boolean {

                        var weakHandler = Handler()
                        weakHandler.postDelayed(Runnable {
                            if(!activity!!.isDestroyed){
                                onLoad2()
                            }
                        },1000)
                        return true
                    }

                    override fun onResourceReady(resource: Bitmap?,
                                                 model: Any?,
                                                 target: Target<Bitmap>?,
                                                 dataSource: DataSource?,
                                                 isFirstResource: Boolean
                    ): Boolean {
                        onFinished()
                        activity?.loadAllData(src!!)
                        return false
                    }
                }).into(binding.photo)
//            glideApp.apply ()

//        }catch (e:Exception){
//        }
    }

    fun onConnecting() {
        binding.progress.visibility = View.VISIBLE
    }

    open fun onFinished() {
        binding.progress.visibility = View.GONE
        binding.photo.visibility = View.VISIBLE
    }

    override fun setListener() {
    }

}
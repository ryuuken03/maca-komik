package mapan.prototype.mapanbacakomik.activity

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.BaseRequestOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import mapan.prototype.mapanbacakomik.R
import mapan.prototype.mapanbacakomik.config.Constants
import mapan.prototype.mapanbacakomik.databinding.ActivityPreviewImageBinding
import mapan.prototype.mapanbacakomik.util.BaseActivity
import mapan.prototype.mapanbacakomik.util.GlideApp
import mapan.prototype.mapanbacakomik.util.Util
import uk.co.senab.photoview.PhotoViewAttacher


class PreviewImageActivity : BaseActivity() {
    lateinit var binding: ActivityPreviewImageBinding
    var imgSrc : String?= null
    var isFirst = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewImageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onResume() {
        super.onResume()
        if(isFirst){
            initConfig()
        }else{
            reload()
        }
    }

    fun reload(){
        binding.photo.visibility = View.GONE
        Handler().postDelayed(Runnable {
            binding.photo.visibility = View.VISIBLE
        },100)
    }

    override fun initConfig() {
        imgSrc = intent.getStringExtra("imgSrc")
        if(imgSrc == null){
            finish()
        }else{
            initUI()
        }
    }

    override fun initUI() {
        binding.toolbar.title.text = "Page "+intent.getStringExtra("title")
        binding.photo.layout(0,0,0,0)
        var display = Util.getDisplay(this)!!
//        var param = binding.photo.layoutParams
//        param.width = width
//        loadData()
        isFirst = false
        loadData2()
        setListener()
    }

    fun loadData(){
//        Util.loadImageOriginalSize(this, imgSrc!!, binding.photo,"")
        Glide.with(this)
            .asBitmap() // could be an issue!
            .override(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .apply(RequestOptions().format(DecodeFormat.PREFER_RGB_565))
            .load(imgSrc!!)
            .into(object : SimpleTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    binding.photo.setImageBitmap(resource)
                }
            })
    }

    fun loadData2(){
//        val GRequest = GlideApp.with(this).asBitmap()
        val GRequest = GlideApp.with(this)

        var glideApp = GRequest.load(imgSrc)

        var reqOptions = RequestOptions() as BaseRequestOptions<*>
        reqOptions.dontAnimate()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .timeout(Constants.IMG_READ_TIMEOUT.toInt())
//            .override(Integer.MIN_VALUE)
            .format(DecodeFormat.PREFER_RGB_565)
        var widthD = Util.getDisplay(this)!!.widthPixels
        var heightD = Util.getDisplay(this)!!.heightPixels
        glideApp.apply(reqOptions)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.color.grey)
            .dontAnimate()
            .into(object : CustomTarget<Drawable>(widthD, 1) {
                override fun onLoadFailed(errorDrawable: Drawable?
                ){

                    var weakHandler = Handler()
                    weakHandler.postDelayed(Runnable {
                        if(!isDestroyed){
                            loadData2()
                        }
                    },1000)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?){
                    binding.photo.updateLayoutParams<LinearLayout.LayoutParams> {
                        if(resource.intrinsicHeight > heightD){
                            heightD = resource.intrinsicHeight
                        }else{
                            var params = binding.layout.layoutParams as FrameLayout.LayoutParams
                            params.gravity = Gravity.CENTER
                            binding.layout.layoutParams = params
                        }
                    }
                    binding.photo.setImageDrawable(resource)
                    binding.photo.visibility = View.VISIBLE
                    var pAttacher = PhotoViewAttacher(binding.photo)
                    pAttacher.update()
                }
            })
//            .into(binding.photo)
    }

    override fun setListener() {
        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}
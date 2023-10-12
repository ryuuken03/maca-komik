package mapan.prototype.mapanbacakomik.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import mapan.prototype.mapanbacakomik.R
import mapan.prototype.mapanbacakomik.databinding.ItemAdapterComicThumbnailBinding
import mapan.prototype.mapanbacakomik.util.*

/***
 * Created By Mohammad Toriq on 16/02/2023
 */
open class AdapterComicThumbnail(context: Context) : AbstractBindingItem<ItemAdapterComicThumbnailBinding>(),
    InitializerUi {
    var context: Context = context
    var id: Long? = null
    var link: String? = null
    var src: String? = null
    var title: String? = null
    var typeComic: String? = null
    var typeSource: Int? = null
    var lastChapter: String? = null
    var urlLastChapter: String? = null

    lateinit var binding: ItemAdapterComicThumbnailBinding

    /** defines the type defining this item. must be unique. preferably an id */
    override val type: Int
        get() = R.id.item_adapter_comic_thumbnail

    override fun bindView(binding: ItemAdapterComicThumbnailBinding, payloads: List<Any>) {
        this.binding = binding
        initConfig()
        initUI()
        setListener()
    }

    override fun unbindView(binding: ItemAdapterComicThumbnailBinding) {
        super.unbindView(binding)
        GlideApp.with(context).clear(binding.photo)
        binding.photo.visibility = View.GONE
        binding.title.visibility = View.GONE
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ItemAdapterComicThumbnailBinding {
        return ItemAdapterComicThumbnailBinding.inflate(inflater, parent, false)
    }

    override fun initConfig() {
    }

    override fun initUI() {
        binding.photo.visibility = View.VISIBLE
        binding.title.visibility = View.VISIBLE
        var width = Util.getDisplay(context as BaseActivity)!!.widthPixels
        var divW = 4.0
        var divh = 3.0
//        if(grid == 3){
            divW = 3.0
            divh = 2.5
//        }else if(grid == 4){
//            divW = 4.0
//            divh = 3.0
//        }
        var wResult : Double = width.toDouble()/divW
        var hResult : Double = width.toDouble()/divh

        var widthR = (Math.round(wResult)).toInt()
        var heightR = (Math.round(hResult)).toInt()
        var param = binding.photo.layoutParams as RelativeLayout.LayoutParams
        param.width = widthR
        param.height = heightR
        var source = src!!
        binding.photo.setScaleType(ImageView.ScaleType.CENTER)
        if(src.equals("")){
            binding.photo.setScaleType(ImageView.ScaleType.CENTER_INSIDE)
            var imageNotFound = context.resources.getStringArray(R.array.source_website_image_not_found)
            source = imageNotFound[typeSource!!]

        }
        var isShowType = true
        if(typeComic!=null){
            if(typeComic.equals("")){
                isShowType = false
            }
        }else{
            isShowType = false
        }

        if(isShowType){
            binding.type.visibility = View.VISIBLE
            binding.type.text = typeComic
        }else{
            binding.type.visibility = View.GONE
        }
        Util.loadImageSetWH(context, source, binding.photo,"",widthR,heightR)
        binding.title.text = Util.convertStringISOtoUTF8(title!!)
        var paramTitle = binding.title.layoutParams as RelativeLayout.LayoutParams
        paramTitle.width = widthR
        
        if(lastChapter == null){
            lastChapter = "-"
        }else{
            if(Util.isTextEmpty(lastChapter)){
                lastChapter = "-"
            }
        }
        binding.lastChapter.text = Util.loadHtmlView(lastChapter!!,binding.lastChapter)
    }

    override fun setListener() {
    }

}
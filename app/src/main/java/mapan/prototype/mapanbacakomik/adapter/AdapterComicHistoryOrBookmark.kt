package mapan.prototype.mapanbacakomik.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import mapan.prototype.mapanbacakomik.R
import mapan.prototype.mapanbacakomik.databinding.ItemAdapterComicHistoryOrBookmarkBinding
import mapan.prototype.mapanbacakomik.util.*

/***
 * Created By Mohammad Toriq on 16/02/2023
 */
open class AdapterComicHistoryOrBookmark(context: Context) : AbstractBindingItem<ItemAdapterComicHistoryOrBookmarkBinding>(),
    InitializerUi {
    var context: Context = context
//    var activity : ListComicActivity?= null
    var id: Long? = null
    var number: Long? = null
    var urlDetailComic: String? = null
    var imgSrc: String? = null
    var title: String? = null
    var genre: String? = null
    var typeComic: String? = null
    var source: String? = null
    var lastChapter: String? = null
    var urlLastChapter: String? = null
    var pageChapter = 0
    var isBookmark = false

    lateinit var binding: ItemAdapterComicHistoryOrBookmarkBinding

    /** defines the type defining this item. must be unique. preferably an id */
    override val type: Int
        get() = R.id.item_adapter_comic_history_or_bookmark

    override fun bindView(binding: ItemAdapterComicHistoryOrBookmarkBinding, payloads: List<Any>) {
        this.binding = binding
        initConfig()
        initUI()
        setListener()
    }

    override fun unbindView(binding: ItemAdapterComicHistoryOrBookmarkBinding) {
        super.unbindView(binding)
        GlideApp.with(context).clear(binding.photo)
        binding.photo.visibility = View.GONE
        binding.number.visibility = View.GONE
        binding.title.visibility = View.GONE
        binding.genre.visibility = View.GONE
        binding.type.visibility = View.GONE
        binding.delete.visibility = View.GONE
        binding.layoutChapter.visibility = View.GONE
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ItemAdapterComicHistoryOrBookmarkBinding {
        return ItemAdapterComicHistoryOrBookmarkBinding.inflate(inflater, parent, false)
    }

    override fun initConfig() {
    }

    override fun initUI() {
        binding.photo.visibility = View.VISIBLE
        binding.title.visibility = View.VISIBLE
        binding.genre.visibility = View.VISIBLE
        binding.type.visibility = View.VISIBLE
        binding.layoutChapter.visibility = View.VISIBLE
//        binding.title.text = title
        binding.title.text = Util.convertStringISOtoUTF8(title!!)
        binding.genre.text = genre
        binding.type.text = typeComic
        if(lastChapter == null){
            lastChapter = "Chapter All"
        }else{
            if(Util.isTextEmpty(lastChapter)){
                lastChapter = "Chapter All"
            }else if(lastChapter.equals("-")){
                lastChapter = "Chapter All"
            }else if(lastChapter!!.contains("Indonesia",true)){
                lastChapter = lastChapter!!.replace("Bahasa Indonesia","",true)
            }
        }
        binding.chapter.text = Util.loadHtmlView(lastChapter!!,binding.chapter)
        binding.source.text = source
        binding.number.visibility = View.VISIBLE
        binding.number.text = number.toString()
        var scaleType = ImageView.ScaleType.CENTER
        var imageNotFound = context.resources.getStringArray(R.array.source_website_image_not_found)
        when(imgSrc){
            imageNotFound[0]->{
                scaleType = ImageView.ScaleType.CENTER_INSIDE
            }
            imageNotFound[1]->{
                scaleType = ImageView.ScaleType.CENTER_INSIDE
            }
            imageNotFound[2]->{
                scaleType = ImageView.ScaleType.CENTER_INSIDE
            }
            imageNotFound[3]->{
                scaleType = ImageView.ScaleType.CENTER_INSIDE
            }
            ""->{
                var titles = context.resources.getStringArray(R.array.source_website_title)
                for(i in 0 .. titles.size-1){
                    if(titles[i].equals(source,true)){
                        imgSrc = imageNotFound[i]
                        break
                    }
                }
                scaleType = ImageView.ScaleType.CENTER_INSIDE
            }
        }
        binding.layout.post {
            var param = binding.photo.layoutParams
            var height = binding.layout.height
            param.height = height

            binding.photo.setScaleType(scaleType)
            Util.loadImageSetWH(context, imgSrc!!, binding.photo,"",0,0)
        }
        if(isBookmark){
            binding.delete.visibility = View.VISIBLE
        }else{
            binding.delete.visibility = View.GONE
        }
    }

    override fun setListener() {

    }

}
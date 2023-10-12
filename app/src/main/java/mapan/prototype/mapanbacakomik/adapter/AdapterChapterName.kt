package mapan.prototype.mapanbacakomik.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import mapan.prototype.mapanbacakomik.R
import mapan.prototype.mapanbacakomik.databinding.ItemAdapterChapterNameBinding
import mapan.prototype.mapanbacakomik.databinding.ItemAdapterChapterPageBinding
import mapan.prototype.mapanbacakomik.databinding.ItemAdapterComicThumbnailBinding
import mapan.prototype.mapanbacakomik.util.BaseActivity
import mapan.prototype.mapanbacakomik.util.GlideApp
import mapan.prototype.mapanbacakomik.util.InitializerUi
import mapan.prototype.mapanbacakomik.util.Util

/***
 * Created By Mohammad Toriq on 16/02/2023
 */
open class AdapterChapterName(context: Context) : AbstractBindingItem<ItemAdapterChapterNameBinding>(),
    InitializerUi {
    var context: Context = context
    var id: Long? = null
    var name: String? = null
    var time: String? = null
    var url: String? = null

    lateinit var binding: ItemAdapterChapterNameBinding

    /** defines the type defining this item. must be unique. preferably an id */
    override val type: Int
        get() = R.id.item_adapter_chapter_name

    override fun bindView(binding: ItemAdapterChapterNameBinding, payloads: List<Any>) {
        this.binding = binding
        initConfig()
        initUI()
        setListener()
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ItemAdapterChapterNameBinding {
        return ItemAdapterChapterNameBinding.inflate(inflater, parent, false)
    }

    override fun initConfig() {
    }

    override fun initUI() {
        binding.name.text = name
        binding.time.text = time
    }

    override fun setListener() {
    }

}
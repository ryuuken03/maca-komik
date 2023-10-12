package mapan.prototype.mapanbacakomik.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import mapan.prototype.mapanbacakomik.R
import mapan.prototype.mapanbacakomik.databinding.ItemAdapterGenreBinding
import mapan.prototype.mapanbacakomik.util.InitializerUi

/***
 * Created By Mohammad Toriq on 16/02/2023
 */
open class AdapterGenre(context: Context) : AbstractBindingItem<ItemAdapterGenreBinding>(),
    InitializerUi {
    var context: Context = context
//    var activity: GenreActivity? = null
    var id: Long? = null
    var name: String? = null
    var value: String? = null
    var isTouch = false

    lateinit var binding: ItemAdapterGenreBinding

    /** defines the type defining this item. must be unique. preferably an id */
    override val type: Int
        get() = R.id.item_adapter_genre

    override fun bindView(binding: ItemAdapterGenreBinding, payloads: List<Any>) {
        this.binding = binding
        initConfig()
        initUI()
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ItemAdapterGenreBinding {
        return ItemAdapterGenreBinding.inflate(inflater, parent, false)
    }

    override fun initConfig() {
    }

    override fun initUI() {
        binding.genre.text = name
        setListener()
    }

    override fun setListener() {
    }

}
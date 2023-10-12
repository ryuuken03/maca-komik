package mapan.prototype.mapanbacakomik.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import mapan.prototype.mapanbacakomik.R
import mapan.prototype.mapanbacakomik.databinding.ItemAdapterFilterComicBinding
import mapan.prototype.mapanbacakomik.util.InitializerUi

/***
 * Created By Mohammad Toriq on 16/02/2023
 */
open class AdapterFilterComic(context: Context) : AbstractBindingItem<ItemAdapterFilterComicBinding>(),
    InitializerUi {
    var context: Context = context
    var id: Long? = null
    var name: String? = null
    var value: String? = null
    var isCheck = false

    lateinit var binding: ItemAdapterFilterComicBinding

    /** defines the type defining this item. must be unique. preferably an id */
    override val type: Int
        get() = R.id.item_adapter_filter_comic

    override fun bindView(binding: ItemAdapterFilterComicBinding, payloads: List<Any>) {
        this.binding = binding
        initConfig()
        initUI()
        setListener()
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ItemAdapterFilterComicBinding {
        return ItemAdapterFilterComicBinding.inflate(inflater, parent, false)
    }

    override fun initConfig() {
    }

    override fun initUI() {
        binding.checkbox.text = name
        binding.checkbox.isChecked = isCheck
    }

    fun setCheckBox(isCheck:Boolean){
        this.isCheck = isCheck
        binding.checkbox.isChecked = this.isCheck
    }

    override fun setListener() {
    }

}
package mapan.prototype.mapanbacakomik.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import mapan.prototype.mapanbacakomik.R
import mapan.prototype.mapanbacakomik.databinding.ItemAdapterSpinnerCustomBinding
import mapan.prototype.mapanbacakomik.util.InitializerUi
import mapan.prototype.mapanbacakomik.util.Util

open class AdapterSpinnerCustom(context: Context) : AbstractBindingItem<ItemAdapterSpinnerCustomBinding>(),
    InitializerUi {
    var context: Context = context;
    var id: Int? = null
    var idString: String? = null
    var title: String? = null
    var desc: String? = null
    var isCheck = false
    lateinit var binding: ItemAdapterSpinnerCustomBinding

    /** defines the type defining this item. must be unique. preferably an id */
    override val type: Int
        get() = R.id.item_adapter_spinner_custom

    override fun bindView(binding: ItemAdapterSpinnerCustomBinding, payloads: List<Any>) {
        this.binding = binding
        initConfig()
        initUI()
        setListener()
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ItemAdapterSpinnerCustomBinding {
        return ItemAdapterSpinnerCustomBinding.inflate(inflater, parent, false)
    }

    override fun initConfig() {
    }

    override fun initUI() {
        if(isCheck){
            binding.iconCheck.visibility = View.VISIBLE
        }else{
            binding.iconCheck.visibility = View.GONE
        }

        if(Util.isTextEmpty(title)){
            binding.title.text = "Nama =  null"
        }else{
            binding.title.text = title
        }
    }

    override fun setListener() {
    }

}
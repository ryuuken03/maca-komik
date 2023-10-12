package mapan.prototype.mapanbacakomik.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import mapan.prototype.mapanbacakomik.R
import mapan.prototype.mapanbacakomik.activity.ChangeSourceWebsiteActivity
import mapan.prototype.mapanbacakomik.databinding.ItemAdapterSourceWebsiteBinding
import mapan.prototype.mapanbacakomik.util.InitializerUi

/***
 * Created By Mohammad Toriq on 16/02/2023
 */
open class AdapterSourceWebsite(context: Context) : AbstractBindingItem<ItemAdapterSourceWebsiteBinding>(),
    InitializerUi {
    var context: Context = context
    var activity: ChangeSourceWebsiteActivity?= null
    var id: Long? = null
    var name: String? = null
    var value: String? = null
    var isCheck = false

    lateinit var binding: ItemAdapterSourceWebsiteBinding

    /** defines the type defining this item. must be unique. preferably an id */
    override val type: Int
        get() = R.id.item_adapter_source_website

    override fun bindView(binding: ItemAdapterSourceWebsiteBinding, payloads: List<Any>) {
        this.binding = binding
        initConfig()
        initUI()
        setListener()
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ItemAdapterSourceWebsiteBinding {
        return ItemAdapterSourceWebsiteBinding.inflate(inflater, parent, false)
    }

    override fun initConfig() {
    }

    override fun initUI() {
        binding.radio.text = name
        binding.radio.isChecked = isCheck
        var index = id!!-1

        when(index.toInt()){
            0->{
                binding.iconSource.visibility = View.VISIBLE
                binding.iconSource.setImageResource(R.drawable.ic_src_komikcast)
            }
            1->{
                binding.iconSource.visibility = View.VISIBLE
                binding.iconSource.setImageResource(R.drawable.ic_src_westmanga)
            }
            2->{
                binding.iconSource.visibility = View.VISIBLE
                binding.iconSource.setImageResource(R.drawable.ic_src_ngomik)
            }
            3->{
                binding.iconSource.visibility = View.VISIBLE
                binding.iconSource.setImageResource(R.drawable.ic_src_shinigami)
            }
            else->{
                binding.iconSource.visibility = View.GONE
            }
        }
    }

    fun setCheckBox(isCheck:Boolean){
        this.isCheck = isCheck
        binding.radio.isChecked = this.isCheck
    }

    override fun setListener() {
    }

}
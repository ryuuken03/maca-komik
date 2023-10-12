package mapan.prototype.mapanbacakomik.activity

import android.app.Activity
import android.content.Intent
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.os.postDelayed
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import mapan.prototype.mapanbacakomik.R
import mapan.prototype.mapanbacakomik.adapter.AdapterComicThumbnail
import mapan.prototype.mapanbacakomik.model.ComicThumbnail
import mapan.prototype.mapanbacakomik.util.BaseActivity
import mapan.prototype.mapanbacakomik.adapter.AdapterFilterComic
import mapan.prototype.mapanbacakomik.adapter.AdapterSourceWebsite
import mapan.prototype.mapanbacakomik.databinding.ActivityChangeSourceWebsiteBinding
import mapan.prototype.mapanbacakomik.model.FilterComic
import mapan.prototype.mapanbacakomik.util.Log
import mapan.prototype.mapanbacakomik.util.Util
import mapan.prototype.mapanbacakomik.util.shared.SourceWebsitePreference
import java.util.*
import kotlin.collections.ArrayList


class ChangeSourceWebsiteActivity : BaseActivity() {
    lateinit var binding: ActivityChangeSourceWebsiteBinding
    lateinit var adapter: ItemAdapter<AdapterSourceWebsite>
    lateinit var fastAdapter: FastAdapter<AdapterSourceWebsite>

    var isFirst = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeSourceWebsiteBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onResume() {
        super.onResume()
        binding.revListData.visibility = View.GONE
        Handler().postDelayed(Runnable {
            binding.revListData.visibility = View.VISIBLE
        },100)
        if(isFirst){
            isFirst = false
            initConfig()
        }
    }

    override fun initConfig() {
        adapter = ItemAdapter()
        fastAdapter = FastAdapter.with(adapter)

        initUI()
    }

    override fun initUI() {
        binding.toolbar.title.text = "Sumber Website"
        binding.revListData.layoutManager = LinearLayoutManager(this)
        binding.revListData.adapter = fastAdapter
        binding.revListData.animation = null
        binding.revListData.isNestedScrollingEnabled = false
        binding.toolbar.btnSave.visibility = View.GONE
        loadData()
        setListener()
    }

    override fun setListener() {

        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }
//        binding.toolbar.btnSave.setOnClickListener {
//            var selectSource = 0
//            for(item in adapter.adapterItems){
//                if(item.isCheck){
//                    selectSource = (item.id!! - 1.toLong()).toInt()
//                    break
//                }
//            }
//
//            SourceWebsitePreference(this).updateSource(selectSource)
//            finish()
//        }
        fastAdapter.onClickListener = { view, adapter, item, position ->
            var selectSource = 0
            for(itemOther in adapter.adapterItems){
                if(item.id != itemOther.id){
                    itemOther.setCheckBox(false)
                }else{
                    item.setCheckBox(true)
                    selectSource = (item.id!! - 1.toLong()).toInt()
                }
            }
            SourceWebsitePreference(this).updateSource(selectSource)
            finish()
            false
        }
    }

    fun loadData(){
        var sourceTitles = resources.getStringArray(R.array.source_website_title)
        var sourceUrls = resources.getStringArray(R.array.source_website_url)
        var typeSource = SourceWebsitePreference(this).getSource()
        var i = 0
        for(filter in sourceTitles){
            var data = AdapterSourceWebsite(this)
            if(i == typeSource){
                data.isCheck = true
            }
            data.activity = this
            data.id = (i+1).toLong()
            data.name = sourceTitles[i]
            data.value = sourceUrls[i]
            adapter.add(data)
            i++
        }

    }

}
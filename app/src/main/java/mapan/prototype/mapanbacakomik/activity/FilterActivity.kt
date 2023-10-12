package mapan.prototype.mapanbacakomik.activity

import android.app.Activity
import android.content.Intent
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.os.postDelayed
import androidx.recyclerview.widget.GridLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import mapan.prototype.mapanbacakomik.R
import mapan.prototype.mapanbacakomik.adapter.AdapterComicThumbnail
import mapan.prototype.mapanbacakomik.model.ComicThumbnail
import mapan.prototype.mapanbacakomik.util.BaseActivity
import mapan.prototype.mapanbacakomik.adapter.AdapterFilterComic
import mapan.prototype.mapanbacakomik.databinding.ActivityFilterBinding
import mapan.prototype.mapanbacakomik.model.FilterComic
import mapan.prototype.mapanbacakomik.util.Log
import mapan.prototype.mapanbacakomik.util.Util
import java.util.*
import kotlin.collections.ArrayList


class FilterActivity : BaseActivity() {
    lateinit var binding: ActivityFilterBinding
    lateinit var adapterGenre: ItemAdapter<AdapterFilterComic>
    lateinit var fastAdapterGenre: FastAdapter<AdapterFilterComic>

    var listGenre = ArrayList<FilterComic>()
    var listType = ArrayList<FilterComic>()
    var listSort = ArrayList<FilterComic>()
    var genreSelected = ArrayList<Long>()
    var typeSelected = ArrayList<Long>()
    var sortSelected = ArrayList<Long>()
    var showGenre = false
    var isFirst = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initConfig()
    }

    override fun onResume() {
        super.onResume()
        if(!isFirst){
            binding.revListGenre.visibility = View.GONE
            Handler().postDelayed(Runnable {
                if(showGenre){
                    binding.revListGenre.visibility = View.VISIBLE
                }
            },100)
        }
    }

    override fun initConfig() {
//        if(intent.getSerializableExtra("listSelected") as ArrayList<Long> !=null){
//            listSelected = intent.getSerializableExtra("listSelected") as ArrayList<Long>
//        }
        if(intent.getSerializableExtra("listGenre") as ArrayList<FilterComic> !=null){
            listGenre = intent.getSerializableExtra("listGenre") as ArrayList<FilterComic>
        }
        if(intent.getSerializableExtra("listType") as ArrayList<FilterComic> !=null){
            listType = intent.getSerializableExtra("listType") as ArrayList<FilterComic>
        }
        if(intent.getSerializableExtra("listSort") as ArrayList<FilterComic> !=null){
            listSort = intent.getSerializableExtra("listSort") as ArrayList<FilterComic>
        }
        if(intent.getSerializableExtra("genreSelected") as ArrayList<Long> !=null){
            genreSelected = intent.getSerializableExtra("genreSelected") as ArrayList<Long>
        }
        if(intent.getSerializableExtra("typeSelected") as ArrayList<Long> !=null){
            typeSelected = intent.getSerializableExtra("typeSelected") as ArrayList<Long>
        }
        if(intent.getSerializableExtra("sortSelected") as ArrayList<Long> !=null){
            sortSelected = intent.getSerializableExtra("sortSelected") as ArrayList<Long>
        }
//            type = intent.getIntExtra("type",-1)
            adapterGenre = ItemAdapter()
            fastAdapterGenre = FastAdapter.with(adapterGenre)

            initUI()
//        }else{
//            finish()
//        }
    }

    override fun initUI() {
        if(isFirst){
            isFirst = false
        }
        binding.toolbar.title.text = "Filter"
        binding.revListGenre.layoutManager = GridLayoutManager(this, 3)
        binding.revListGenre.adapter = fastAdapterGenre
        binding.revListGenre.animation = null
        binding.revListGenre.isNestedScrollingEnabled = false
        loadData()
        setListener()
    }

    override fun setListener() {

        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.toolbar.btnSave.setOnClickListener {
            var intent = Intent()
            genreSelected.clear()
            for(item in adapterGenre.adapterItems){
                if(item.isCheck){
                    genreSelected.add(item.id!!)
                }
            }

            typeSelected.clear()
            var posType = binding.inputType.selectedItemPosition
            typeSelected.add(listType[posType].id!!)
            var posSort = binding.inputSort.selectedItemPosition
            sortSelected.add(listSort[posSort].id!!)
//            if(type > 0 && listSelected.size == 0){
//                listSelected.add(adapter.adapterItems[0].id!!)
//            }
            intent.putExtra("genreSelected",genreSelected)
            intent.putExtra("typeSelected",typeSelected)
            intent.putExtra("sortSelected",sortSelected)
            intent.putExtra("isFinish",true)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        binding.inputType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
//                typeSelected.clear()
//                typeSelected.add(listType[position].id!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.inputSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
//                sortSelected.clear()
//                sortSelected.add(listSort[position].id!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.labelGenre.setOnClickListener {
            if(showGenre){
                binding.revListGenre.visibility = View.GONE
                binding.iconDown3.visibility = View.VISIBLE
                binding.iconUp3.visibility = View.GONE
                showGenre = false
            }else{
                binding.revListGenre.visibility = View.VISIBLE
                binding.iconDown3.visibility = View.INVISIBLE
                binding.iconUp3.visibility = View.VISIBLE
                showGenre = true
            }
        }
        binding.textGenre.setOnClickListener {
            if(showGenre){
                binding.revListGenre.visibility = View.GONE
                binding.iconDown3.visibility = View.VISIBLE
                binding.iconUp3.visibility = View.GONE
                showGenre = false
            }else{
                binding.revListGenre.visibility = View.VISIBLE
                binding.iconDown3.visibility = View.INVISIBLE
                binding.iconUp3.visibility = View.VISIBLE
                showGenre = true
            }
        }
        fastAdapterGenre.onClickListener = { view, adapter, item, position ->
            var check = !item.isCheck
            item.setCheckBox(check)

            setGenreLabel()
            false
        }
    }

    fun setGenreLabel(){
        var genreLabel = ""
        var count = 0
        var other = 0
        for(item in adapterGenre.adapterItems){
            if(item.isCheck){
                if(count < 2){
                    if(!genreLabel.equals("")){
                        genreLabel +=", "
                    }
                    genreLabel += item.name!!
                    count++
                }else{
                    other++
                }
            }
        }
        if(other > 0){
            genreLabel += ", +"+other+" lainnya"
        }
        if(!genreLabel.equals("")){
            binding.textGenre.setText(genreLabel)
        }else{
            binding.textGenre.setText("Pilih Genre")
        }
    }

    fun loadData(){
        for(filter in listGenre){
            var data = AdapterFilterComic(this)
            for(selected in genreSelected){
                if(selected ==  filter.id){
                    data.isCheck = true
                    break
                }
            }
            data.id = filter.id
            data.name = filter.name
            data.value = filter.value
            adapterGenre.add(data)
        }
        setGenreLabel()
        var adapterType = ArrayAdapter<String>(this,R.layout.item_adapter_spinner)
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        for(type in listType){
            var name = type.name!!.substring(0,1)+type.name!!.substring(1)
            if(name.equals("All",true)){
                name = "Semua Jenis"
            }
            adapterType.add(name)
        }
        binding.inputType.adapter = adapterType
        if(typeSelected.size > 0){
            for(i in 0 .. listType.size-1){
                if(typeSelected[0] == listType[i].id){
                    binding.inputType.setSelection(i)
                    break
                }
            }
        }else{
            binding.inputType.setSelection(0)
        }

        var adapterSort = ArrayAdapter<String>(this,R.layout.item_adapter_spinner)
        adapterSort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        for(type in listSort){
            var name = type.name!!.substring(0,1)+type.name!!.substring(1)
            if(name.equals("Update",true)){
                name = "Terbaru"
            }else if(name.equals("Popular",true)){
                name = "Terpopuler"
            }else if(name.equals("Added",true)){
                name = "Baru Ditambahkan"
            }
            adapterSort.add(name)
        }
        binding.inputSort.adapter = adapterSort
        Log.d("OkSetSort","sortSelected.size:"+sortSelected.size.toString())
        if(sortSelected.size > 0){
            Log.d("OkSetSort","sortSelected[0]:"+sortSelected[0].toString())
            for(i in 0 .. listSort.size-1){
                if(sortSelected[0] == listSort[i].id){
                    binding.inputSort.setSelection(i)
                    break
                }
            }
        }else{
            binding.inputSort.setSelection(2)
        }
    }
}
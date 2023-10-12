package mapan.prototype.mapanbacakomik.activity

import android.content.Intent
import android.os.*
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import mapan.prototype.mapanbacakomik.AsyncTask.ListComicAsyncTask
import mapan.prototype.mapanbacakomik.R
import mapan.prototype.mapanbacakomik.util.BaseActivity
import mapan.prototype.mapanbacakomik.adapter.AdapterGenre
import mapan.prototype.mapanbacakomik.databinding.ActivityGenreBinding
import mapan.prototype.mapanbacakomik.model.FilterComic
import kotlin.collections.ArrayList


class GenreActivity : BaseActivity() {
    lateinit var binding: ActivityGenreBinding
    lateinit var adapter: ItemAdapter<AdapterGenre>
    lateinit var fastAdapter: FastAdapter<AdapterGenre>

    var listGenre = ArrayList<FilterComic>()
    var isFirst = true
    var sourceWeb = "https://komikcast.vip/"
    var typeSource = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenreBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onResume() {
        super.onResume()
        if(isFirst){
            initConfig()
        }else{
            resumeData()
        }
    }

    override fun initConfig() {
        typeSource = intent.getIntExtra("typeSource",0)
        if(intent.getSerializableExtra("listGenre")!=null){
            listGenre = intent.getSerializableExtra("listGenre") as ArrayList<FilterComic>
        }
        adapter = ItemAdapter()
        fastAdapter = FastAdapter.with(adapter)

        initUI()
    }

    override fun initUI() {
        if(isFirst){
            isFirst = false
        }
        setUrlSource(typeSource)
        binding.revListData.layoutManager = GridLayoutManager(this, 2)
        binding.revListData.adapter = fastAdapter
        binding.revListData.animation = null
        binding.revListData.isNestedScrollingEnabled = false
        binding.toolbar.iconSource.visibility = View.GONE
        if(listGenre.size == 0){
            getData()
        }else{
            loadData()
        }
        setListener()
    }

    override fun setListener() {
        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }
        fastAdapter.onClickListener = { view, adapter, item, position ->
            changeIntent(item)
            false
        }

    }

    fun changeIntent(item:AdapterGenre){
        var intent = Intent(this@GenreActivity, ListComicByGenreActivity::class.java)
        var sourceTitle = resources.getStringArray(R.array.source_website_title)
        intent.putExtra("title",item.name+" "+sourceTitle[typeSource])
        intent.putExtra("typeSource",typeSource)
        var sourceUrls = resources.getStringArray(R.array.source_website_url)
        var selectUrl = sourceUrls[typeSource]
        var value = item.value
        try {
            if(value!!.toDouble() > 0.0){
                value = item.name?.replace(" ","-")!!.toLowerCase()
            }
        }catch (e:NumberFormatException){
        }
        when(typeSource){
            0->{
                selectUrl += "genres/"+value
//                    selectUrl += "genres/"+item.value
            }
            1->{
                selectUrl += "genres/"+value
//                    selectUrl += "genres/"+item.value
            }
            2->{
//                    var value = item.name?.replace(" ","-")!!.toLowerCase()
                selectUrl += "genres/"+value
            }
            3->{
                selectUrl += "genre/"+value
//                    selectUrl += "genre/"+item.value
            }
            else->{
                selectUrl += "genres/"+value
//                    selectUrl += "genres/"+item.value
            }
        }
        intent.putExtra("selectUrl",selectUrl)
        startActivity(intent)
    }

    fun setUrlSource(type:Int = 0){
        typeSource = type

        var sourceTitles = resources.getStringArray(R.array.source_website_title)
        var sourceUrls = resources.getStringArray(R.array.source_website_url)
        var title = "Genre "+sourceTitles[type]
        sourceWeb = sourceUrls[type]
        binding.toolbar.iconSource.visibility = View.VISIBLE
        when(type){
            0->{
                binding.toolbar.iconSource.setImageResource(R.drawable.ic_src_komikcast)
            }
            1->{
                binding.toolbar.iconSource.setImageResource(R.drawable.ic_src_westmanga)
            }
            2->{
                binding.toolbar.iconSource.setImageResource(R.drawable.ic_src_ngomik)
            }
            3->{
                binding.toolbar.iconSource.setImageResource(R.drawable.ic_src_shinigami)
            }
            else->{
                binding.toolbar.iconSource.visibility = View.GONE
            }
        }
        binding.toolbar.title.text = title
    }

    fun showProgress(waitTime : Long? = null){
        binding.revListData.visibility = View.GONE
        binding.progress.visibility = View.VISIBLE
        var timer = 1000.toLong()
        if(waitTime != null){
            timer = waitTime
        }
        Handler().postDelayed(Runnable {
            showData()
        },timer)
    }

    fun showData(){
        binding.revListData.visibility = View.VISIBLE
        binding.progress.visibility = View.GONE
        if(adapter.adapterItemCount == 0){
            binding.textDataNotFound.visibility = View.VISIBLE
        }else{
            binding.textDataNotFound.visibility = View.GONE
        }
    }

    fun resumeData(){
        if(adapter.adapterItemCount > 0){
            showProgress(100)
        }else{
            getData()
        }
    }

    fun getData(){
        showProgress()
        adapter.clear()
        var url = sourceWeb+getDefaultIndex()
        var task = ListComicAsyncTask()
        var resultTask = task.execute(url,typeSource.toString())?.get()
        if(resultTask?.isSuccessed!!){
            listGenre = resultTask.genres!!
            loadData()
        }else{
            showData()
        }
    }

    fun loadData(){
        for(filter in listGenre){
            var data = AdapterGenre(this)
//            data.activity = this
            data.id = filter.id
            data.name = filter.name
            data.value = filter.value
            adapter.add(data)
        }

    }

    fun getDefaultIndex():String{
        var result = ""
        if(typeSource == 0){
            result = "daftar-komik/"
        }else if(typeSource == 1){
            result = "manga/"
        }else if(typeSource == 2){
            result = "manga/"
        }else if(typeSource == 3){
            result = "semua-series/"
        }
        return result
    }

}
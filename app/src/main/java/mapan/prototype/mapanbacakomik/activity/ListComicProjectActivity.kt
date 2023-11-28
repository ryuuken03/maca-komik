package mapan.prototype.mapanbacakomik.activity

import android.content.Intent
import android.net.Uri
import android.os.*
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import mapan.prototype.mapanbacakomik.AsyncTask.ListComicAsyncTask
import mapan.prototype.mapanbacakomik.R
import mapan.prototype.mapanbacakomik.adapter.AdapterComicThumbnail
import mapan.prototype.mapanbacakomik.api.APIHelper
import mapan.prototype.mapanbacakomik.api.RetrofitClient
import mapan.prototype.mapanbacakomik.databinding.ActivityListComicProjectBinding
import mapan.prototype.mapanbacakomik.model.ComicThumbnail
import mapan.prototype.mapanbacakomik.model.ListComic
import mapan.prototype.mapanbacakomik.model.api.Comic
import mapan.prototype.mapanbacakomik.util.BaseActivity
import mapan.prototype.mapanbacakomik.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ListComicProjectActivity : BaseActivity() {
    lateinit var binding: ActivityListComicProjectBinding
    lateinit var adapter: ItemAdapter<AdapterComicThumbnail>
    lateinit var fastAdapter: FastAdapter<AdapterComicThumbnail>

    var callbackGetList: Call<ArrayList<Comic>>? = null
    var service: APIHelper?= null

    var listPhotoGallery = ArrayList<AdapterComicThumbnail>()
    var sourceWeb = "https://komikcast.site/"
    var typeSource = 0
    var selectUrl: String? = null
    var isFirst = true
    var grid = 3
    var resultTask : ListComic?= null
    var currentPage = 1
    var isMax = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListComicProjectBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(callbackGetList!=null){
            callbackGetList?.cancel()
        }
    }

    override fun onResume() {
        super.onResume()
        if(isFirst){
            initConfig()
        }else{
            resumeData()
        }
    }

    fun setUrlSource(type:Int = 0){
        typeSource = type
        var sourceTitles = resources.getStringArray(R.array.source_website_title)
        var sourceUrls = resources.getStringArray(R.array.source_website_url)
        var title = "Proyek "+sourceTitles[type]
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

    fun getDefaultIndex():String{
        var result = ""
        if(typeSource == 0){
            result = "project-list/"
        }else if(typeSource == 1){
            result = "project/"
        }else if(typeSource == 2){
            result = "pj/"
        }else if(typeSource == 3){
            result = "project/"
        }
        return result
    }

    override fun initConfig() {
        service = RetrofitClient.getClient(this)?.create(APIHelper::class.java)
        typeSource = intent.getIntExtra("typeSource",0)
        adapter = ItemAdapter()
        fastAdapter = FastAdapter.with(adapter)
        setUrlSource(typeSource)

        initUI()
    }

    override fun initUI() {
        if(isFirst){
            isFirst = false
        }
        binding.revListData.layoutManager = GridLayoutManager(this, grid)
        binding.revListData.adapter = fastAdapter
        binding.revListData.animation = null
        binding.revListData.isNestedScrollingEnabled = false
        loadData()
//        loadDataTemp()
        setListener()
    }

    fun showProgress(waitTime : Long? = null){
        binding.revListData.visibility = View.GONE
        binding.progress.visibility = View.VISIBLE
        var timer = 1000.toLong()
        var isShow = true
        if(waitTime != null){
            timer = waitTime
            if(waitTime == 0.toLong()){
                isShow = false
            }
        }
        if(isShow){
            Handler().postDelayed(Runnable {
                showData()
            },timer)
        }
    }

    fun showData(){
        binding.revListData.visibility = View.VISIBLE
        binding.progress.visibility = View.GONE
    }

    fun setupSwipeRefresh() {
        try {
            binding.swipeRefresh.setOnRefreshListener(object :
                SwipeRefreshLayout.OnRefreshListener {
                override fun onRefresh() {
                    loadData()

                    object : CountDownTimer(2000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            try{
                                binding.swipeRefresh.setRefreshing(false)
                            }catch (e : Exception){
                            }catch (e : KotlinNullPointerException){
                            }
                        }

                        override fun onFinish() {
                            try{
                                binding.swipeRefresh.setRefreshing(false)
                            }catch (e : Exception){
                            }catch (e : KotlinNullPointerException){
                            }
                        }
                    }.start()
                }
            })
        } catch (e: Exception) {
        } catch (e: KotlinNullPointerException) {
        }
    }

    override fun setListener() {

        setupSwipeRefresh()

        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.toolbar.iconSource.setOnClickListener {
//            var linkSource = sourceWeb
            var linkSource = selectUrl!!
            try{
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(linkSource))
                startActivity(i)
//                finish()
            }catch (e : android.content.ActivityNotFoundException){
                var webpage = Uri.parse(linkSource)
                if (!linkSource.startsWith("http://") && !linkSource.startsWith("https://")) {
                    webpage = Uri.parse("http://$linkSource")
                }
                val i = Intent(Intent.ACTION_VIEW, webpage)
                startActivity(i)
//                finish()
            }catch (e : Exception){
                var message = "Gagal memuat aplikasi"
                Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
//                val handler = Handler()
//                handler.postDelayed({
//                    finish()
//                }, 1000)
            }
        }
        binding.revListData.setOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                var mLayoutManager = binding.revListData.layoutManager as LinearLayoutManager
                val firstPos: Int = mLayoutManager.findFirstCompletelyVisibleItemPosition()
                val lastPost: Int = mLayoutManager.findLastCompletelyVisibleItemPosition()
                Log.d("OkPos","firstPos:"+firstPos.toString())
                Log.d("OkPos","lastPost:"+lastPost.toString())
                if (firstPos != 0) {
                    binding.swipeRefresh.setEnabled(false)
                } else {
                    binding.swipeRefresh.setEnabled(true)
                    if (binding.revListData.getScrollState() === 1) {
                        if (binding.swipeRefresh.isRefreshing()){
                            binding.revListData.stopScroll()
                        }
                    }
                }

                if(adapter.adapterItemCount == lastPost+1){
                    if(!isMax){
                        loadMore()
                    }
                }
            }
        })
        fastAdapter.onClickListener = { view, adapter, item, position ->
            var isLastChapter = false
            if(item.urlLastChapter!= null){
                if(!item.urlLastChapter.equals("")){
                    isLastChapter = true
                }
            }
            if(isLastChapter){
                var intent = Intent(this@ListComicProjectActivity, ListChapterPageActivity::class.java)
                intent.putExtra("selectUrl",item.urlLastChapter)
                intent.putExtra("title","Chapter " + item.lastChapter)
                intent.putExtra("titleComic",item.title)
                intent.putExtra("allChapterUrl",item.link)
                intent.putExtra("thumbnail",item.src)
                startActivity(intent)
            }else{
                var intent = Intent(this@ListComicProjectActivity, DetailComicActivity::class.java)
                intent.putExtra("selectUrl",item.link)
                intent.putExtra("titleComic",item.title)
                intent.putExtra("imgSrc",item.src)
                startActivity(intent)
            }
            false
        }
    }

    fun resumeData(){
        if(adapter.adapterItemCount > 0){
            showProgress(100)
        }else{
            loadData()
        }
    }

    fun loadData(){
        if(typeSource == 3){
            loadShinigamiID(true)
        }else{
            showProgress()
            currentPage = 1
            isMax = false
            adapter.clear()
            listPhotoGallery.clear()
            var url = sourceWeb+getDefaultIndex()
            if(selectUrl != null){
                url = selectUrl!!
            }else{
                selectUrl = url
            }
            Log.d("OkCheck", "selectUrl:"+url)
            var task = ListComicAsyncTask()
            resultTask = task.execute(url,typeSource.toString())?.get()
            for(i in  0 .. resultTask!!.list!!.size-1){
                addDataAdapter(resultTask?.list!![i])
            }

            binding.revListData.scrollToPosition(0)
        }

    }
    fun loadShinigamiID(isReset : Boolean = false){
        if(isReset){
            showProgress(0)
            currentPage = 1
            isMax = false
            adapter.clear()
            listPhotoGallery.clear()
        }else{
            currentPage++
        }
        callbackGetList  = service?.loadComicListProject(currentPage)
        callbackGetList!!.enqueue(object : Callback<ArrayList<Comic>> {
            override fun onResponse(
                call: Call<ArrayList<Comic>>,
                response: Response<ArrayList<Comic>>
            ) {
                if (response.isSuccessful) {
                    var tmp = response.body()!!
                    for(i in  0 .. tmp.size-1){
                        var data = tmp[i]
                        var comic = ComicThumbnail()
                        comic.id = i+1.toLong()
                        comic.title = data.title
                        comic.url = data.url
                        comic.imgSrc = data.cover
                        comic.lastChap = data.latestChapter
                        comic.urlLastChap = data.latestChapterUrl
                        addDataAdapter(comic)
                    }
                    if(isReset){
                        showData()
                        binding.revListData.scrollToPosition(0)
                    }else{
                        if(tmp.size == 0){
                            isMax = true
                        }
                    }
                } else {
                    if(!isReset){
                        isMax = true
                    }else{
                        showData()
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Comic>>, t: Throwable) {
                if(!isReset){
                    isMax = true
                }else{
                    showData()
                }
            }
        })
    }

    fun loadMore(){
        if(typeSource == 3) {
            loadShinigamiID()
        }else{
            var url = sourceWeb+getDefaultIndex()+"page/"+(currentPage+1)
            var task = ListComicAsyncTask()
            var resultTask2 = task.execute(url,typeSource.toString())?.get()
            if(resultTask2!!.list!!.size > 0){
                var firstItem = resultTask2!!.list!![0]
                for(iAdapter in adapter.adapterItems){
                    if(firstItem.url!!.equals(iAdapter.link)){
                        isMax = true
                        break
                    }
                }
                if(!isMax){
                    for(i in  0 .. resultTask2!!.list!!.size-1){
                        addDataAdapter(resultTask2?.list!![i])
                    }
                }
                currentPage++
            }else{
                isMax = true
            }
        }
    }

    fun addDataAdapter(comic: ComicThumbnail){
        var data = AdapterComicThumbnail(this)
        data.id = comic.id
        data.title = comic.title
        data.typeSource = typeSource
        data.typeComic = comic.type
        data.link = comic.url
        data.src = comic.imgSrc
        data.lastChapter = comic.lastChap
        data.urlLastChapter = comic.urlLastChap
        adapter.add(data)
        listPhotoGallery.add(data)
    }

}
package mapan.prototype.mapanbacakomik.activity

import android.content.Intent
import android.net.Uri
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
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
import mapan.prototype.mapanbacakomik.databinding.ActivityListComicByGenreBinding
import mapan.prototype.mapanbacakomik.model.ComicThumbnail
import mapan.prototype.mapanbacakomik.model.ListComic
import mapan.prototype.mapanbacakomik.util.BaseActivity
import mapan.prototype.mapanbacakomik.util.Log
import mapan.prototype.mapanbacakomik.util.Util
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ListComicByGenreActivity : BaseActivity() {
    lateinit var binding: ActivityListComicByGenreBinding
    lateinit var adapter: ItemAdapter<AdapterComicThumbnail>
    lateinit var fastAdapter: FastAdapter<AdapterComicThumbnail>

    var listPhotoGallery = ArrayList<AdapterComicThumbnail>()
    var sourceWeb = "https://komikcast.vip/"
    var typeSource = 0
    var selectUrl: String? = null
    var isFirst = true
    var grid = 3
    var resultTask : ListComic?= null

    var isError = false
    var service: APIHelper?= null

    var currentPage = 1
    var isMax = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListComicByGenreBinding.inflate(layoutInflater)
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

    fun setUrlSource(type:Int = 0){
        typeSource = type
        var sourceUrls = resources.getStringArray(R.array.source_website_url)
        var title = intent.getStringExtra("title")
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
        if(typeSource == 3){
//            canUseFilter = false
            currentPage = 0
        }
        binding.toolbar.title.text = title
    }

    override fun initConfig() {
        service = RetrofitClient.getClient(this)?.create(APIHelper::class.java)
        adapter = ItemAdapter()
        fastAdapter = FastAdapter.with(adapter)
        if(intent.getIntExtra("typeSource",0)!=null){
            typeSource = intent.getIntExtra("typeSource",0)
        }
        selectUrl = intent.getStringExtra("selectUrl")
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
        setListener()
    }

    fun hideKeyboardInActivity(){
         if(currentFocus!=null){
             Util.hideKeyboard(this)
         }
    }

    fun showProgress(waitTime : Long? = null){
        binding.revListData.visibility = View.GONE
        binding.progress.visibility = View.VISIBLE
        binding.textDataNotFound.visibility = View.GONE
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
//
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
            var linkSource = selectUrl!!
            try{
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(linkSource))
                startActivity(i)
            }catch (e : android.content.ActivityNotFoundException){
                var webpage = Uri.parse(linkSource)
                if (!linkSource.startsWith("http://") && !linkSource.startsWith("https://")) {
                    webpage = Uri.parse("http://$linkSource")
                }
                val i = Intent(Intent.ACTION_VIEW, webpage)
                startActivity(i)
            }catch (e : Exception){
                var message = "Gagal memuat aplikasi"
                Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
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
                        if(typeSource == 3){
                            loadMoreShinigamiID(currentPage+1)
                        }else{
                            loadMore()
                        }
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
                var intent = Intent(this@ListComicByGenreActivity, ListChapterPageActivity::class.java)
                intent.putExtra("selectUrl",item.urlLastChapter)
                intent.putExtra("title","Chapter " + item.lastChapter)
                intent.putExtra("titleComic",item.title)
                intent.putExtra("allChapterUrl",item.link)
                intent.putExtra("thumbnail",item.src)
                startActivity(intent)
            }else{
                var intent = Intent(this@ListComicByGenreActivity, DetailComicActivity::class.java)
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
        showProgress()
        adapter.clear()
        listPhotoGallery.clear()
        currentPage = 1
        isMax = false
        var task = ListComicAsyncTask()
        resultTask = task.execute(selectUrl,typeSource.toString())?.get()
        if(resultTask?.isSuccessed!!){
            for(i in  0 .. resultTask!!.list!!.size-1){
                addDataAdapter(resultTask?.list!![i])
            }
        }else{
            isError = true
        }
        if(adapter.adapterItemCount > 0 && adapter.adapterItemCount < 9){
            if(!isMax){
                if(typeSource == 3){
                    if(adapter.adapterItemCount == 10){
                        loadMoreShinigamiID(currentPage+1)
                    }
                }else{
                    if(typeSource != 0){
                        loadMore()
                    }
                }
            }
        }
    }

    fun loadMore(){
        var page = "?page="+(currentPage+1).toString()
        if(typeSource == 0){
            page = "/page/"+(currentPage+1).toString()
        }else if(typeSource == 1){
            page = "/page/"+(currentPage+1).toString()
        }else if(typeSource == 2){
            page = "/page/"+(currentPage+1).toString()
        }
        var url = selectUrl+page
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
                currentPage++
            }
        }else{
            isMax = true
        }
    }

    fun loadMoreShinigamiID(page : Int){
        var contentType = "application/x-www-form-urlencoded; charset=UTF-8"
        var bodyData = "action=madara_load_more&" +
                "page="+page.toString()+"&" +
                "template=madara-core%2Fcontent%2Fcontent-archive&" +
                "vars%5Bpaged%5D=1&" +
                "vars%5Borderby%5D=meta_value_num&" +
                "vars%5Btemplate%5D=archive&" +
                "vars%5Bsidebar%5D=full&" +
                "vars%5Bmeta_query%5D%5B0%5D%5B0%5D%5Bkey%5D=_wp_manga_chapter_type&" +
                "vars%5Bmeta_query%5D%5B0%5D%5B0%5D%5Bvalue%5D=manga&" +
                "vars%5Bmeta_query%5D%5B0%5D%5Brelation%5D=AND&" +
                "vars%5Bmeta_query%5D%5Brelation%5D=OR&" +
                "vars%5Bpost_type%5D=wp-manga&" +
                "vars%5Bpost_status%5D=publish&" +
                "vars%5Bmeta_key%5D=_latest_update&" +
                "vars%5Border%5D=desc"
        var body = RequestBody.create("text/plain".toMediaTypeOrNull(), bodyData)

        var callData  = service?.loadMore(contentType,body)
        callData!!.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    var tmp = response.body()!!
                    var task = ListComicAsyncTask()
                    var param = ""
                    var resultTask2 = task.execute(param,typeSource.toString(),tmp.string())?.get()
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
                            currentPage++
                        }
                    }else{
                        isMax = true
                    }
                } else {

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
        })
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
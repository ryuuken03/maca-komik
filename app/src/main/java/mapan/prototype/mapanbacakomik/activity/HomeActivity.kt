package mapan.prototype.mapanbacakomik.activity

import android.content.Intent
import android.net.Uri
import android.os.*
import android.view.View
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import mapan.prototype.mapanbacakomik.AsyncTask.HomeComicAsyncTask
import mapan.prototype.mapanbacakomik.AsyncTask.ListComicAsyncTask
import mapan.prototype.mapanbacakomik.R
import mapan.prototype.mapanbacakomik.adapter.AdapterComicThumbnail
import mapan.prototype.mapanbacakomik.adapter.AdapterGenre
import mapan.prototype.mapanbacakomik.api.APIHelper
import mapan.prototype.mapanbacakomik.api.RetrofitClient
import mapan.prototype.mapanbacakomik.databinding.ActivityHomeBinding
import mapan.prototype.mapanbacakomik.model.ComicThumbnail
import mapan.prototype.mapanbacakomik.model.HomeComic
import mapan.prototype.mapanbacakomik.model.ListComic
import mapan.prototype.mapanbacakomik.util.BaseActivity
import mapan.prototype.mapanbacakomik.util.Log
import mapan.prototype.mapanbacakomik.util.Util
import mapan.prototype.mapanbacakomik.util.shared.SourceWebsitePreference
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class HomeActivity : BaseActivity() {
    lateinit var binding: ActivityHomeBinding
    lateinit var adapter: ItemAdapter<AdapterComicThumbnail>
    lateinit var fastAdapter: FastAdapter<AdapterComicThumbnail>
    lateinit var adapterPopular: ItemAdapter<AdapterComicThumbnail>
    lateinit var fastAdapterPopular: FastAdapter<AdapterComicThumbnail>
    lateinit var adapterGenre: ItemAdapter<AdapterGenre>
    lateinit var fastAdapterGenre: FastAdapter<AdapterGenre>
    var service: APIHelper?= null

    var listPhotoGallery = ArrayList<AdapterComicThumbnail>()
    var listPhotoGalleryPopular = ArrayList<AdapterComicThumbnail>()
    var sourceWeb = "https://komikcast.vip/"
    var typeSource = 0
    var isFirst = true
    var grid = 3
    var resultTask : HomeComic?= null

    var isReload = true

    var isOpenFAB = false
    var currentPage = 1
    var isMax = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onResume() {
        super.onResume()
        typeSource = SourceWebsitePreference(this).getSource()
        if(isFirst){
            initConfig()
        }else{
            if(isReload){
                resumeData()
            }else{
                showProgress(100)
            }
        }
    }

    fun setUrlSource(type:Int = 0){
        typeSource = type

        var sourceTitles = resources.getStringArray(R.array.source_website_title)
        var sourceUrls = resources.getStringArray(R.array.source_website_url)
        var title = "MACA "+sourceTitles[type]
        sourceWeb = sourceUrls[type]
        binding.toolbar.iconOther.visibility = View.VISIBLE
        binding.textProjectAll.text = "Proyek "+sourceTitles[type]
        when(type){
            0->{
                binding.toolbar.iconOther.setImageResource(R.drawable.ic_src_komikcast)
            }
            1->{
                binding.toolbar.iconOther.setImageResource(R.drawable.ic_src_westmanga)
            }
            2->{
                binding.toolbar.iconOther.setImageResource(R.drawable.ic_src_ngomik)
            }
            3->{
                binding.toolbar.iconOther.setImageResource(R.drawable.ic_src_shinigami)
            }
            else->{
                binding.toolbar.iconOther.visibility = View.GONE
            }
        }
//        if(typeSource == 0){
//            title = "MACA Komikcast"
//            sourceWeb = "https://komikcast.site/"
//        }else if(typeSource == 1){
//            title = "MACA Westmanga"
//            sourceWeb = "https://westmanga.info/"
//        }
        binding.toolbar.title.text = title
    }

    override fun initConfig() {

        service = RetrofitClient.getClient(this)?.create(APIHelper::class.java)
        adapterPopular = ItemAdapter()
        fastAdapterPopular = FastAdapter.with(adapterPopular)
        adapter = ItemAdapter()
        fastAdapter = FastAdapter.with(adapter)
        adapterGenre = ItemAdapter()
        fastAdapterGenre = FastAdapter.with(adapterGenre)

        initUI()
    }

    override fun initUI() {
        if(isFirst){
            isFirst = false
        }
        binding.revListPopular.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.revListPopular.adapter = fastAdapterPopular
        binding.revListPopular.animation = null
        binding.revListPopular.isNestedScrollingEnabled = false

        binding.revListData.layoutManager = GridLayoutManager(this, grid)
        binding.revListData.adapter = fastAdapter
        binding.revListData.animation = null
        binding.revListData.isNestedScrollingEnabled = false

        binding.revListDataGenre.layoutManager = GridLayoutManager(this, 2)
        binding.revListDataGenre.adapter = fastAdapterGenre
        binding.revListDataGenre.animation = null
        binding.revListDataGenre.isNestedScrollingEnabled = false

        setSource()
        setListener()
    }

    fun setSource(){
        setUrlSource(typeSource)
        loadData()

    }

    fun hideKeyboardInActivity(){
         if(currentFocus!=null){
             Util.hideKeyboard(this)
         }
    }

    fun showProgress(waitTime : Long? = null){
        hideKeyboardInActivity()
        binding.layoutData.visibility = View.GONE
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
        binding.progress.visibility = View.GONE
        if(adapterGenre.adapterItemCount == 0){
            binding.textGenre.visibility = View.GONE
//            binding.btnShowOtherGenre.visibility = View.GONE
            binding.textGenreAll.visibility = View.GONE
            binding.revListDataGenre.visibility = View.GONE
        }else{
            binding.textGenre.visibility = View.VISIBLE
//            binding.btnShowOtherGenre.visibility = View.VISIBLE
            binding.textGenreAll.visibility = View.VISIBLE
            binding.revListDataGenre.visibility = View.VISIBLE
        }
        if(adapter.adapterItemCount == 0){
            binding.layoutData.visibility = View.GONE
            binding.textDataNotFound.visibility = View.VISIBLE
        }else{
            binding.layoutData.visibility = View.VISIBLE
            binding.textDataNotFound.visibility = View.GONE
        }
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

    fun openCloseFAB(isClick : Boolean = true){
        if(isOpenFAB || isClick){
            binding.openFAB.setImageResource(R.drawable.ic_baseline_add_24)
            binding.openBookmarks.visibility = View.GONE
            binding.textOpenBookmark.visibility = View.GONE
            binding.changeSource.visibility = View.GONE
            binding.textChangeSource.visibility = View.GONE
            binding.history.visibility = View.GONE
            binding.textHistroy.visibility = View.GONE
            isOpenFAB = false
        }else{
            binding.openFAB.setImageResource(R.drawable.ic_baseline_close_24)
            binding.openBookmarks.visibility = View.VISIBLE
            binding.textOpenBookmark.visibility = View.VISIBLE
            binding.changeSource.visibility = View.VISIBLE
            binding.textChangeSource.visibility = View.VISIBLE
            binding.history.visibility = View.VISIBLE
            binding.textHistroy.visibility = View.VISIBLE
            isOpenFAB = true
        }
    }

    override fun setListener() {
        setupSwipeRefresh()

        binding.toolbar.iconOther.setOnClickListener {
            var linkSource = sourceWeb
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
        binding.changeSource.setOnClickListener {
            var intent = Intent(this@HomeActivity, ChangeSourceWebsiteActivity::class.java)
            startActivity(intent)
            isReload = true
            openCloseFAB()
        }
//        binding.toolbar.bookmarks.setOnClickListener {
//            var intent = Intent(this@HomeActivity, ListComicSaveActivity::class.java)
//            startActivity(intent)
//            openCloseFAB()
//        }
        binding.openBookmarks.setOnClickListener {
//            var intent = Intent(this@HomeActivity, ListComicSaveActivity::class.java)
            var intent = Intent(this@HomeActivity, ListComicHistoryOrBookmarkActivity::class.java)
            intent.putExtra("isBookmark",true)
            startActivity(intent)
            openCloseFAB()
        }
        binding.history.setOnClickListener {
            var intent = Intent(this@HomeActivity, ListComicHistoryOrBookmarkActivity::class.java)
            startActivity(intent)
            openCloseFAB()
        }

        binding.openFAB.setOnClickListener {
            openCloseFAB(false)
        }
//        binding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            if (!isMax) {
//                if (v.getChildAt(0).measuredHeight - v.measuredHeight - scrollY < 100) {
////                if (v.getChildAt(0).measuredHeight - v.measuredHeight - scrollY < 2) {
//                    binding.progressLoadMore.visibility = View.VISIBLE
////                    Handler().postDelayed(Runnable {
//                        if(typeSource == 3){
//                            loadMoreShinigamiID(currentPage+1)
//                        }else{
//                            loadMore()
//                        }
////                    },100)
//                }
//            }
//        })
//        binding.btnShowOtherGenre.setOnClickListener {
        binding.textGenreAll.setOnClickListener {
            var intent = Intent(this@HomeActivity, GenreActivity::class.java)
            intent.putExtra("typeSource",typeSource)
            startActivity(intent)
            openCloseFAB()
        }

//        binding.btnProject.setOnClickListener {
        binding.textProjectAll.setOnClickListener {
            var intent = Intent(this@HomeActivity, ListComicProjectActivity::class.java)
            intent.putExtra("typeSource",typeSource)
            startActivity(intent)
            openCloseFAB()
        }

//        binding.btnShowAll.setOnClickListener {
        binding.textComicAll.setOnClickListener {
//            var intent = Intent(this@HomeActivity, ListComicActivity::class.java)
            var intent = Intent(this@HomeActivity, ListComicNewActivity::class.java)
            intent.putExtra("typeSource",typeSource)
            startActivity(intent)
            openCloseFAB()
        }

        fastAdapterGenre.onClickListener = { view, adapter, item, position ->
            var intent = Intent(this@HomeActivity, ListComicByGenreActivity::class.java)
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
            false
        }

        fastAdapter.onClickListener = { view, adapter, item, position ->
            var isLastChapter = false
            if(item.urlLastChapter!= null){
                if(!item.urlLastChapter.equals("")){
                    isLastChapter = true
                }
            }
            if(isLastChapter){
                var intent = Intent(this@HomeActivity, ListChapterPageActivity::class.java)
                intent.putExtra("selectUrl",item.urlLastChapter)
                intent.putExtra("title","Chapter " + item.lastChapter)
                startActivity(intent)
            }else{
                var intent = Intent(this@HomeActivity, DetailComicActivity::class.java)
                intent.putExtra("selectUrl",item.link)
                startActivity(intent)
            }
            openCloseFAB()
            false
        }

        fastAdapterPopular.onClickListener = { view, adapter, item, position ->
            var isLastChapter = false
            if(item.urlLastChapter!= null){
                if(!item.urlLastChapter.equals("")){
                    isLastChapter = true
                }
            }
            if(isLastChapter){
                var intent = Intent(this@HomeActivity, ListChapterPageActivity::class.java)
                intent.putExtra("selectUrl",item.urlLastChapter)
                intent.putExtra("title","Chapter " + item.lastChapter)
                startActivity(intent)
            }else{
                var intent = Intent(this@HomeActivity, DetailComicActivity::class.java)
                intent.putExtra("selectUrl",item.link)
                startActivity(intent)
            }
            openCloseFAB()
            false
        }

    }
    fun resumeData(){
        setSource()
        if(adapter.adapterItemCount > 0){
            showProgress(100)
        }else{
            loadData()
        }
    }

    fun loadGenre(){
        var url = sourceWeb+getDefaultIndex()
        var task = ListComicAsyncTask()
        var resultTask = task.execute(url,typeSource.toString())?.get()
        if(resultTask?.isSuccessed!!){
            var listGenre = resultTask.genres!!

            for(filter in listGenre){
                var data = AdapterGenre(this)
                data.id = filter.id
                data.name = filter.name
                data.value = filter.value
//                if(adapterGenre.adapterItemCount < 4){
                if(adapterGenre.adapterItemCount < 6){
                    adapterGenre.add(data)
                }else{
                    break
                }
            }
        }
    }

    fun loadData(){
        isReload = false
        openCloseFAB()
        showProgress()
        currentPage = 1
        isMax = false
        adapter.clear()
        adapterPopular.clear()
        listPhotoGallery.clear()
        listPhotoGalleryPopular.clear()
        var url = sourceWeb
        Log.d("OkCheck", "selectUrl:"+url)
        var task = HomeComicAsyncTask()
        resultTask = task.execute(url,typeSource.toString())?.get()
//        for(i in  0 .. resultTask!!.list!!.size-1){
        var end = 11
        if(resultTask?.list!!.size < end){
            end = resultTask?.list!!.size
            if(end != 0){
                end --
            }
        }
        Log.d("OkCheck", "end:"+end.toString())
        if(end > 0){
            for(i in  0 .. end){
                addDataAdapter(resultTask?.list!![i])
            }
        }
        binding.scrollView.smoothScrollTo(0,0)
        var endPopular = resultTask!!.popular!!.size-1
        if(resultTask?.popular!!.size == 0){
            endPopular = 0
        }
        if(endPopular > 0){
            for(i in  0 .. endPopular){
                addDataAdapter(resultTask?.popular!![i],true)
            }
        }
        adapterGenre.clear()
        loadGenre()
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

    fun addDataAdapter(comic: ComicThumbnail, isPopular : Boolean = false){
        var data = AdapterComicThumbnail(this)
        data.id = comic.id
        data.title = comic.title
        data.typeSource = typeSource
        data.link = comic.url
        data.src = comic.imgSrc
        data.typeComic = comic.type
        data.lastChapter = comic.lastChap
        data.urlLastChapter = comic.urlLastChap
        if(isPopular){
            adapterPopular.add(data)
            listPhotoGalleryPopular.add(data)
        }else{
            adapter.add(data)
            listPhotoGallery.add(data)
        }
    }

}
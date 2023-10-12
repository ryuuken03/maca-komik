package mapan.prototype.mapanbacakomik.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import io.realm.Realm
import mapan.prototype.mapanbacakomik.AsyncTask.ComicDetailAsyncTask
import mapan.prototype.mapanbacakomik.AsyncTask.ListChapterPageAsyncTask
import mapan.prototype.mapanbacakomik.BuildConfig
import mapan.prototype.mapanbacakomik.R
import mapan.prototype.mapanbacakomik.adapter.AdapterChapterPage
import mapan.prototype.mapanbacakomik.config.Constants
import mapan.prototype.mapanbacakomik.databinding.ActivityListChapterPageBinding
import mapan.prototype.mapanbacakomik.model.ComicChapterPage
import mapan.prototype.mapanbacakomik.model.realm.ComicHistory
import mapan.prototype.mapanbacakomik.model.realm.ComicSave
import mapan.prototype.mapanbacakomik.util.BaseActivity
import mapan.prototype.mapanbacakomik.util.Log
import mapan.prototype.mapanbacakomik.util.Util


class ListChapterPageActivity : BaseActivity() {
    lateinit var binding: ActivityListChapterPageBinding
    lateinit var adapter: ItemAdapter<AdapterChapterPage>
    lateinit var fastAdapter: FastAdapter<AdapterChapterPage>

    var listPageChapter = ArrayList<AdapterChapterPage>()
    var listOnFinishLoad = ArrayList<String>()
    var selectUrl: String? = null
    var prevUrl: String? = null
    var allChapterUrl: String? = null
    var nextUrl: String? = null
    var isFirst = true
    var isOpenDetailComic = false
    var realm: Realm? = null

    var firstPos = 0
    var position = 0
    var savePosition = 0
    var maxPage = 1
    var loadPage = 0
    var perLoadPage = 3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListChapterPageBinding.inflate(layoutInflater)
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
        realm = Realm.getDefaultInstance()
        adapter = ItemAdapter()
        fastAdapter = FastAdapter.with(adapter)

        initUI()
    }

    override fun initUI() {
        if(isFirst){
            isFirst = false
        }
        selectUrl = intent.getStringExtra("selectUrl")
        if(intent.getBooleanExtra("isOpenDetailComic",false) !=null){
            isOpenDetailComic = intent.getBooleanExtra("isOpenDetailComic",false)
        }
//        if(intent.getIntExtra("position",0) !=null){
//            position = intent.getIntExtra("position",0)
//        }
        var title = intent.getStringExtra("title")
        binding.toolbar.title.text = title
        binding.toolbar.btnAddBookmark.visibility = View.VISIBLE
        binding.revListData.layoutManager = LinearLayoutManager(this)
        binding.revListData.adapter = fastAdapter
        binding.revListData.animation = null
        binding.revListData.isNestedScrollingEnabled = false
//        var snapHelper = LinearSnapHelper()
//        snapHelper.attachToRecyclerView(binding.revListData)
        loadData()
        setListener()
    }

    fun showProgress(waitTime : Long? = null){
        binding.titlePage.visibility = View.GONE
        binding.layoutNavControll.visibility = View.GONE
        binding.revListData.visibility = View.GONE
        binding.fastscroller.visibility = View.GONE
        binding.showNavbar.visibility = View.GONE
        binding.progress.visibility = View.VISIBLE

        var timer = 1000.toLong()
        if(waitTime != null){
            timer = waitTime
        }
        if(timer > 0.toLong()){
            Handler().postDelayed(Runnable {
                binding.revListData.visibility = View.VISIBLE
                showData()
            },timer)
        }else{
            binding.fastscroller.visibility = View.INVISIBLE
            binding.revListData.visibility = View.VISIBLE
        }
    }

    fun showData(){
        binding.titlePage.visibility = View.VISIBLE
        binding.layoutNavControll.visibility = View.VISIBLE
        binding.fastscroller.visibility = View.VISIBLE
        binding.progress.visibility = View.GONE
    }

    fun setupSwipeRefresh() {
//        try {
//            binding.swipeRefresh.setOnRefreshListener(object :
//                SwipeRefreshLayout.OnRefreshListener {
//                override fun onRefresh() {
//                    firstPos = 0
//                    loadData()
//
//                    object : CountDownTimer(2000, 1000) {
//                        override fun onTick(millisUntilFinished: Long) {
//                            try{
//                                binding.swipeRefresh.setRefreshing(false)
//                            }catch (e : Exception){
//                            }catch (e : KotlinNullPointerException){
//                            }
//                        }
//
//                        override fun onFinish() {
//                            try{
//                                binding.swipeRefresh.setRefreshing(false)
//                            }catch (e : Exception){
//                            }catch (e : KotlinNullPointerException){
//                            }
//                        }
//                    }.start()
//                }
//            })
//        } catch (e: Exception) {
//        } catch (e: KotlinNullPointerException) {
//        }
    }

    override fun setListener() {
//        setupSwipeRefresh()
        binding.toolbar.btnBack.setOnClickListener {
            finish()
        }
        binding.toolbar.btnAddBookmark.setOnClickListener {
            if(loadPage > 0){
                parsingDetailComic(allChapterUrl!!)
            }else{
                Toast.makeText(this@ListChapterPageActivity,"Mohon Tunggu Sebentar !",Toast.LENGTH_SHORT).show()
            }
        }

        binding.revListData.setOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                var mLayoutManager = binding.revListData.layoutManager as LinearLayoutManager
                firstPos = mLayoutManager.findFirstCompletelyVisibleItemPosition()
                position = mLayoutManager.findFirstVisibleItemPosition()
                if (firstPos != 0) {
                    binding.scrollUp.visibility = View.VISIBLE
                    showHideNavbar(true)
//                    binding.swipeRefresh.setEnabled(false)
                } else {
                    binding.scrollUp.visibility = View.GONE
                    showHideNavbar(false)
//                    binding.swipeRefresh.setEnabled(true)
                    if (binding.revListData.getScrollState() === 1) {
//                        if (binding.swipeRefresh.isRefreshing()){
//                            binding.revListData.stopScroll()
//                        }
                    }
                }
                binding.toolbar.chapterPage.text = (position+1).toString()+" / "+maxPage
            }
        })
        binding.toolbar.chapterPage.setOnClickListener {
//            parsingDetailComic(allChapterUrl!!)
//            var textChapterPage = binding.toolbar.chapterPage.text.toString()
//            var pos = 0
//            if(textChapterPage.contains("/")){
//                var split = textChapterPage.split("/")
//                if(split.size > 1){
//                    pos = split[0].trim().toInt()
//                }
//            }
            if(Constants.IS_DEBUG){
                scrollTo(position)
            }
        }

        binding.btnReresh.setOnClickListener {
            firstPos = 0
            loadData()
        }

        binding.btnScrollToBookmark.setOnClickListener {
            scrollTo(savePosition)
        }
        binding.scrollUp.setOnClickListener {
            binding.scrollUp.visibility = View.GONE
            binding.revListData.scrollToPosition(0)
            showHideNavbar(false)
        }
        binding.showNavbar.setOnClickListener {
            showHideNavbar(false)
        }
        fastAdapter.onClickListener = { view, adapter, item, position ->
            var intent = Intent(this@ListChapterPageActivity, PreviewImageActivity::class.java)
            intent.putExtra("title",item.id.toString())
            intent.putExtra("imgSrc",item.src)
            startActivity(intent)
            false
        }

        fastAdapter.onLongClickListener = { view, adapter, item, position ->
            item.load()
            false
        }

        binding.pagePrev.setOnClickListener {
            selectUrl = prevUrl
            position = 0
            loadData()
        }
        binding.pageAll.setOnClickListener {
            if(isOpenDetailComic){
                finish()
            }else{
                var intent = Intent(this@ListChapterPageActivity, DetailComicActivity::class.java)
                intent.putExtra("selectUrl",allChapterUrl)
                intent.putExtra("title",binding.titlePage.text.toString())
                startActivity(intent)
                finish()
            }
        }
        binding.pageNext.setOnClickListener {
            selectUrl = nextUrl
            position = 0
            loadData()
        }
    }

    fun loadAllData(src:String){
        var isAddLoadPage = true
        for(finish in listOnFinishLoad){
            if(src.equals(finish)){
                isAddLoadPage = false
                break
            }
        }
        if(isAddLoadPage){
            listOnFinishLoad.add(src)
            loadPage++
            Log.d("OkCheck","loadPage:"+loadPage.toString())
            var isShowData = false
            if(loadPage > 0 ){
                isShowData = true
            }
//            if(listPageChapter.size < perLoadPage){
//                isShowData = true
////                showData()
//            }else{
//                if(loadPage >= perLoadPage){
//                    isShowData = true
//                    showData()
//                }
//            }
            if(isShowData){
                showData()
            }
//            checkAddAdapter()
//            if(loadPage == maxPage){
//                Log.d("OkCheck","finish Load All Data")
//                fastAdapter.notifyDataSetChanged()
//            }
        }
    }

    fun showHideNavbar(isGone: Boolean){
        var visiblity = View.VISIBLE
        var visiblityShowBar = View.GONE
        if(isGone){
            visiblity = View.GONE
            visiblityShowBar = View.VISIBLE
        }
        binding.titlePage.visibility = visiblity
        binding.layoutNavControll.visibility = visiblity
        binding.showNavbar.visibility = visiblityShowBar
    }

    fun resumeData(){
        if(adapter.adapterItemCount > 0){
            showProgress(100)
        }else{
            loadData()
        }
    }

    fun checkIsSave() : Int{
        binding.btnScrollToBookmark.visibility = View.GONE
        var result = 0
        val detailChapter = realm!!.where(ComicSave::class.java).equalTo("urlDetail", allChapterUrl).findFirst()
        if(detailChapter!= null){
            result = 1
            if(detailChapter.urlChapter!!.equals(selectUrl)){
                if(detailChapter.pageChapter!=null){
                    position = detailChapter.pageChapter!!
                    savePosition = detailChapter.pageChapter!!
                    if(Constants.IS_DEBUG){
                        binding.btnScrollToBookmark.visibility = View.VISIBLE
                        try {
//                            scrollTo(savePosition)
                        }catch (e:Exception){
                        }
                    }
                }
                result = 2
            }
        }
        return result
    }

    fun saveHistory(){
        var type = "0"
        var sourceUrls = resources.getStringArray(R.array.source_website_url)
        var index = 0
        var titleComic = binding.titlePage.text.toString()
        var genreComic = "-"
        var typeComic = "-"
        var thumbComic = listPageChapter[0].src
        for(source in sourceUrls){
            if(allChapterUrl!!.contains(source,true)){
                type = index.toString()
                break
            }
            index++
        }
        if(allChapterUrl!!.equals(selectUrl)){

        }else{
            var task = ComicDetailAsyncTask()
            var resultTask = task.execute(allChapterUrl,type)?.get()
            if(resultTask?.title!=null){
                titleComic = resultTask?.title!!
            }
            if(resultTask?.genre!=null){
                genreComic = resultTask.genre!!
            }
            if(resultTask?.type!=null){
                typeComic = resultTask.type!!
            }
            if(resultTask?.imgSrc!=null){
                thumbComic = resultTask.imgSrc!!
            }
        }
        val history = realm!!.where(ComicHistory::class.java).findAll()
        var id = 1
        var idDelete = 1
        var isDelete = false
        if(history.size > 0){
            for(comic in history){
                if(comic.urlDetail!!.equals(allChapterUrl)){
                    idDelete = comic.id!!.toInt()
                    isDelete = true
                    break
                }
            }
            id = history.get(history.size-1)!!.id!!.toInt()+1
        }
        var save = ComicHistory()
        save.id = id.toLong()
        save.title = titleComic
        save.genre = genreComic
        save.type = typeComic
        save.chapter = binding.toolbar.title.text.toString()
        save.urlChapter = selectUrl
        save.urlDetail = allChapterUrl
        save.pageChapter = position
        save.imgSrc = thumbComic
        if(isDelete){
            var dataDelete = realm!!.where(ComicHistory::class.java).equalTo("id", idDelete).findAll()
            realm!!.beginTransaction()
            dataDelete.deleteAllFromRealm()
            realm!!.commitTransaction()
        }
        realm!!.beginTransaction()
        realm!!.insertOrUpdate(save)
        realm!!.commitTransaction()
    }

    fun parsingDetailComic(urlDetail:String){
        showProgress()
        var type = "0"
        var sourceUrls = resources.getStringArray(R.array.source_website_url)
        var index = 0
        var titleComic = binding.titlePage.text.toString()
        var genreComic = "-"
        var typeComic = "-"
        var thumbComic = listPageChapter[0].src
        for(source in sourceUrls){
            if(urlDetail.contains(source,true)){
                type = index.toString()
                break
            }
            index++
        }
        if(urlDetail.equals(selectUrl)){

        }else{
            var task = ComicDetailAsyncTask()
            var resultTask = task.execute(urlDetail,type)?.get()
            if(resultTask?.title!=null){
                titleComic = resultTask?.title!!
            }
            if(resultTask?.genre!=null){
                genreComic = resultTask.genre!!
            }
            if(resultTask?.type!=null){
                typeComic = resultTask.type!!
            }
            if(resultTask?.imgSrc!=null){
                thumbComic = resultTask.imgSrc!!
            }
        }
        val listComic = realm!!.where(ComicSave::class.java).findAll()
        var id = 1
        var idDelete = 1
        var isDelete = false
        if(listComic!=null){
//            Log.d("OkCheck","size:"+listComic.size.toString())
            if(listComic.size > 0){
                for(comic in listComic){
                    if(comic.urlDetail!!.equals(urlDetail)){
                        idDelete = comic.id!!.toInt()
                        isDelete = true
                        break
                    }
                }
//                if(!isDelete){
                    id = listComic.get(listComic.size-1)!!.id!!.toInt()+1
//                }else{
//                    Log.d("OkCheck","url:"+url)
//                }
            }
        }
//        Log.d("OkCheck","id:"+id.toString())
        var save = ComicSave()
        save.id = id.toLong()
        save.title = titleComic
        save.genre = genreComic
        save.type = typeComic
        save.chapter = binding.toolbar.title.text.toString()
        save.urlChapter = selectUrl
        save.urlDetail = urlDetail
        save.pageChapter = position
        save.imgSrc = thumbComic
        if(isDelete){
            var dataDelete = realm!!.where(ComicSave::class.java).equalTo("id", idDelete).findAll()
            realm!!.beginTransaction()
            dataDelete.deleteAllFromRealm()
            realm!!.commitTransaction()
        }
        realm!!.beginTransaction()
        realm!!.insertOrUpdate(save)
        realm!!.commitTransaction()

        with(binding.revListData.adapter as FastAdapter<AdapterChapterPage>){
            if(position > 0){
                getItem(position)?.isStar = true
                notifyAdapterDataSetChanged()
            }
        }

        showBookmark()
        if(!isDelete){
            Toast.makeText(this@ListChapterPageActivity,"Komik tersimpan dengan sukses pada halaman "+(position+1).toString(), Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this@ListChapterPageActivity,"Komik telah disimpan pada halaman "+(position+1).toString(),Toast.LENGTH_SHORT).show()
        }
//        showShowData()
    }

    fun showBookmark(){
        var bookmarkType = checkIsSave()
        with(binding.revListData.adapter as FastAdapter<AdapterChapterPage>){
            if(position > 0){
                getItem(position)?.isStar = true
                notifyAdapterDataSetChanged()
            }
        }
        if( bookmarkType == 1){
            binding.toolbar.btnAddBookmark.setImageResource(R.drawable.ic_baseline_bookmark_add_24)
            binding.toolbar.btnAddBookmark.setColorFilter(
                ContextCompat.getColor(this, R.color.black),
                android.graphics.PorterDuff.Mode.SRC_IN);
        }else if(bookmarkType == 2){
            binding.toolbar.btnAddBookmark.setImageResource(R.drawable.ic_baseline_bookmarks_24)
            binding.toolbar.btnAddBookmark.setColorFilter(
                ContextCompat.getColor(this, R.color.color_primary),
                android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    fun loadData(){
        showProgress(0)
        loadPage = 0
        listOnFinishLoad.clear()
        adapter.clear()
        listPageChapter.clear()
        prevUrl = null
        nextUrl = null
        var type = "0"
        var sourceUrls = resources.getStringArray(R.array.source_website_url)
        var index = 0
        for(source in sourceUrls){
            if(selectUrl!!.contains(source,true)){
                type = index.toString()
                break
            }
            index++
        }

        var task = ListChapterPageAsyncTask()
        var resultTask = task.execute(selectUrl,type)?.get()
        for(i in  0 .. resultTask!!.list!!.size-1){
            addToList(resultTask.list!![i])
//            if(i < perLoadPage){
                addToAdapter(listPageChapter[i])
//            }
        }
        maxPage = resultTask.list!!.size
        if(maxPage  > 1){
            binding.toolbar.chapterPage.visibility = View.VISIBLE
        }
        binding.toolbar.chapterPage.text = (position+1).toString()+" / "+maxPage
        if(resultTask.currentChap!=null){
            if(resultTask.currentChap!!.contains("Indonesia",true)){
                resultTask.currentChap = resultTask.currentChap!!.replace("Bahasa Indonesia","",true)
            }
        }
        binding.toolbar.title.text = "Chapter "+resultTask.currentChap

//        binding.titlePage.text = resultTask.title
        binding.titlePage.text = Util.convertStringISOtoUTF8(resultTask.title!!)
        binding.pagePrev.visibility = View.INVISIBLE
        binding.pageAll.visibility = View.INVISIBLE
        binding.pageNext.visibility = View.INVISIBLE
        if(resultTask.pagePrev!=null){
            if(!Util.isTextEmpty(resultTask.pagePrev)){
                prevUrl = resultTask.pagePrev
                binding.pagePrev.visibility = View.VISIBLE
            }
        }
        if(resultTask.pageAll!=null){
            if(!Util.isTextEmpty(resultTask.pageAll)){
                allChapterUrl = resultTask.pageAll
                binding.pageAll.visibility = View.VISIBLE
                showBookmark()
            }
        }
        if(resultTask.pageNext!=null){
            if(!Util.isTextEmpty(resultTask.pageNext)){
                nextUrl = resultTask.pageNext
                binding.pageNext.visibility = View.VISIBLE
            }
        }
        saveHistory()

    }

    fun scrollTo(pos : Int){
        binding.toolbar.chapterPage.text = (pos+1).toString()+" / "+maxPage
        binding.revListData.post{
//            binding.revListData.scrollToPosition(pos)
            binding.revListData.smoothScrollToPosition(pos)
        }
//        Handler().postDelayed(Runnable {
//            binding.revListData.onScrollStateChanged(RecyclerView.SCROLL_STATE_SETTLING)
//            binding.revListData.scrollToPosition(pos)
//        },200)
    }

    fun addToList(chapter: ComicChapterPage){
        var data = AdapterChapterPage(this)
        data.activity = this
        data.id = chapter.id
        data.wSrc = chapter.w
        data.hSrc = chapter.h
        data.src = chapter.imgSrc
//        adapter.add(data)
        listPageChapter.add(data)
    }

    fun checkAddAdapter(){
        if(adapter.adapterItemCount < listPageChapter.size){
            if(loadPage % perLoadPage == 0){
                if(loadPage < maxPage){
                    var start = loadPage
                    var end = loadPage+perLoadPage-1
//                    var start = adapter.adapterItemCount
//                    var end = start + perLoadPage-1
                    if(end >= maxPage){
                        end = loadPage
                    }
                    for(i in start .. end){
                        if(i < maxPage){
                            addToAdapter(listPageChapter[i])
                        }
                    }
                }
            }
//            Handler().postDelayed(Runnable {
//                checkAddAdapter()
//            },300)
        }
    }

    fun addToAdapter(data: AdapterChapterPage){
        adapter.add(data)
    }

}
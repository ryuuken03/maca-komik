package mapan.prototype.mapanbacakomik.activity

import android.content.Intent
import android.os.*
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import io.realm.Realm
import mapan.prototype.mapanbacakomik.AsyncTask.ComicDetailAsyncTask
import mapan.prototype.mapanbacakomik.R
import mapan.prototype.mapanbacakomik.util.BaseActivity
import mapan.prototype.mapanbacakomik.adapter.AdapterChapterName
import mapan.prototype.mapanbacakomik.api.APIHelper
import mapan.prototype.mapanbacakomik.api.RetrofitClient
import mapan.prototype.mapanbacakomik.databinding.ActivityDetailComicBinding
import mapan.prototype.mapanbacakomik.model.ComicChapter
import mapan.prototype.mapanbacakomik.model.api.DetailComic
import mapan.prototype.mapanbacakomik.model.realm.ComicSave
import mapan.prototype.mapanbacakomik.util.Log
import mapan.prototype.mapanbacakomik.util.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class DetailComicActivity : BaseActivity() {
    lateinit var binding: ActivityDetailComicBinding
    lateinit var adapter: ItemAdapter<AdapterChapterName>
    lateinit var fastAdapter: FastAdapter<AdapterChapterName>

    var callbackGetDetail: Call<DetailComic>? = null
    var service: APIHelper?= null

    var listChapter = ArrayList<AdapterChapterName>()
    var selectUrl: String? = null
    var titleComic: String? = null
    var imgSrc: String? = null
    var isFirst = true
    var realm: Realm? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailComicBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(callbackGetDetail!=null){
            callbackGetDetail?.cancel()
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

    override fun initConfig() {
        service = RetrofitClient.getClient(this)?.create(APIHelper::class.java)
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
        imgSrc = intent.getStringExtra("imgSrc")
        titleComic = intent.getStringExtra("titleComic")

        binding.toolbar.title.text = "Daftar Chapter"
        binding.toolbar.btnAddBookmark.visibility = View.VISIBLE
        binding.revListData.layoutManager = LinearLayoutManager(this)
        binding.revListData.adapter = fastAdapter
        binding.revListData.animation = null
        binding.revListData.isNestedScrollingEnabled = false
//        if(checkIsSave(selectUrl!!)){
//            binding.toolbar.btnAddBookmark.setColorFilter(
//                ContextCompat.getColor(this, R.color.color_primary),
//                android.graphics.PorterDuff.Mode.SRC_IN);
//        }

        loadData()
        setListener()
    }

    fun showProgress(waitTime : Long? = null){
        binding.layoutData.visibility = View.GONE
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
        binding.layoutData.visibility = View.VISIBLE
        binding.revListData.visibility = View.VISIBLE
        binding.progress.visibility = View.GONE
    }

    fun checkIsSave(url : String) : Boolean{
        var result = false
        val detailComic = realm!!.where(ComicSave::class.java).equalTo("urlDetail", url).findFirst()
        if(detailComic!= null){
            result = true
        }
        return result
    }


    fun setupSwipeRefresh() {
        try {
            binding.swipeRefresh.setOnRefreshListener(object :
                SwipeRefreshLayout.OnRefreshListener {
                override fun onRefresh() {
                    resumeData()

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
        binding.toolbar.btnAddBookmark.setOnClickListener {
            val listComic = realm!!.where(ComicSave::class.java).findAll()
            var id = 1
            var isSave = true
            if(listComic!=null){
                Log.d("OkCheck","size:"+listComic.size.toString())
                if(listComic.size > 0){
                    for(save in listComic){
                        if(save.urlDetail!!.equals(selectUrl)){
//                            id = save.id!!.toInt()
                            isSave = false
                            break
                        }
                    }
                    if(isSave){
                        id = listComic.get(listComic.size-1)!!.id!!.toInt()+1
                    }else{
                        Log.d("OkCheck","url:"+selectUrl)
                    }
                }
            }
            Log.d("OkCheck","id:"+id.toString())
            if(isSave){
                var save = ComicSave()
                save.id = id.toLong()
                save.title = binding.titlePage.text.toString()
                save.genre = binding.genre.text.toString()
                save.type = binding.type.text.toString()
                save.chapter = "-"
                save.urlChapter = ""
                save.pageChapter = 0
                save.urlDetail = selectUrl
                save.imgSrc = imgSrc
                realm!!.beginTransaction()
                realm!!.insertOrUpdate(save)
                realm!!.commitTransaction()
                Toast.makeText(this@DetailComicActivity,"Komik tersimpan dengan sukses",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@DetailComicActivity,"Komik telah disimpan",Toast.LENGTH_SHORT).show()
            }
            showBookmark()
        }
        fastAdapter.onClickListener = { view, adapter, item, position ->
            var intent = Intent(this@DetailComicActivity, ListChapterPageActivity::class.java)
            intent.putExtra("selectUrl",item.url)
            intent.putExtra("title",item.name)
            intent.putExtra("isOpenDetailComic",true)
            intent.putExtra("titleComic",binding.titlePage.text.toString())
            intent.putExtra("allChapterUrl",selectUrl)
            intent.putExtra("thumbnail",imgSrc)
            startActivity(intent)
            false
        }
        binding.revListData.setOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                var mLayoutManager = binding.revListData.layoutManager as LinearLayoutManager
                val firstPos: Int = mLayoutManager.findFirstCompletelyVisibleItemPosition()
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
            }
        })
    }

    fun resumeData(){
        if(adapter.adapterItemCount > 0){
            showProgress(100)
        }else{
            loadData()
        }
    }

    fun loadDetailComicShinigami(){
        showProgress(0)
        adapter.clear()
        listChapter.clear()
        callbackGetDetail  = service?.loadComicDetail(selectUrl!!)
        callbackGetDetail!!.enqueue(object : Callback<DetailComic> {
            override fun onResponse(
                call: Call<DetailComic>,
                response: Response<DetailComic>
            ) {
                if (response.isSuccessful) {
                    var tmp = response.body()!!
                    for(i in  0 .. tmp.chapterList!!.size-1){
                        var data = tmp.chapterList!![i]
                        var ch = ComicChapter()
                        ch.id = i+1.toLong()
                        var titles = data.title!!.split(" - ")
                        ch.name = titles[0]
                        ch.time = data.releaseDate
                        ch.url = data.url!!
                        addDataAdapter(ch)
                    }
                    binding.titlePage.text = Util.convertStringISOtoUTF8(titleComic!!)
                    var genre = ""
                    var typeComic = ""
                    var author = ""
                    for(data in tmp?.detailList!!){
                        if(data.name!!.contains("Genre",true)){
                            genre = data.value!!
                        }
                        if(data.name!!.contains("Type",true)){
                            typeComic = "Type: "+data.value!!
                        }
                        if(data.name!!.contains("Author",true)){
                            author = "Author: "+data.value!!
                        }
                    }
                    binding.genre.text = Util.loadHtmlView(genre,binding.genre)
                    binding.type.text = Util.loadHtmlView(typeComic,binding.type)
                    binding.release.text = Util.loadHtmlView(author,binding.release)

                    var width = Util.getDisplay(this@DetailComicActivity)!!.widthPixels-Util.convertDpToPx(6,this@DetailComicActivity)
                    var wResult : Double = width.toDouble()/3.0
                    var hResult : Double = width.toDouble()/2.0

                    var param = binding.photo.layoutParams as RelativeLayout.LayoutParams
                    param.width = wResult.toInt()
                    param.height = hResult.toInt()
                    var scaleType = ImageView.ScaleType.CENTER
                    var isImageEmpty = false
                    if(imgSrc == null){
                        isImageEmpty = true
                    }else{
                        if(imgSrc.equals("")){
                            isImageEmpty = true
                        }
                    }
                    if(isImageEmpty){
                        scaleType = ImageView.ScaleType.CENTER_INSIDE
                        var imageNotFound = resources.getStringArray(R.array.source_website_image_not_found)
                        imgSrc = imageNotFound[3]
                    }
                    binding.photo.setScaleType(scaleType)
                    Util.loadImageSetWH(this@DetailComicActivity, imgSrc!!, binding.photo,"",wResult.toInt(),hResult.toInt())
                    showData()
                    showBookmark()
                } else {

                }
            }

            override fun onFailure(call: Call<DetailComic>, t: Throwable) {

            }
        })
    }

    fun loadData(){
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
        if(type.equals("3")){
            loadDetailComicShinigami()
        }else{
            showProgress()
            adapter.clear()
            listChapter.clear()

            var task = ComicDetailAsyncTask()
            var resultTask = task.execute(selectUrl,type)?.get()
            for(i in  0 .. resultTask!!.list!!.size-1){
                addDataAdapter(resultTask.list!![i])
            }
            binding.titlePage.text = Util.convertStringISOtoUTF8(resultTask.title!!)
            binding.genre.text = Util.loadHtmlView(resultTask.genre!!,binding.genre)
            binding.type.text = Util.loadHtmlView(resultTask.type!!,binding.type)
            binding.release.text = Util.loadHtmlView(resultTask.release!!,binding.release)

            var width = Util.getDisplay(this)!!.widthPixels-Util.convertDpToPx(6,this)
            var wResult : Double = width.toDouble()/3.0
            var hResult : Double = width.toDouble()/2.0

            var param = binding.photo.layoutParams as RelativeLayout.LayoutParams
            param.width = wResult.toInt()
            param.height = hResult.toInt()
            imgSrc = resultTask.imgSrc
            var scaleType = ImageView.ScaleType.CENTER
            var isImageEmpty = false
            if(imgSrc == null){
                isImageEmpty = true
            }else{
                if(imgSrc.equals("")){
                    isImageEmpty = true
                }
            }
            if(isImageEmpty){
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                var imageNotFound = resources.getStringArray(R.array.source_website_image_not_found)
                imgSrc = imageNotFound[type.toInt()]
            }
            binding.photo.setScaleType(scaleType)
            Util.loadImageSetWH(this, imgSrc!!, binding.photo,"",wResult.toInt(),hResult.toInt())
            showBookmark()
        }
    }

    fun addDataAdapter(chapter: ComicChapter){
        var data = AdapterChapterName(this)
        data.id = chapter.id
        data.name = chapter.name
        if(data.name!=null){
            if(data.name!!.contains("Indonesia",true)){
                data.name = data.name!!.replace("Bahasa Indonesia","",true)
            }
            if(data.name!!.contains("Chapter",true)){
                var split = data.name!!.split(" ")
                var name = split[0]
                var temp = ""
                if(split.size > 1){
                    Log.d("OkChapter","split > 1 ->"+data.name)
                    var chp = split[1].trim()
                    if(split[1].contains("-")){
                        chp = split[1].split("-")[0]
                        var addTemp = false
                        if(temp.contains("end",true)){
                            addTemp = true
                        }else if(temp.contains("tamat",true)){
                            addTemp = true
                        }
                        if(addTemp){
                            temp = " - "+split[1].split("-")[1]
                        }
                    }
                    try{
                        if(chp.toDouble() > -1){
                        }
                    }catch (e:NumberFormatException){
                        chp = "0"
                    }

//                    chp = chp.replace(".",",")
//                    if(chp.contains(",")){
//                        chp = chp.replace(",",".")
//                    }
                    if(chp.toDouble() < 10){
                        if(!chp.substring(0,1).equals("0")){
                            name += " 0"+chp
                        }else{
                            name += " "+chp
                        }
                    }else{
                        name += " "+chp
                    }
                    if(split.size > 2){
                        for(i in 2 .. split.size-1){
                            name += " "+split[i]
                        }
                    }
                }
                data.name = name+temp
            }
        }
        data.time = chapter.time
        data.url = chapter.url
        adapter.add(data)
        listChapter.add(data)
    }

    fun checkIsSave() : Int{
        var result = 0
        val detailChapter = realm!!.where(ComicSave::class.java).equalTo("urlDetail", selectUrl).findFirst()
        if(detailChapter!= null){
            result = 1
        }
        return result
    }

    fun showBookmark(){
        var bookmarkType = checkIsSave()
//        if( bookmarkType == 1){
//            binding.toolbar.btnAddBookmark.setImageResource(R.drawable.ic_baseline_bookmark_add_24)
//            binding.toolbar.btnAddBookmark.setColorFilter(
//                ContextCompat.getColor(this, R.color.black),
//                android.graphics.PorterDuff.Mode.SRC_IN);
//        }
//        else
        if(bookmarkType == 1){
            binding.toolbar.btnAddBookmark.setImageResource(R.drawable.ic_baseline_bookmarks_24)
            binding.toolbar.btnAddBookmark.setColorFilter(
                ContextCompat.getColor(this, R.color.color_primary),
                android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

}
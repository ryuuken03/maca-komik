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
import mapan.prototype.mapanbacakomik.databinding.ActivityListComicNewBinding
import mapan.prototype.mapanbacakomik.model.ComicThumbnail
import mapan.prototype.mapanbacakomik.model.FilterComic
import mapan.prototype.mapanbacakomik.model.ListComic
import mapan.prototype.mapanbacakomik.model.api.Comic
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


class ListComicNewActivity : BaseActivity() {
    lateinit var binding: ActivityListComicNewBinding
    lateinit var adapter: ItemAdapter<AdapterComicThumbnail>
    lateinit var fastAdapter: FastAdapter<AdapterComicThumbnail>

    var listPhotoGallery = ArrayList<AdapterComicThumbnail>()
    var sourceWeb = "https://komikcast.vip/"
    var typeSource = 0
    var selectUrl: String? = null
    var isFirst = true
    var isSelectFilter = false
    var grid = 3
    var FILTER_CODE = 1111
    var search: String?= null

    var listGenre = ArrayList<FilterComic>()
    var selectGenre = ArrayList<Long>()
    var listType = ArrayList<FilterComic>()
    var selectType = ArrayList<Long>()
    var listOrderBy = ArrayList<FilterComic>()
    var selectOrderBy= ArrayList<Long>()
    var resultTask : ListComic?= null
    var canUseFilter = true

    var isError = false
    var callbackGetList: Call<ArrayList<Comic>>? = null
    var service: APIHelper?= null

    var currentPage = 1
    var isMax = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListComicNewBinding.inflate(layoutInflater)
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
        var title = "MACA "+sourceTitles[type]
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

    fun getDefaultIndex():String{
        var result = ""
        if(typeSource == 0){
            if(search == null){
                result = "daftar-komik/"
            }
        }else if(typeSource == 1){
            if(search == null){
                result = "manga/"
            }
        }else if(typeSource == 2){
            if(search == null){
                result = "manga/"
            }
        }else if(typeSource == 3){
            result = "terbaru/"
        }
        return result
    }

    override fun initConfig() {
        service = RetrofitClient.getClient(this)?.create(APIHelper::class.java)
        adapter = ItemAdapter()
        fastAdapter = FastAdapter.with(adapter)
        if(intent.getIntExtra("typeSource",0)!=null){
            typeSource = intent.getIntExtra("typeSource",0)
        }
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
//        hideKeyboardInActivity()
        binding.layoutFilterNow.visibility = View.GONE
        binding.revListData.visibility = View.GONE
        binding.progress.visibility = View.VISIBLE
        binding.textDataNotFound.visibility = View.GONE
        binding.openPageLinkLayout.visibility = View.GONE
        binding.inputLinkLayout.visibility = View.GONE

        binding.inputLink.setText("")
        var isShow = true
        var timer = 1000.toLong()
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

        if(adapter.adapterItemCount == 0){
            binding.layoutFilterNow.visibility = View.GONE
            binding.textDataNotFound.visibility = View.VISIBLE
            if(isError){
                binding.searchLayout.visibility = View.GONE
                binding.openPageLinkLayout.visibility = View.VISIBLE
            }
        }else{
            binding.textDataNotFound.visibility = View.GONE
            binding.searchLayout.visibility = View.VISIBLE
        }

        if(canUseFilter){
            binding.searchLayout.visibility = View.VISIBLE
            if(typeSource == 3){
                binding.layoutFilterNow.visibility = View.GONE
            }else{
                binding.layoutFilterNow.visibility = View.VISIBLE
            }
        }else{
            binding.searchLayout.visibility = View.GONE
            binding.layoutFilterNow.visibility = View.GONE
        }

        if(search!=null){
            binding.layoutFilterNow.visibility = View.GONE
        }
//
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == FILTER_CODE){
            var isFinish = false
            if(data?.getBooleanExtra("isFinish",false)!=null){
                isFinish = data.getBooleanExtra("isFinish",false)
            }
            if(isFinish){
                isSelectFilter = true
                var genreSelected = data?.getSerializableExtra("genreSelected") as ArrayList<Long>
                selectGenre = genreSelected
                var typeSelected = data?.getSerializableExtra("typeSelected") as ArrayList<Long>
                selectType = typeSelected
                var sortSelected = data?.getSerializableExtra("sortSelected") as ArrayList<Long>
                selectOrderBy = sortSelected
                selectUrl = sourceWeb+getDefaultIndex()+getUrlSeleted()
                var isPointFilter = false
                if(selectType.size > 0){
                    if(selectType[0] != 2.toLong()){
                        isPointFilter = true
                    }
                }
                if(sortSelected.size > 0){
                    if(sortSelected[0] != 2.toLong()){
                        isPointFilter = true
                    }
                }
                if(genreSelected.size > 0){
                    isPointFilter = true
                    var textGenre = "Genre : "
                    for(genre in listGenre){
                        for(select in genreSelected){
                            if(select == genre.id){
                                if(!textGenre.equals("Genre : ")){
                                    textGenre += ", "
                                }
                                textGenre += genre.name!!
                                break
                            }
                        }
                    }
                    binding.textGenre.visibility = View.VISIBLE
                    binding.textGenre.text = textGenre
                }else{
                    binding.textGenre.visibility = View.GONE
                }
                if(isPointFilter){
                    binding.pointFilter.visibility = View.VISIBLE
                }else{
                    binding.pointFilter.visibility = View.GONE
                }
                loadData()
            }
        }
    }

    fun resetFilter(){
        isSelectFilter = false
        selectGenre.clear()
        selectType.clear()
        selectOrderBy.clear()

        hideKeyboardInActivity()
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
//                            loadMoreShinigamiID(currentPage+1)
                            loadShinigamiID()
                        }else{
                            loadMore()
                        }
                    }
                }
            }
        })

        binding.layoutFilterNow.setOnClickListener {
            var intent = Intent(this@ListComicNewActivity, FilterActivity::class.java)
            intent.putExtra("listGenre",listGenre)
            intent.putExtra("listType",listType)
            intent.putExtra("listSort",listOrderBy)
            intent.putExtra("genreSelected",selectGenre)
            intent.putExtra("typeSelected",selectType)
            intent.putExtra("sortSelected",selectOrderBy)
            startActivityForResult(intent,FILTER_CODE)
        }

        fastAdapter.onClickListener = { view, adapter, item, position ->
            var isLastChapter = false
            if(item.urlLastChapter!= null){
                if(!item.urlLastChapter.equals("")){
                    isLastChapter = true
                }
            }
            if(isLastChapter){
                var intent = Intent(this@ListComicNewActivity, ListChapterPageActivity::class.java)
                intent.putExtra("selectUrl",item.urlLastChapter)
                intent.putExtra("title",
                    (if(item.lastChapter!!.contains("chapter",true))
                        ""
                    else "Chapter ") +item.lastChapter)
//                intent.putExtra("title","Chapter " + item.lastChapter)
                intent.putExtra("titleComic",item.title)
                intent.putExtra("allChapterUrl",item.link)
                intent.putExtra("thumbnail",item.src)
                startActivity(intent)
            }else{
                var intent = Intent(this@ListComicNewActivity, DetailComicActivity::class.java)
                intent.putExtra("selectUrl",item.link)
                intent.putExtra("titleComic",item.title)
                intent.putExtra("imgSrc",item.src)
                startActivity(intent)
            }
            false
        }

        binding.search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if(binding.search.text.toString().trim().length != 0){
                    search = binding.search.text.toString()
                }
                resetFilter()
                if(search == null){
//                    selectUrl = sourceWeb+getDefaultIndex()+"?sortby=update"
                    selectUrl = sourceWeb+getDefaultIndex()+getUrlSeleted()
                }else{
                    if(Util.isTextEmpty(search)){
                        search = null
                        selectUrl = sourceWeb+getDefaultIndex()+getUrlSeleted()
                    }else{
                        selectUrl = sourceWeb+getUrlSeleted()
                    }
                }
                loadData()
                true
            }
            false
        }

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().length != 0) {
                    binding.btnClear.visibility = View.VISIBLE
                } else {
                    binding.btnClear.visibility = View.GONE
                }
            }
        })

        binding.btnClear.setOnClickListener {
            search = null
            selectUrl = sourceWeb+getDefaultIndex()+"/"+getUrlSeleted()
            resetFilter()
            binding.search.setText("")
            binding.btnClear.visibility = View.GONE
            loadData()
        }

        binding.openPageLinkLayout.setOnClickListener {
            binding.openPageLinkLayout.visibility = View.GONE
            binding.textDataNotFound.visibility = View.GONE
            binding.inputLinkLayout.visibility = View.VISIBLE
        }

        binding.inputLink.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                var link = ""
                if(binding.inputLink.text.toString().trim().length != 0){
                    link = binding.inputLink.text.toString()
                }

                hideKeyboardInActivity()

                var isDetail = false
                if(typeSource == 0){
                    if(link.contains("/komik/",true)){
                        isDetail = true
                    }
                }else{
                    if(link.contains("/manga/",true)){
                        isDetail = true
                    }
                }

                if(isDetail){
                    var intent = Intent(this@ListComicNewActivity, DetailComicActivity::class.java)
                    intent.putExtra("selectUrl",link)
                    startActivity(intent)
                }else{
                    var intent = Intent(this@ListComicNewActivity, ListChapterPageActivity::class.java)
                    intent.putExtra("selectUrl",link)
//                    intent.putExtra("title",item.lastChapter)
                    intent.putExtra("firstPos",0)
                    startActivity(intent)
                }
                true
            }
            false
        }
        binding.inputLink.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().length != 0) {
                    binding.btnClearLink.visibility = View.VISIBLE
                } else {
                    binding.btnClearLink.visibility = View.GONE
                }
            }
        })
        binding.btnClearLink.setOnClickListener {
            binding.inputLink.setText("")
            binding.btnClearLink.visibility = View.GONE
        }
    }

    fun getUrlSeleted(isPage:Boolean = false):String{
        var url =""
        if(search!=null){
            if(typeSource  == 0){
                url+="?"
            }else if(typeSource  == 1){
                if(search!=null){
                    url+="?"
                }
            }else if(typeSource  == 2){
                if(search!=null){
                    url+="?"
                }
            }else if(typeSource  == 3){
                url+="?"
            }else{
                if(!isPage){
                    url+="?"
                }else{
                    url+="&"
                }
            }
            url+="s="+search
            if(typeSource  == 3){
                url+="&post_type=wp-manga"
            }
        }else{
            if(isSelectFilter){
                if(typeSource  == 0){
                    url+="?"
                }else{
                    if(!isPage){
                        url+="?"
                    }else{
                        url+="&"
                    }
                }
                if(selectGenre.size>0){
                    var i = 0
                    for(genre in listGenre){
                        for(select in selectGenre){
                            if(genre.id == select){
                                if(isPage){
                                    url+="genre%5B"+i.toString()+"%5D="+genre.value+"&"
                                }else{
                                    url+="genre%5B%5D="+genre.value+"&"
                                }
                                i++
                                break
                            }
                        }
                    }
                }
                url+="status"
                url+="&type"
                if(selectType.size > 0){
                    for(type in listType){
                        if(type.id == selectType[0]){
                            if(!type.value.equals("")){
                                url+="="+type.value
                            }
                            break
                        }
                    }
                }else{
                    url+=listType[0].value
                }
                if(typeSource == 0){
                    url+="&orderby"
                }else if(typeSource == 1){
                    url+="&order"
                }else if(typeSource == 2){
                    url+="&order"
                }
                if(selectOrderBy.size > 0){
                    for(orderby in listOrderBy){
                        if(orderby.id == selectOrderBy[0]){
                            if(typeSource == 0){
                                url = url.replace("orderby","sortby")
                            }
                            url+="="+orderby.value
                            break
                        }
                    }
                }else{
                    url+=listOrderBy[0].value
                }
            }else{
                if(typeSource  == 0){
                    url+="?"
                }else{
                    if(!isPage){
                        url+="?"
                    }else{
                        url+="&"
                    }
                }
                if(typeSource == 0){
                    url+="sortby=update"
                }else if(typeSource == 3) {
                    url+="m_orderby=latest"
                }else{
                    url+="order=update"
                }
            }
        }
        return url
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
            adapter.clear()
            listPhotoGallery.clear()
            currentPage = 1
            isMax = false
            var url = sourceWeb+getDefaultIndex()
            url+=getUrlSeleted()
            if(selectUrl != null){
                url = selectUrl!!
            }else{
                selectUrl = url
            }
            Log.d("OkCheck", "selectUrl:"+url)
            var task = ListComicAsyncTask()
            resultTask = task.execute(url,typeSource.toString())?.get()
            if(resultTask?.isSuccessed!!){
                for(i in  0 .. resultTask!!.list!!.size-1){
                    addDataAdapter(resultTask?.list!![i])
                }
                listGenre = resultTask?.genres!!
                listType = resultTask?.types!!
                if(listType.size> 0 ){
                    if(selectType.size == 0){
                        selectType.add(listType[0].id!!)
                    }
                }
                listOrderBy = resultTask?.orderbys!!
                if(listOrderBy.size> 0 ){
                    if(selectOrderBy.size == 0){
                        selectOrderBy.add(listOrderBy[2].id!!)
                    }
                }
            }else{
                isError = true
            }
            if(adapter.adapterItemCount > 0 && adapter.adapterItemCount < 9){
                if(!isMax){
                    if(typeSource == 3){
//                    if(adapter.adapterItemCount == 10){
//                        loadShinigamiID(currentPage+1)
//                    }
                    }else{
                        if(typeSource != 0){
                            loadMore()
                        }
                    }
                }
            }
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
        if(search!=null){
            callbackGetList  = service?.loadComicListSearch(search!!,currentPage)
        }else{
            callbackGetList  = service?.loadComicList(currentPage)
        }
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
        var page = "?page="+(currentPage+1).toString()
        if(typeSource == 0){
            page = "page/"+(currentPage+1).toString()
        }else if(typeSource == 1){
            if(search!=null){
                page = "page/"+(currentPage+1).toString()
            }
        }else if(typeSource == 2){
            if(search!=null){
                page = "page/"+(currentPage+1).toString()
            }
        }
        var url = sourceWeb+getDefaultIndex()+page+getUrlSeleted(true)
//        if(typeSource == 0){
//            url = sourceWeb+page+getUrlSeleted(true)
//        }
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
        if(search!=null){
            bodyData="action=madara_load_more&" +
                    "page="+page.toString()+"&" +
                    "template=madara-core%2Fcontent%2Fcontent-search&" +
                    "vars%5Bs%5D="+search+"&" +
                    "vars%5Borderby%5D=&" +
                    "vars%5Bpaged%5D=1&" +
                    "vars%5Btemplate%5D=search&" +
                    "vars%5Bmeta_query%5D%5B0%5D%5Brelation%5D=AND&" +
                    "vars%5Bmeta_query%5D%5Brelation%5D=AND&" +
                    "vars%5Bpost_type%5D=wp-manga&" +
                    "vars%5Bpost_status%5D=publish&" +
                    "vars%5Bmanga_archives_item_layout%5D=big_thumbnail"
        }

        var body = RequestBody.create("text/plain".toMediaTypeOrNull(), bodyData)

        var callData  = service?.loadMore(contentType,body)
        callData!!.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    var tmp = response.body()!!
//                    currentPage++
                    var task = ListComicAsyncTask()
                    var param = ""
                    if(search!=null){
                        param = "?s="
                    }
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
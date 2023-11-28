package mapan.prototype.mapanbacakomik.activity

import android.content.Intent
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.listeners.ClickEventHook
import io.realm.Realm
import io.realm.Sort
import mapan.prototype.mapanbacakomik.R
import mapan.prototype.mapanbacakomik.adapter.AdapterComicHistoryOrBookmark
import mapan.prototype.mapanbacakomik.databinding.ActivityListComicHistoryBinding
import mapan.prototype.mapanbacakomik.databinding.ItemAdapterComicHistoryOrBookmarkBinding
import mapan.prototype.mapanbacakomik.model.realm.ComicHistory
import mapan.prototype.mapanbacakomik.model.realm.ComicSave
import mapan.prototype.mapanbacakomik.util.BaseActivity
import mapan.prototype.mapanbacakomik.util.Util
import java.util.*
import kotlin.collections.ArrayList


class ListComicHistoryOrBookmarkActivity : BaseActivity() {
    lateinit var binding: ActivityListComicHistoryBinding
    lateinit var adapter: ItemAdapter<AdapterComicHistoryOrBookmark>
    lateinit var fastAdapter: FastAdapter<AdapterComicHistoryOrBookmark>

    var listSource = ArrayList<AdapterComicHistoryOrBookmark>()
    var listSearch = ArrayList<AdapterComicHistoryOrBookmark>()
    var isFirst = true
    var realm: Realm? = null
    var isBookmark = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListComicHistoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onResume() {
        super.onResume()
        if(isFirst){
            initConfig()
        } else{
            resumeData()
        }
    }

    override fun initConfig() {
        isBookmark = intent.getBooleanExtra("isBookmark",false)
        realm = Realm.getDefaultInstance()
        adapter = ItemAdapter()
        fastAdapter = FastAdapter.with(adapter)

        initUI()
    }

    override fun initUI() {
        if(isFirst){
            isFirst = false
        }
        var title = "Riwayat MACA"
        if(isBookmark){
            title = "Bookmark"
        }
        binding.toolbar.title.text = title
        binding.revListData.layoutManager = LinearLayoutManager(this)
        binding.revListData.adapter = fastAdapter
        binding.revListData.animation = null
        binding.revListData.isNestedScrollingEnabled = false

        loadData()
        setListener()
    }

    fun showProgress(waitTime : Long? = null){
        binding.progress.visibility = View.VISIBLE
        binding.revListData.visibility = View.GONE
        binding.layoutData.visibility = View.GONE
        if(waitTime !=null){
//        var timer = 1000.toLong()
//        if(waitTime != null){
//            timer = waitTime
//        }
            Handler().postDelayed(Runnable {
                showData()
            },waitTime)
        }
    }
    fun showData(){
        binding.progress.visibility = View.GONE
        binding.revListData.visibility = View.VISIBLE
        binding.layoutData.visibility = View.VISIBLE

    }

    fun searchComic(search : String){
        adapter.clear()
        listSearch.clear()
        for(item in listSource){
            var isAdd = false
            if(search.trim().length == 0){
                isAdd = true
            }else{
                var sentence = search.toLowerCase()
                if(item.title!!.toLowerCase().contains(sentence)){
                    isAdd = true
                }else if(item.genre!!.toLowerCase().contains(sentence)){
                    isAdd = true
                }else if(item.typeComic!!.toLowerCase().contains(sentence)){
                    isAdd = true
                }
//                else if(item.lastChapter!!.toLowerCase().contains(sentence)){
//                    isFilter = true
//                }
                else if(item.source!!.toLowerCase().contains(sentence)){
                    isAdd = true
                }
            }
            if(isAdd){
//                adapter.add(item)
                listSearch.add(item)
            }
        }
    }

    override fun setListener() {
//        adapter.itemFilter.filterPredicate = { item: AdapterComicSave, constraint: CharSequence? ->
//            if(constraint.toString().trim().length == 0){
//                true
//            }else{
//                var sentence = constraint.toString().toLowerCase()
//                var isFilter = false
//                if(item.title!!.toLowerCase().contains(sentence)){
//                    isFilter = true
//                }else if(item.genre!!.toLowerCase().contains(sentence)){
//                    isFilter = true
//                }else if(item.typeComic!!.toLowerCase().contains(sentence)){
//                    isFilter = true
//                }
////                else if(item.lastChapter!!.toLowerCase().contains(sentence)){
////                    isFilter = true
////                }
//                else if(item.source!!.toLowerCase().contains(sentence)){
//                    isFilter = true
//                }
//                isFilter
//            }
//        }

        binding.toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                var search = ""
                if(binding.search.text.toString().trim().length != 0){
                    search = binding.search.text.toString()
                }
                Util.hideKeyboard(this@ListComicHistoryOrBookmarkActivity)
//                adapter.filter(search)
                searchComic(search)
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
            binding.search.setText("")
            searchComic("")
            Util.hideKeyboard(this@ListComicHistoryOrBookmarkActivity)
        }
        fastAdapter.onClickListener = { view, adapter, item, position ->
            if(item.lastChapter.equals("Chapter All",true)){
                var intent = Intent(this@ListComicHistoryOrBookmarkActivity, DetailComicActivity::class.java)
                intent.putExtra("selectUrl",item.urlDetailComic)
                intent.putExtra("titleComic",item.title)
                intent.putExtra("imgSrc",item.imgSrc)
                startActivity(intent)
            }else{
                var intent = Intent(this@ListComicHistoryOrBookmarkActivity, ListChapterPageActivity::class.java)
                intent.putExtra("selectUrl",item.urlLastChapter)
                intent.putExtra("title",item.lastChapter)
                intent.putExtra("position",item.pageChapter)
                intent.putExtra("titleComic",item.title)
                intent.putExtra("allChapterUrl",item.urlDetailComic)
                intent.putExtra("thumbnail",item.imgSrc)
                startActivity(intent)
            }
            false
        }
        if(isBookmark){
            fastAdapter.addEventHook(object : ClickEventHook<AdapterComicHistoryOrBookmark>() {
                inline fun <reified T : ViewBinding> RecyclerView.ViewHolder.asBinding(block: (T) -> View): View? {
                    return if (this is BindingViewHolder<*> && this.binding is T) {
                        block(this.binding as T)
                    } else {
                        null
                    }
                }

                override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                    return viewHolder.asBinding<ItemAdapterComicHistoryOrBookmarkBinding> {
                        it.delete
                    }
                }

                override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<AdapterComicHistoryOrBookmark>, item: AdapterComicHistoryOrBookmark) {
                    var title = "Hapus Komik"
                    var message = "Apakah kamu ingin menghapus komik ini dari penyimpanan ?"
                    showPrompt(title,message,View.OnClickListener {
                        checkDialogDismissable(dialog)
                        showProgress()
                        realm!!.beginTransaction()
                        val dataDelete =
                            realm!!.where(ComicSave::class.java).equalTo("id", item.id).findAll()
                        dataDelete.deleteAllFromRealm()
                        realm!!.commitTransaction()
                        loadData()
                    })
                }
            })
        }
    }

    fun resumeData(){
//        if(adapter.adapterItemCount > 0){
//            showProgress(100)
//        }
//        else{
            loadData()
//        }
    }

    fun loadData(){
        showProgress()
        adapter.clear()
        listSearch.clear()
        listSource.clear()
        if(isBookmark){
            val bookmarks = realm!!.where(ComicSave::class.java)
                .sort("id", Sort.DESCENDING).findAll()

            if(bookmarks!=null){
                for(comic in bookmarks){
                    addDataAdapter(comic)
                }
            }
        }else{
            val histories = realm!!.where(ComicHistory::class.java)
                .sort("id", Sort.DESCENDING).findAll()
            var removeHistories = ArrayList<Long>()
            if(histories.size > 20){
                var query = realm!!.where(ComicHistory::class.java)
                for(i in 20 .. histories.size-1){
                    var id = histories[i]!!.id!!
                    if(removeHistories.size > 0){
                        query = query.or()
                    }
                    query = query.equalTo("id", id)
                    removeHistories.add(id)
                }
                var dataDelete = query.findAll()
                realm!!.beginTransaction()
                dataDelete.deleteAllFromRealm()
                realm!!.commitTransaction()
            }
            if(histories!=null){
                for(comic in histories){
                    addDataAdapter(comic)
                }
            }
        }
        for(data in listSearch){
            adapter.add(data)
        }
        showData()
    }

    fun addDataAdapter(chapter: ComicHistory){
        var data = AdapterComicHistoryOrBookmark(this)
        data.id = chapter.id
        data.number = (listSource.size+1).toLong()
        data.title = chapter.title
        data.typeComic = chapter.type
        data.genre = chapter.genre
        data.imgSrc = chapter.imgSrc
        data.lastChapter = chapter.chapter
        data.urlLastChapter = chapter.urlChapter
        data.urlDetailComic = chapter.urlDetail
        var sourceUrls = resources.getStringArray(R.array.source_website_url)
        var sourceTitles = resources.getStringArray(R.array.source_website_title)
        if(chapter.pageChapter!=null){
            data.pageChapter = chapter.pageChapter!!
        }
        var source = "Komikcast"
        var index = 0
        for(url in sourceUrls){
            if(data.urlDetailComic!!.contains(url,true)){
                source = sourceTitles[index]
                break
            }
            index++
        }
        data.source = source
//        adapter.add(data)
        listSearch.add(data)
        listSource.add(data)
    }

    fun addDataAdapter(chapter: ComicSave){
        var data = AdapterComicHistoryOrBookmark(this)
        data.id = chapter.id
        data.number = (listSource.size+1).toLong()
        data.title = chapter.title
        data.typeComic = chapter.type
        data.genre = chapter.genre
        data.imgSrc = chapter.imgSrc
        data.lastChapter = chapter.chapter
        data.urlLastChapter = chapter.urlChapter
        data.urlDetailComic = chapter.urlDetail
        data.isBookmark = true
        var sourceUrls = resources.getStringArray(R.array.source_website_url)
        var sourceTitles = resources.getStringArray(R.array.source_website_title)
        if(chapter.pageChapter!=null){
            data.pageChapter = chapter.pageChapter!!
        }
        var source = "Komikcast"
        var index = 0
        for(url in sourceUrls){
            if(data.urlDetailComic!!.contains(url,true)){
                source = sourceTitles[index]
                break
            }
            index++
        }
        data.source = source
        listSearch.add(data)
        listSource.add(data)
    }

}
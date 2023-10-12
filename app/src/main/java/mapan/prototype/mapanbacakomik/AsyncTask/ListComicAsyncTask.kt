package mapan.prototype.mapanbacakomik.AsyncTask

import android.os.AsyncTask
import mapan.prototype.mapanbacakomik.model.ComicThumbnail
import mapan.prototype.mapanbacakomik.model.FilterComic
import mapan.prototype.mapanbacakomik.model.ListComic
import mapan.prototype.mapanbacakomik.util.Log
import mapan.prototype.mapanbacakomik.util.Util
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.FileNotFoundException

import java.net.URL

/***
 * Created By Mohammad Toriq on 19/02/2023
 */
class ListComicAsyncTask : AsyncTask<String, Void, ListComic>() {

    override fun onPostExecute(result: ListComic) {
        super.onPostExecute(result)
    }

    override fun doInBackground(vararg url: String): ListComic{
        Log.d("OkCheck", url[0])
        var result = ListComic()
        var isEmpty = false
//        try{
//            Log.d("OkParse","url[0]:"+url[0])
//            Log.d("OkParse","url[1]:"+url[1])
            var doc : Document?= null
            if(!url[0].contains("http")){
//                Log.d("OkParse","url[2]:"+url[2])
                doc = Jsoup.parse(url[2])
            }else{
                try{
//                    doc = Jsoup.parse(URL(url[0]).openStream(),  "ISO-8859-1", url[0])
                    doc = Jsoup.parse(URL(url[0]).openStream(),  "UTF-8", url[0])
                }catch (e: FileNotFoundException){
                    isEmpty = true
                }
            }
            if(!isEmpty){
                if(url[1].equals("0")){
                    result = parsingKomikcast(doc!!)
                }else if(url[1].equals("1")){
                    result = parsingWestmanga(doc!!)
                }else if(url[1].equals("2")){
                    result = parsingNgomik(doc!!)
                }else if(url[1].equals("3")){
                    var isSearch = false
                    if(url[0].contains("?s=")){
                        isSearch = true
                    }
//                else{
//                    if(url.size > 2){
//                        if(url[0].contains("?s=")){
//                            isSearch = true
//                        }
//                    }
//                }
                    if(isSearch){
                        result = parsingShinigamiIdSearch(doc!!)
                    }else{
                        result = parsingShinigamiId(doc!!)
                    }
                }
            }else{
                var homePage = ListComic()
                var listThumb = ArrayList<ComicThumbnail>()
                var genres = ArrayList<FilterComic>()
                var types = ArrayList<FilterComic>()
                var orderbys = ArrayList<FilterComic>()
                var page1 = "-1"
                var page2 = "-1"
                var page2Url = ""
                var page3 = "-1"
    //        var page3Url = ""
                var pageLast = "-1"
                homePage.page1 = page1
                homePage.page2 = page2
                homePage.page2Url = page2Url
                homePage.page3 = page3
    //        homePage.page3Url = page3Url
                homePage.pageLast = pageLast
    //        homePage.pageLastUrl = pageLastUrl
                homePage.list = listThumb
                homePage.genres = genres
                homePage.types = types
                homePage.orderbys = orderbys

                result = homePage
            }
//        }catch (e:Exception){
//            var homePage = ListComic()
//            var listThumb = ArrayList<ComicThumbnail>()
//            var genres = ArrayList<FilterComic>()
//            var types = ArrayList<FilterComic>()
//            var orderbys = ArrayList<FilterComic>()
//            var page1 = "-1"
//            var page2 = "-1"
//            var page2Url = ""
//            var page3 = "-1"
////        var page3Url = ""
//            var pageLast = "-1"
//            homePage.page1 = page1
//            homePage.page2 = page2
//            homePage.page2Url = page2Url
//            homePage.page3 = page3
////        homePage.page3Url = page3Url
//            homePage.pageLast = pageLast
////        homePage.pageLastUrl = pageLastUrl
//            homePage.list = listThumb
//            homePage.genres = genres
//            homePage.types = types
//            homePage.orderbys = orderbys
//            homePage.isSuccesed = false
//
//            result = homePage
//        }
        return result

    }

    fun parsingKomikcast(doc: Document): ListComic{
        doc.charset()
        var listThumb = ArrayList<ComicThumbnail>()
        var listThumbProject = ArrayList<ComicThumbnail>()
        val listupds = doc.select(".list-update_items-wrapper")
        var listUpdateItem = listupds[0].getElementsByClass("list-update_item")
        for(uta in listUpdateItem){
            var links = uta.getElementsByClass("data-tooltip")
//            Log.d("OkCheckSize", links.size.toString())
            for (i in 0 .. 0) {
                var imgSrc = uta.getElementsByTag("img")[0].attr("src")
//                Log.d("OkCheckUta", "uta:"+uta.toString())
//                Log.d("OkCheck", "imgSrc:"+imgSrc)
                if(imgSrc.equals("")){
//                    Log.d("OkChecknoScript", "noScript:"+uta.getElementsByTag("noscript").toString())
                    var noScript = uta.getElementsByTag("noscript")
                    if(noScript.size > 0){
                        imgSrc = noScript[0].getElementsByTag("img")[0].attr("src")
                    }
                }
                var type = ""
                var span = uta.getElementsByTag("span")
                if(span.size > 0){
                    for(it in span){
                        if(it.className().contains("type")){
                            type = it.text()
                            break
                        }
                    }
                }
//            Log.d("OkCheck","Type:"+type)
                var title = uta.getElementsByTag("h3")[0].text()
//                val charset = Charsets.UTF_8
                var chapter = uta.getElementsByClass("chapter")[0].text()
                var chapterLink = uta.getElementsByClass("chapter")[0].attr("href")
                var url = links[i].attr("href")

                var com = ComicThumbnail()
                com.id = i+1.toLong()
                com.title = title
                com.url = url
                com.type = type
                com.imgSrc = imgSrc
                com.lastChap = chapter
                com.urlLastChap = chapterLink
                listThumb.add(com)
            }
        }
        var filters = doc.select(".komiklist_filter-dropdown")
        var genres = ArrayList<FilterComic>()
        var types = ArrayList<FilterComic>()
        var orderbys = ArrayList<FilterComic>()
        for(filter in filters){
            var ulGenre = filter.getElementsByClass("komiklist_dropdown-menu c4 genrez")
            if(ulGenre.size > 0){
                var lis = ulGenre[0].getElementsByTag("li")
                for(li in lis){
                    var value = li.getElementsByTag("input").attr("value")
                    var name = li.getElementsByTag("label").text()
                    var filComic = FilterComic()
                    filComic.id = (genres.size+1).toLong()
                    filComic.value = value
                    filComic.name = name
                    genres.add(filComic)
                }
            }
            var ulType = filter.getElementsByClass("komiklist_dropdown-menu type")
            if(ulType.size > 0){
                var lis = ulType[0].getElementsByTag("li")
                for(li in lis){
                    var value = li.getElementsByTag("input").attr("value")
                    var name = li.getElementsByTag("label").text()
                    var filComic = FilterComic()
                    filComic.id = (types.size+1).toLong()
                    filComic.value = value
                    filComic.name = name
                    types.add(filComic)
                }
            }
            var ulOrderBy = filter.getElementsByClass("komiklist_dropdown-menu sort_by")
            if(ulOrderBy.size > 0){
                var lis = ulOrderBy[0].getElementsByTag("li")
                for(li in lis){
                    var value = li.getElementsByTag("input").attr("value")
                    var name = li.getElementsByTag("label").text()
                    var filComic = FilterComic()
                    filComic.id = (orderbys.size+1).toLong()
                    filComic.value = value
                    filComic.name = name
                    orderbys.add(filComic)
                }
            }
        }

        var pagination = doc.select(".pagination")
        var page1 = "-1"
        var page2 = "-1"
        var page2Url = ""
        var page3 = "-1"
        var pageLast = "-1"
        if(pagination.size > 0){
            var checkPage1 = pagination[0].getElementsByClass("page-numbers current")
            if(checkPage1.size > 0){
                var pages =  pagination[0].getElementsByClass("page-numbers")
                pageLast = pagination[0].getElementsByClass("page-numbers")[pages.size-1].text()
                if(!Util.isNumeric(pageLast)){
                    pageLast = pagination[0].getElementsByClass("page-numbers")[pages.size-2].text()
    //                pageLastUrl = pagination[0].getElementsByClass("page-numbers")[pages.size-2].attr("href")
                }
                page1 = pagination[0].getElementsByClass("page-numbers current")[0].text()
                page2 = (page1.toInt()+1).toString()
                page3 = (page2.toInt()+1).toString()
                if(pagination[0].getElementsByClass("page-numbers").size > 1){
                    page2Url = pagination[0].getElementsByClass("page-numbers")[1].attr("href")
    //                Log.d("Okpage2Url",page2Url)
                    if(pagination[0].getElementsByClass("page-numbers").size > 2){
    //                    page3Url = pagination[0].getElementsByClass("page-numbers")[2].attr("href")
                    }
                }
                if(page2.toInt() >= pageLast.toInt()){
                    if(!page2.equals(pageLast)){
                        page2="-1"
                        page2Url=""
                    }
                    page3="-1"
    //                page3Url=""
                    pageLast="-1"
    //                pageLastUrl=""
                }else if(page3.toInt()>= pageLast.toInt()){
                    if(!page3.equals(pageLast)){
                        page3="-1"
    //                    page3Url=""
                    }
                    pageLast="-1"
    //                pageLastUrl=""
                }
            }
        }

        var homePage = ListComic()
        homePage.page1 = page1
        homePage.page2 = page2
        homePage.page2Url = page2Url
        homePage.page3 = page3
//        homePage.page3Url = page3Url
        homePage.pageLast = pageLast
//        homePage.pageLastUrl = pageLastUrl
        homePage.list = listThumb
        homePage.genres = genres
        homePage.types = types
        homePage.orderbys = orderbys
        return homePage
    }

    fun parsingWestmanga(doc: Document): ListComic{
        var listThumb = ArrayList<ComicThumbnail>()
        val listupds = doc.select(".listupd")
        var listUpdateItem = listupds[0].getElementsByClass("bs")
        var i = 0
//        Log.d("OkCheck", "listUpdateItem:"+listUpdateItem.size.toString())
        for(uta in listUpdateItem){
            var links = uta.getElementsByTag("a")
            var limit = links[0].getElementsByClass("limit")
            var type = ""
            if(limit.size > 0){
                var span = limit[0].getElementsByTag("span")
                if(span.size > 0){
                    for(it in span){
//                    Log.d("OkCheck","class:"+it.className())
                        if(it.className().contains("type")){
                            type = it.className().replace("type ","")
                            break
                        }
                    }
                }
            }
            var imgSrc = links[0].getElementsByTag("img")[0].attr("src")
//            Log.d("OkCheck", "imgSrc:"+links[0].getElementsByTag("img")[0].toString())
            var title = links[0].attr("title")
//            var chapter = links[0].getElementsByClass("bigor")[0].getElementsByClass("epxs")[0].text()
//            var bigor = links[0].getElementsByClass("bigor")
//            var adds = bigor[0].getElementsByClass("adds")
            var chapter = uta.getElementsByClass("epxs")[0].text()
            var url = links[0].attr("href")
//            Log.d("OkCheck", "url:"+url)

            var com = ComicThumbnail()
            com.id = i.toLong()
            com.title = title
            com.url = url
            com.type = type
            com.imgSrc = imgSrc
            com.lastChap = chapter
            com.urlLastChap = ""
            listThumb.add(com)
            i++
        }
        var qFilter = doc.select(".quickfilter")
        var genres = ArrayList<FilterComic>()
        var types = ArrayList<FilterComic>()
        var orderbys = ArrayList<FilterComic>()
        if(qFilter.size > 0){
            var filters = qFilter[0].getElementsByClass("filter dropdown")
            for(filter in filters){
                var ulGenre = filter.getElementsByClass("dropdown-menu c4 genrez")
                if(ulGenre.size > 0){
                    var lis = ulGenre[0].getElementsByTag("li")
                    for(li in lis){
                        var value = li.getElementsByTag("input").attr("value")
                        var name = li.getElementsByTag("label").text()
                        var filComic = FilterComic()
                        filComic.id = (genres.size+1).toLong()
                        filComic.value = value
                        filComic.name = name
                        genres.add(filComic)
                    }
                }
                var typeText = filter.getElementsByTag("button")[0].text()
                if(typeText.contains("Type",true)){
                    var ulType = filter.getElementsByClass("dropdown-menu c1")
                    if(ulType.size > 0){
                        var lis = ulType[0].getElementsByTag("li")
                        for(li in lis){
                            var value = li.getElementsByTag("input").attr("value")
                            var name = li.getElementsByTag("label").text()
                            var filComic = FilterComic()
                            filComic.id = (types.size+1).toLong()
                            filComic.value = value
                            filComic.name = name
                            types.add(filComic)
                        }
                    }
                }
                if(typeText.contains("Order",true)){
                    var ulOrderBy = filter.getElementsByClass("dropdown-menu c1")
                    var lis = ulOrderBy[0].getElementsByTag("li")
                    for(li in lis){
                        var value = li.getElementsByTag("input").attr("value")
                        var name = li.getElementsByTag("label").text()
                        var filComic = FilterComic()
                        filComic.id = (orderbys.size+1).toLong()
                        filComic.value = value
                        filComic.name = name
                        orderbys.add(filComic)
                    }
                }
            }
        }

        var page1 = "-1"
        var page2 = "-1"
        var page2Url = ""
        var page3 = "-1"
        var pageLast = "-1"
        var pagePrevUrl = ""
        var pageNextUrl = ""

        var mrgn = doc.select(".mrgn")
        if(mrgn.size > 0){
            var checkPage = mrgn[0].getElementsByTag("a")
            for(page in checkPage){
//            Log.d("OkParse","page.text() : "+page.text())
//            Log.d("OkParse","page.attr(href) : "+page.attr("href"))
                if(page.text().contains("Next",true)){
//                Log.d("OkParse","pageNextUrl")
                    pageNextUrl = page.attr("href")
                }
                if(page.text().contains("Prev",true)){
                    pagePrevUrl = page.attr("href")
                }
            }
        }
        var pagination = doc.select(".pagination")
        if(pagination.size > 0){
            var checkPage1 = pagination[0].getElementsByClass("page-numbers current")
            if(checkPage1.size > 0){
                var pages =  pagination[0].getElementsByClass("page-numbers")
                pageLast = pagination[0].getElementsByClass("page-numbers")[pages.size-1].text()
                if(!Util.isNumeric(pageLast)){
                    pageLast = pagination[0].getElementsByClass("page-numbers")[pages.size-2].text()
//                pageLastUrl = pagination[0].getElementsByClass("page-numbers")[pages.size-2].attr("href")
                }
                page1 = pagination[0].getElementsByClass("page-numbers current")[0].text()
                page2 = (page1.toInt()+1).toString()
                page3 = (page2.toInt()+1).toString()
                if(pagination[0].getElementsByClass("page-numbers").size > 1){
                    page2Url = pagination[0].getElementsByClass("page-numbers")[1].attr("href")
//                    Log.d("Okpage2Url",page2Url)
                    if(pagination[0].getElementsByClass("page-numbers").size > 2){
//                    page3Url = pagination[0].getElementsByClass("page-numbers")[2].attr("href")
                    }
                }
                if(page2.toInt() >= pageLast.toInt()){
                    if(!page2.equals(pageLast)){
                        page2="-1"
                        page2Url=""
                    }
                    page3="-1"
//                page3Url=""
                    pageLast="-1"
//                pageLastUrl=""
                }else if(page3.toInt()>= pageLast.toInt()){
                    if(!page3.equals(pageLast)){
                        page3="-1"
//                    page3Url=""
                    }
                    pageLast="-1"
//                pageLastUrl=""
                }
            }
        }

        var homePage = ListComic()
        homePage.page1 = page1
        homePage.page2 = page2
        homePage.page2Url = page2Url
        homePage.page3 = page3
        homePage.pageLast = pageLast
        homePage.pagePrevUrl = pagePrevUrl
        homePage.pageNextUrl = pageNextUrl
        homePage.list = listThumb
        homePage.genres = genres
        homePage.types = types
        homePage.orderbys = orderbys
        return homePage
    }

    fun parsingNgomik(doc: Document): ListComic{
        var listThumb = ArrayList<ComicThumbnail>()
        val listupds = doc.select(".listupd")
        var listUpdateItem = listupds[0].getElementsByClass("bs")
        var i = 0
        for(uta in listUpdateItem){
            var links = uta.getElementsByTag("a")
            var limit = links[0].getElementsByClass("limit")
            var type = ""
            if(limit.size > 0){
                var span = limit[0].getElementsByTag("span")
                if(span.size > 0){
                    for(it in span){
                        Log.d("OkCheck","class:"+it.className())
                        if(it.className().contains("type")){
                            type = it.className().replace("type ","")
                            break
                        }
                    }
                }
            }
            Log.d("OkCheck","Type:"+type)
            var imgSrc = links[0].getElementsByTag("img")[0].attr("src")
            var title = links[0].attr("title")
//            var chapter = links[0].getElementsByClass("bigor")[0].getElementsByClass("epxs")[0].text()
//            var bigor = links[0].getElementsByClass("bigor")
//            var adds = bigor[0].getElementsByClass("adds")
            var chapter = uta.getElementsByClass("epxs")[0].text()
            var url = links[0].attr("href")

            var com = ComicThumbnail()
            com.id = i.toLong()
            com.title = title
            com.url = url
            com.type = type
            com.imgSrc = imgSrc
            com.lastChap = chapter
            com.urlLastChap = ""
            listThumb.add(com)
            i++
        }
        var qFilter = doc.select(".quickfilter")
        var genres = ArrayList<FilterComic>()
        var types = ArrayList<FilterComic>()
        var orderbys = ArrayList<FilterComic>()
        if(qFilter.size > 0){
            var filters = qFilter[0].getElementsByClass("filter dropdown")
            for(filter in filters){
                var ulGenre = filter.getElementsByClass("dropdown-menu c4 genrez")
                if(ulGenre.size > 0){
                    var lis = ulGenre[0].getElementsByTag("li")
                    for(li in lis){
                        var value = li.getElementsByTag("input").attr("value")
                        var name = li.getElementsByTag("label").text()
                        var filComic = FilterComic()
                        filComic.id = (genres.size+1).toLong()
                        filComic.value = value
                        filComic.name = name
                        genres.add(filComic)
                    }
                }
                var typeText = filter.getElementsByTag("button")[0].text()
                if(typeText.contains("Type",true)){
                    var ulType = filter.getElementsByClass("dropdown-menu c1")
                    if(ulType.size > 0){
                        var lis = ulType[0].getElementsByTag("li")
                        for(li in lis){
                            var value = li.getElementsByTag("input").attr("value")
                            var name = li.getElementsByTag("label").text()
                            var filComic = FilterComic()
                            filComic.id = (types.size+1).toLong()
                            filComic.value = value
                            filComic.name = name
                            types.add(filComic)
                        }
                    }
                }
                if(typeText.contains("Order",true)){
                    var ulOrderBy = filter.getElementsByClass("dropdown-menu c1")
                    var lis = ulOrderBy[0].getElementsByTag("li")
                    for(li in lis){
                        var value = li.getElementsByTag("input").attr("value")
                        var name = li.getElementsByTag("label").text()
                        var filComic = FilterComic()
                        filComic.id = (orderbys.size+1).toLong()
                        filComic.value = value
                        filComic.name = name
                        orderbys.add(filComic)
                    }
                }
            }
        }

        var page1 = "-1"
        var page2 = "-1"
        var page2Url = ""
        var page3 = "-1"
        var pageLast = "-1"
        var pagePrevUrl = ""
        var pageNextUrl = ""

        var mrgn = doc.select(".mrgn")
        if(mrgn.size > 0){
            Log.d("OkParse","mrgn: "+mrgn.size.toString())
            var hpage = mrgn[0].lastElementChild()
            Log.d("OkParse","mrgn[0]: "+mrgn[0].lastElementChild()!!.className())
            if(hpage?.className().equals("hpage")){
                var aList = hpage?.getElementsByTag("a")!!
                Log.d("OkParse","aList: "+aList.size.toString())
                for(a in aList){
                    if(a.className().equals("l")){
                        pagePrevUrl = a.attr("href")
                    }else if(a.className().equals("r")){
                        pageNextUrl = a.attr("href")
                    }
                }
            }else{
                var checkPage = mrgn[0].getElementsByTag("a")
                for(page in checkPage){
//            Log.d("OkParse","page.text() : "+page.text())
//            Log.d("OkParse","page.attr(href) : "+page.attr("href"))
                    if(page.text().contains("Next",true)){
//                Log.d("OkParse","pageNextUrl")
                        pageNextUrl = page.attr("href")
                    }
                    if(page.text().contains("Prev",true)){
                        pagePrevUrl = page.attr("href")
                    }
                }

            }
        }


        var pagination = doc.select(".pagination")
        if(pagination.size > 0){
            var checkPage1 = pagination[0].getElementsByClass("page-numbers current")
            if(checkPage1.size > 0){
                var pages =  pagination[0].getElementsByClass("page-numbers")
                pageLast = pagination[0].getElementsByClass("page-numbers")[pages.size-1].text()
                if(!Util.isNumeric(pageLast)){
                    pageLast = pagination[0].getElementsByClass("page-numbers")[pages.size-2].text()
//                pageLastUrl = pagination[0].getElementsByClass("page-numbers")[pages.size-2].attr("href")
                }
                page1 = pagination[0].getElementsByClass("page-numbers current")[0].text()
                page2 = (page1.toInt()+1).toString()
                page3 = (page2.toInt()+1).toString()
                if(pagination[0].getElementsByClass("page-numbers").size > 1){
                    page2Url = pagination[0].getElementsByClass("page-numbers")[1].attr("href")
//                    Log.d("Okpage2Url",page2Url)
                    if(pagination[0].getElementsByClass("page-numbers").size > 2){
//                    page3Url = pagination[0].getElementsByClass("page-numbers")[2].attr("href")
                    }
                }
                if(page2.toInt() >= pageLast.toInt()){
                    if(!page2.equals(pageLast)){
                        page2="-1"
                        page2Url=""
                    }
                    page3="-1"
//                page3Url=""
                    pageLast="-1"
//                pageLastUrl=""
                }else if(page3.toInt()>= pageLast.toInt()){
                    if(!page3.equals(pageLast)){
                        page3="-1"
//                    page3Url=""
                    }
                    pageLast="-1"
//                pageLastUrl=""
                }
            }
        }

        var homePage = ListComic()
        homePage.page1 = page1
        homePage.page2 = page2
        homePage.page2Url = page2Url
        homePage.page3 = page3
        homePage.pageLast = pageLast
        homePage.pagePrevUrl = pagePrevUrl
        homePage.pageNextUrl = pageNextUrl
        homePage.list = listThumb
        homePage.genres = genres
        homePage.types = types
        homePage.orderbys = orderbys
        return homePage
    }

    fun parsingShinigamiId(doc: Document): ListComic{
        var listThumb = ArrayList<ComicThumbnail>()
        val page = doc.select(".page-content-listing")
        var detailManga = doc.select(".page-item-detail")
        if(page.size > 0){
            detailManga = page[0].getElementsByClass("page-item-detail manga")
        }
        Log.d("OkSize","Shinigami:"+detailManga.size.toString())
        var i = 0
        for(uta in detailManga){
            var links = uta.getElementsByClass("item-thumb")[0].getElementsByTag("a")
            var imgSrc = links[0].getElementsByTag("img")[0].attr("data-src")
            Log.d("OkCheck",imgSrc)
            var url = links[0].attr("href")
            Log.d("OkCheck",url)
            var itemSummary = uta.getElementsByClass("item-summary")[0]
            var title = itemSummary.getElementsByTag("h3").text()
//            Log.d("OkCheck",title)
//            var chapter = links[0].getElementsByClass("bigor")[0].getElementsByClass("epxs")[0].text()
//            var bigor = links[0].getElementsByClass("bigor")
//            var adds = bigor[0].getElementsByClass("adds")
            var chapterItem = uta.getElementsByClass("chapter-item")[0].getElementsByClass("chapter")[0]
            var chapter = chapterItem.text()
//            Log.d("OkCheck",chapter)
            var urlLastChap = chapterItem.getElementsByTag("a").attr("href")
//            Log.d("OkCheck",urlLastChap)

            var com = ComicThumbnail()
            com.id = i.toLong()
            com.title = title
            com.url = url
            com.imgSrc = imgSrc
            com.lastChap = chapter
            com.urlLastChap = urlLastChap
            listThumb.add(com)
            i++
        }
        var genres = ArrayList<FilterComic>()
        var types = ArrayList<FilterComic>()
        var orderbys = ArrayList<FilterComic>()
        var genresWrap = doc.select(".genres_wrap")
        if(genresWrap.size > 0){
            var genres__collapse = genresWrap[0].getElementsByClass("genres__collapse")
            var row_genres = genres__collapse[0].getElementsByClass("row genres")
            var ul = row_genres[0].getElementsByClass("list-unstyled")
            var li = ul[0].getElementsByTag("li")
            if(li.size > 0){
                for(item in li){
                    var aLink = item.getElementsByTag("a")
                    if(!aLink.text().contains("(0)")){
                        var href = aLink.attr("href")
                        var splitName = aLink.text().split(" (")
                        var name = splitName[0]
                        var split = href.split("genre/")
                        var value = ""
                        if(split.size > 1){
                            value = split[1].substring(0,split[1].length-1)
                        }
                        var filComic = FilterComic()
                        filComic.id = (genres.size+1).toLong()
                        filComic.value = value
                        filComic.name = name
                        genres.add(filComic)
                    }
                }
            }
        }
//            var filters = qFilter[0].getElementsByClass("filter dropdown")
//            for(filter in filters){
//                var ulGenre = filter.getElementsByClass("dropdown-menu c4 genrez")
//                if(ulGenre.size > 0){
//                    var lis = ulGenre[0].getElementsByTag("li")
//                    for(li in lis){
//                        var value = li.getElementsByTag("input").attr("value")
//                        var name = li.getElementsByTag("label").text()
//                        var filComic = FilterComic()
//                        filComic.id = (genres.size+1).toLong()
//                        filComic.value = value
//                        filComic.name = name
//                        genres.add(filComic)
//                    }
//                }
//                var typeText = filter.getElementsByTag("button")[0].text()
//                if(typeText.contains("Type",true)){
//                    var ulType = filter.getElementsByClass("dropdown-menu c1")
//                    if(ulType.size > 0){
//                        var lis = ulType[0].getElementsByTag("li")
//                        for(li in lis){
//                            var value = li.getElementsByTag("input").attr("value")
//                            var name = li.getElementsByTag("label").text()
//                            var filComic = FilterComic()
//                            filComic.id = (types.size+1).toLong()
//                            filComic.value = value
//                            filComic.name = name
//                            types.add(filComic)
//                        }
//                    }
//                }
//                if(typeText.contains("Order",true)){
//                    var ulOrderBy = filter.getElementsByClass("dropdown-menu c1")
//                    var lis = ulOrderBy[0].getElementsByTag("li")
//                    for(li in lis){
//                        var value = li.getElementsByTag("input").attr("value")
//                        var name = li.getElementsByTag("label").text()
//                        var filComic = FilterComic()
//                        filComic.id = (orderbys.size+1).toLong()
//                        filComic.value = value
//                        filComic.name = name
//                        orderbys.add(filComic)
//                    }
//                }
//            }
//        }

        var page1 = "-1"
        var page2 = "-1"
        var page2Url = ""
        var page3 = "-1"
        var pageLast = "-1"
        var pagePrevUrl = ""
        var pageNextUrl = ""

        var pageNavi = doc.select(".wp-pagenavi")
        if(pageNavi.size > 0){
            var page1 = pageNavi[0].getElementsByClass("current").text()
            var pageLager = pageNavi[0].getElementsByClass("page larger")
            for(lager in pageLager){
                page2
            }
            if(pageLager.size > 0){
                page2 = pageLager[0].text()
                if(pageLager.size > 1){
                    page3 = pageLager[1].text()
                }
            }
            var previouspostslink = pageNavi[0].getElementsByClass("previouspostslink")
            if(previouspostslink.size > 0){
                pagePrevUrl = previouspostslink.attr("href")
            }
            var nextpostslink = pageNavi[0].getElementsByClass("nextpostslink")
            if(nextpostslink.size > 0){
                pageNextUrl = nextpostslink.attr("href")
            }

        }
        var homePage = ListComic()
        homePage.page1 = page1
        homePage.page2 = page2
        homePage.page2Url = page2Url
        homePage.page3 = page3
        homePage.pageLast = pageLast
        homePage.pagePrevUrl = pagePrevUrl
        homePage.pageNextUrl = pageNextUrl
        homePage.list = listThumb
        homePage.genres = genres
        homePage.types = types
        homePage.orderbys = orderbys
        return homePage
    }

    fun parsingShinigamiIdSearch(doc: Document): ListComic{
        var listThumb = ArrayList<ComicThumbnail>()
//        val page = doc.select(".search-wrap")
        var tabsItem = doc.select(".c-tabs-item")
        var list = doc.select(".c-tabs-item__content")
//        var list = doc.select("row c-tabs-item__content")
        if(tabsItem.size > 0){
            list = tabsItem[0].getElementsByClass("row c-tabs-item__content")
        }
        Log.d("OkSize ShinigamiS",list.size.toString())
        var i = 0
        for(uta in list){
            var links = uta.getElementsByClass("tab-thumb")[0].getElementsByTag("a")
            var imgSrc = links[0].getElementsByTag("img")[0].attr("data-src")
//            Log.d("OkCheck",imgSrc)
            var url = links[0].attr("href")
//            Log.d("OkCheck",url)
            var tabSummary = uta.getElementsByClass("tab-summary")[0]
            var postTitle = tabSummary.getElementsByClass("post-title")[0]
            var title = postTitle.getElementsByTag("h3")[0].getElementsByTag("a").text()

            var tabMeta = uta.getElementsByClass("tab-meta")[0]
            var latestChap = tabMeta.getElementsByClass("meta-item latest-chap")[0]
            var chapterItem = latestChap.getElementsByClass("font-meta chapter")[0]
            var chapter = chapterItem.getElementsByTag("a").text()
//            Log.d("OkCheck",chapter)
            var urlLastChap = chapterItem.getElementsByTag("a").attr("href")
//            Log.d("OkCheck",urlLastChap)

            var com = ComicThumbnail()
            com.id = i.toLong()
            com.title = title
            com.url = url
            com.imgSrc = imgSrc
            com.lastChap = chapter
            com.urlLastChap = urlLastChap
            listThumb.add(com)
            i++
        }
//        var qFilter = doc.select(".quickfilter")
        var genres = ArrayList<FilterComic>()
        var types = ArrayList<FilterComic>()
        var orderbys = ArrayList<FilterComic>()
//        if(qFilter.size > 0){
//            var filters = qFilter[0].getElementsByClass("filter dropdown")
//            for(filter in filters){
//                var ulGenre = filter.getElementsByClass("dropdown-menu c4 genrez")
//                if(ulGenre.size > 0){
//                    var lis = ulGenre[0].getElementsByTag("li")
//                    for(li in lis){
//                        var value = li.getElementsByTag("input").attr("value")
//                        var name = li.getElementsByTag("label").text()
//                        var filComic = FilterComic()
//                        filComic.id = (genres.size+1).toLong()
//                        filComic.value = value
//                        filComic.name = name
//                        genres.add(filComic)
//                    }
//                }
//                var typeText = filter.getElementsByTag("button")[0].text()
//                if(typeText.contains("Type",true)){
//                    var ulType = filter.getElementsByClass("dropdown-menu c1")
//                    if(ulType.size > 0){
//                        var lis = ulType[0].getElementsByTag("li")
//                        for(li in lis){
//                            var value = li.getElementsByTag("input").attr("value")
//                            var name = li.getElementsByTag("label").text()
//                            var filComic = FilterComic()
//                            filComic.id = (types.size+1).toLong()
//                            filComic.value = value
//                            filComic.name = name
//                            types.add(filComic)
//                        }
//                    }
//                }
//                if(typeText.contains("Order",true)){
//                    var ulOrderBy = filter.getElementsByClass("dropdown-menu c1")
//                    var lis = ulOrderBy[0].getElementsByTag("li")
//                    for(li in lis){
//                        var value = li.getElementsByTag("input").attr("value")
//                        var name = li.getElementsByTag("label").text()
//                        var filComic = FilterComic()
//                        filComic.id = (orderbys.size+1).toLong()
//                        filComic.value = value
//                        filComic.name = name
//                        orderbys.add(filComic)
//                    }
//                }
//            }
//        }

        var page1 = "-1"
        var page2 = "-1"
        var page2Url = ""
        var page3 = "-1"
        var pageLast = "-1"
        var pagePrevUrl = ""
        var pageNextUrl = ""

        var pageNavi = doc.select(".wp-pagenavi")
        if(pageNavi.size > 0){
            var page1 = pageNavi[0].getElementsByClass("current").text()
            var pageLager = pageNavi[0].getElementsByClass("page larger")
            for(lager in pageLager){
                page2
            }
            if(pageLager.size > 0){
                page2 = pageLager[0].text()
                if(pageLager.size > 1){
                    page3 = pageLager[1].text()
                }
            }
            var previouspostslink = pageNavi[0].getElementsByClass("previouspostslink")
            if(previouspostslink.size > 0){
                pagePrevUrl = previouspostslink.attr("href")
            }
            var nextpostslink = pageNavi[0].getElementsByClass("nextpostslink")
            if(nextpostslink.size > 0){
                pageNextUrl = nextpostslink.attr("href")
            }

        }
        var homePage = ListComic()
        homePage.page1 = page1
        homePage.page2 = page2
        homePage.page2Url = page2Url
        homePage.page3 = page3
        homePage.pageLast = pageLast
        homePage.pagePrevUrl = pagePrevUrl
        homePage.pageNextUrl = pageNextUrl
        homePage.list = listThumb
        homePage.genres = genres
        homePage.types = types
        homePage.orderbys = orderbys
        return homePage
    }

}
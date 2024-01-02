package mapan.prototype.mapanbacakomik.config


import mapan.prototype.mapanbacakomik.BuildConfig
import java.util.*

class Constants {
    companion object {
        val IS_DEBUG = getIsDebug(true)
        val IS_LOG: Boolean = getIsLog()
        val IMAGE_GALLERY_REQUEST_CODE = 1000

        val ROOT_DIR = "Mapan Baca Komik"

        val CONNECT_TIMEOUT : Long = 60
        val READ_TIMEOUT: Long = 60
        val WRITE_TIMEOUT: Long = 60
        val IMG_CONNECT_TIMEOUT : Long = 10000
        val IMG_READ_TIMEOUT: Long = 10000
        val IMG_WRITE_TIMEOUT: Long = 10000

        fun getRecentURL(url : String) : String {
            var result = url
            if(result.contains("komikcast.")){
                result = getKomikcastURL(url)
            }else if(result.contains("westmanga.")){
                result = url
            }else if(result.contains("ngomik.")){
                result = url
            }else if(result.contains("shinigami.")){
                result = getShinigamiURL(url)
            }
            return result
        }

        fun getKomikcastURL(url : String) : String{
            var result = url
            if(result.contains("komikcast.")){
                var split = result.split("komikcast.")
                if(split.size > 0){
                    var split2 = split[1].split("/")
                    if(split2.size > 0){
                        if(split2[0].equals("lol")){
                            result = url
                        }else{
                            result = split[0]+"komikcast.lol"
                            for(i in 1 .. split2.size-1){
                                result += "/"+split2[i]
                            }
                        }
                    }
                }

            }
            return result
        }
        fun getShinigamiURL(url : String) : String{
            var result = url
            if(result.contains("shinigami.")){
                var split = result.split("shinigami.")
                if(split.size > 0){
                    var split2 = split[1].split("/")
                    if(split2.size > 0){
                        if(split2[0].equals("moe")){
                            result = url
                        }else{
                            result = split[0]+"shinigami.moe"
                            for(i in 1 .. split2.size-1){
                                result += "/"+split2[i]
                            }
                        }
                    }
                }

            }
            return result
        }


        fun getIsDebug(isDebug : Boolean): Boolean {
            return  if (!BuildConfig.DEBUG) {
                false
            } else {
                isDebug
            }
        }
        fun getIsLog(): Boolean {
//            return true
            return  if (!BuildConfig.DEBUG) {
                false
            } else {
                true
            }
        }

        fun getListExtentionImage() : ArrayList<String>{
            var list = ArrayList<String>()
            list.add("jpg")
            list.add("jpeg")
            list.add("png")

            return list
        }
    }

}
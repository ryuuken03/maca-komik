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
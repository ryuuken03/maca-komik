package mapan.prototype.mapanbacakomik.util.shared

import android.content.Context
import android.content.SharedPreferences
import java.io.Serializable


open class SourceWebsitePreference(context: Context) : Serializable {
    private val KEY_ID = "ID"
    private val KEY_SOURCE_WEBSITE = "SOURCE_WEBSITE"

    private var pref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    init {
        pref = context.getSharedPreferences("SOURCE_WEBSITE_PREF", Context.MODE_PRIVATE)
    }

    open fun updateSource(source: Int) {
        editor = pref!!.edit()
        editor?.putString(KEY_ID, "1")
        editor?.putInt(KEY_SOURCE_WEBSITE, source)
        editor?.commit()
    }

    open fun getSource(): Int {
        val source = pref!!.getInt(KEY_SOURCE_WEBSITE, 0)
        return source
    }

    open fun deleteData() {
        editor = pref!!.edit()
        editor?.clear()
        editor?.commit()
    }
}
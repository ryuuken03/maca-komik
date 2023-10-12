package mapan.prototype.mapanbacakomik.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager.BadTokenException
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import mapan.prototype.mapanbacakomik.R
import java.net.SocketTimeoutException


abstract class BaseActivity : AppCompatActivity(), InitializerUi {
    var dialog: AlertDialog? = null

    open fun checkDialogDismissable(dialogx: AlertDialog?) {
        try {
            if (dialogx != null) {
                if (dialogx!!.isShowing) {
                    dialogx!!.dismiss()
                }
            }
        } catch (e: BadTokenException) {
        } catch (ex: Exception) {
        }
    }

    open fun showLoading() {
        try {
            dialog = AlertDialog.Builder(this).create()
            val inflater = layoutInflater
            val alertLayout: View = inflater.inflate(R.layout.custom_dialog_progress, null)
            val textDesc: TextView = alertLayout.findViewById(R.id.textDesc)
            textDesc.text = getString(R.string.text_loading)

            dialog!!.setView(alertLayout)
            dialog!!.setCancelable(false)

            dialog!!.show()
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } catch (e: BadTokenException) {
        } catch (x: Exception) {
        }
    }

    open fun showAlert(title_text: String?, alert_text: String, listener: View.OnClickListener?) {
        try {
            dialog = AlertDialog.Builder(this).create()
            val inflater = layoutInflater
            val alertLayout: View = inflater.inflate(R.layout.custom_dialog_alert, null)
            val textTitle = alertLayout.findViewById(R.id.textTitle) as TextView
            val textDesc = alertLayout.findViewById(R.id.textDesc) as TextView
            val buttonSend = alertLayout.findViewById(R.id.buttonSend) as TextView
            val buttonCancel = alertLayout.findViewById(R.id.buttonCancel) as TextView

            buttonSend.text = resources.getString(R.string.text_ok)
            textTitle.text = title_text
            textDesc.text = alert_text

            dialog!!.setView(alertLayout)
            dialog!!.setCancelable(false)

            buttonSend.setOnClickListener(listener)
            buttonCancel.visibility = View.GONE

            dialog!!.show()
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } catch (e: BadTokenException) {
        } catch (x: Exception) {
        }
    }

    open fun showPrompt(title_text: String?, prompt_text: String, listener: View.OnClickListener?) {
        try {
            dialog = AlertDialog.Builder(this).create()
            val inflater = layoutInflater
            val alertLayout: View = inflater.inflate(R.layout.custom_dialog_alert, null)
            val textTitle = alertLayout.findViewById(R.id.textTitle) as TextView
            val textDesc = alertLayout.findViewById(R.id.textDesc) as TextView
            val buttonSend = alertLayout.findViewById(R.id.buttonSend) as TextView
            val buttonCancel = alertLayout.findViewById(R.id.buttonCancel) as TextView

            buttonSend.text = resources.getString(R.string.text_ok)
            textDesc.text = prompt_text
            textTitle.text = title_text

            dialog!!.setView(alertLayout)
            dialog!!.setCancelable(false)

            buttonSend.setOnClickListener(listener)
            buttonCancel.setOnClickListener { dialog!!.dismiss() }

            dialog!!.show()
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } catch (e: BadTokenException) {
        } catch (x: Exception) {
        }
    }

    open fun showPromptFailure(
        title_text: String,
        prompt_text: String,
        button_action: String?,
        listener: View.OnClickListener?,
        listener2: View.OnClickListener?
    ) {
        try {
            dialog = AlertDialog.Builder(this).create()
            val inflater = layoutInflater
            val alertLayout: View = inflater.inflate(R.layout.custom_dialog_alert_failure, null)
            dialog!!.setCancelable(false)
            val textTitle = alertLayout.findViewById(R.id.textTitle) as TextView
            val textDesc = alertLayout.findViewById(R.id.textDesc) as TextView
            val buttonSend = alertLayout.findViewById(R.id.buttonSend) as TextView
            val buttonCancel = alertLayout.findViewById(R.id.buttonCancel) as TextView

            buttonSend.text = button_action
            textTitle.text = title_text
            textDesc.text = prompt_text

            dialog!!.setView(alertLayout)
            buttonSend.setOnClickListener(listener)
            buttonCancel.setOnClickListener(listener2)

            dialog!!.show()
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } catch (e: BadTokenException) {
        } catch (x: Exception) {
        } catch (z: IllegalArgumentException) {
        }
    }

    open fun handleLoadFailure(
        t: Throwable,
        listener: View.OnClickListener?,
        listener2: View.OnClickListener?
    ) {
        t.printStackTrace()
        if (t is SocketTimeoutException) {
            showPromptFailure(
                getString(R.string.text_warning),
                getString(R.string.error_rto),
                getString(R.string.action_try_again),
                listener,
                listener2
            )
        } else {
            showPromptFailure(
                getString(R.string.text_warning),
                getString(R.string.error_server),
                getString(R.string.action_try_again),
                listener,
                listener2
            )
        }
    }

    open fun showCustomToast(
        context: Context,
        text: String,
        isFailure: Boolean,
        marginBottoms: Int
    ) {
        val inflater = layoutInflater

        val layout: View = inflater.inflate(
            R.layout.custom_toast,
            null
        )
        val root = layout.findViewById(R.id.root) as LinearLayout
        val textToast = layout.findViewById(R.id.text) as TextView

        textToast.text = text
        if (isFailure) {
            root.setBackgroundResource(R.drawable.bg_8dp_red)
        } else {
            root.setBackgroundResource(R.drawable.bg_8dp_grey)
        }

        val toast = Toast(applicationContext)
        val marginBottom: Int = Util.convertDpToPx(marginBottoms, context)
        //        toast.setGravity(Gravity.BOTTOM, 0, marginBottom);
        toast.setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM, marginBottom, marginBottom);
//        toast.setGravity(Gravity.BOTTOM, marginBottom, marginBottom);
//        toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL or Gravity.LEFT, 0, 0)
//        toast.setMargin(0f, 0f)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()

    }

    open fun showCustomSnackbarColor(
        context: Context,
        view: View,
        text: String,
        resouceDrawable: Int = R.drawable.container_snackbar_black,
        longTime: Int = Snackbar.LENGTH_LONG
    ) {

        val snackbar = Snackbar.make(view, text, longTime)
        snackbar.view.background = ContextCompat.getDrawable(applicationContext, resouceDrawable)
        snackbar.show()
    }


    open fun showCustomSnackbar(
        context: Context,
        view: View?,
        text: String,
        isFailure: Boolean,
        marginBottoms: Int
    ) {
        try {
            if(view != null){
                val marginBottom: Int = Util.convertDpToPx(marginBottoms, context)
                val marginLeftRight = Util.convertDpToPx(10, context)

                val snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT)
                if (isFailure) {
//            snackbar.view.setBackgroundResource(R.drawable.rounded_bg_8dp_red_dark)
                    snackbar.view.background =
                        ContextCompat.getDrawable(applicationContext, R.drawable.container_snackbar_failure)
                } else {
//            snackbar.view.setBackgroundResource(R.drawable.rounded_bg_8dp_grey_dark)
                    snackbar.view.background =
                        ContextCompat.getDrawable(applicationContext, R.drawable.container_snackbar)
                }
                snackbar.show()
            }
        }catch (e: Exception){}
    }

    open fun showCustomSnackbarFaster(
        context: Context,
        view: View?,
        text: String,
        isFailure: Boolean,
        marginBottoms: Int
    ) {
        try {
            if(view != null){
                val marginBottom: Int = Util.convertDpToPx(marginBottoms, context)
                val marginLeftRight = Util.convertDpToPx(10, context)

                val snackbar = Snackbar.make(view, text, 500)
                if (isFailure) {
//            snackbar.view.setBackgroundResource(R.drawable.rounded_bg_8dp_red_dark)
                    snackbar.view.background =
                        ContextCompat.getDrawable(applicationContext, R.drawable.container_snackbar_failure)
                } else {
//            snackbar.view.setBackgroundResource(R.drawable.rounded_bg_8dp_grey_dark)
                    snackbar.view.background =
                        ContextCompat.getDrawable(applicationContext, R.drawable.container_snackbar)
                }

                snackbar.show()
            }
        }catch (e: Exception){}
    }

}
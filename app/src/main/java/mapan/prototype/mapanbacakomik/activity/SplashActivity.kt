package mapan.prototype.mapanbacakomik.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import mapan.prototype.mapanbacakomik.databinding.ActivitySplashBinding
import mapan.prototype.mapanbacakomik.util.BaseActivity

class SplashActivity : BaseActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initConfig()
    }

    override fun initConfig() {
        initUI()
    }

    override fun initUI() {
        Handler().postDelayed(Runnable {

//            var intent = Intent(this@SplashActivity, ListComicActivity::class.java)
            var intent = Intent(this@SplashActivity, HomeActivity::class.java)

//            var intent = Intent(this@SplashActivity, ListComicHiddenActivity::class.java)
            startActivity(intent)
            finish()
        },2000)

        setListener()
    }

    override fun setListener() {
    }
}
package com.sophisticated.person.under.sun.ui.show.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.ling.ding.sighed.adtool.ShowDataTool
import com.ling.ding.sighed.limt.AdLimiter
import com.ling.ding.sighed.limt.DailyClickLimitRule
import com.ling.ding.sighed.limt.DailyShowLimitRule
import com.ling.ding.sighed.limt.HourlyShowLimitRule
import com.ling.ding.sighed.limt.TimeManager
import com.ling.ding.sighed.txtdata.LocalStorage
import com.ling.ding.sighed.txtmain.FirstRunFun.mainStart
import com.sophisticated.person.under.sun.R
import com.sophisticated.person.under.sun.databinding.ActivityMainBinding
import com.sophisticated.person.under.sun.ui.show.plus.AddActivity
import com.sophisticated.person.under.sun.ui.show.we.WeightActivity
import com.sophisticated.person.under.sun.utils.BmiUtils
import com.sophisticated.person.under.sun.utils.BmiUtils.navigateTo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        clickEvent()
//        setAdCanShow()
    }

    private fun clickEvent() {
        onBackPressedDispatcher.addCallback(this) {
            if (binding.dlHome.isOpen) {
                binding.dlHome.close()
                return@addCallback
            }
            finish()
            exitProcess(-1)
        }
        binding.imgBmi.setOnClickListener {
            navigateTo<AddActivity>()
        }
        binding.imgWe.setOnClickListener {
            navigateTo<WeightActivity>()
        }
        binding.aivMenu.setOnClickListener {
            binding.dlHome.open()
        }


        binding.tvShare.setOnClickListener {
            BmiUtils.shareText(this)
        }
        binding.tvRate.setOnClickListener {
            startActivity(Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=${this.packageName}")))
        }
        binding.tvPrivacyPolicy.setOnClickListener {
            startActivity(
                Intent(
                    "android.intent.action.VIEW", Uri.parse("https://sites.google.com/view/bmi-record-app/home")
                )
            )
        }
    }
    private fun setAdCanShow() {
        lifecycleScope.launch {
            while (true) {
                val adminData = ShowDataTool.getAdminData()
                val data = try {
                    adminData?.assetConfig?.identifiers?.linkData?: ""
                } catch (e: Exception) {
                    ""
                }
                if (data.isEmpty()) {
                    binding.linAd.visibility = View.GONE
                } else {
                    binding.linAd.visibility = View.VISIBLE
                    return@launch
                }
                delay(1100)
            }
        }
        binding.linAd.setOnClickListener {
            val adminData = ShowDataTool.getAdminData()
            val https = try {
                adminData?.assetConfig?.identifiers?.linkData?: ""
            } catch (e: Exception) {
                ""
            }
            this.startActivity(Intent.parseUri(https, Intent.URI_INTENT_SCHEME))
        }
    }
}
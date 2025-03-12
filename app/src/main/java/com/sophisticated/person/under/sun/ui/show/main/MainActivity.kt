package com.sophisticated.person.under.sun.ui.show.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sophisticated.person.under.sun.R
import com.sophisticated.person.under.sun.databinding.ActivityMainBinding
import com.sophisticated.person.under.sun.ui.show.plus.AddActivity
import com.sophisticated.person.under.sun.ui.show.we.WeightActivity
import com.sophisticated.person.under.sun.utils.BmiUtils
import com.sophisticated.person.under.sun.utils.BmiUtils.navigateTo
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
}
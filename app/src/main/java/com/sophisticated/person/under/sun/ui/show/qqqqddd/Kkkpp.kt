package com.sophisticated.person.under.sun.ui.show.qqqqddd

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sophisticated.person.under.sun.R
import kotlinx.coroutines.*
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import com.sophisticated.person.under.sun.databinding.ActivityKpBinding
import com.sophisticated.person.under.sun.ui.show.main.MainActivity
import com.sophisticated.person.under.sun.utils.BmiUtils.navigateTo
import com.sophisticated.person.under.sun.utils.ImageRotationUtil

class Kkkpp : AppCompatActivity() {
    private lateinit var binding: ActivityKpBinding
    val rotationUtil = ImageRotationUtil()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityKpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        lifecycleScope.launch {
            startProgressBar()
        }
        onBackPressedDispatcher.addCallback {
        }
    }

    // Usage example
    private suspend fun startProgressBar() {
       rotationUtil.rotateImageSmoothly(
            imageView = binding.imgBar,
            duration = 1000,
            clockwise = true
        )
        delay(3000)
        toMain()
    }

    private fun toMain() {
        navigateTo<MainActivity>(finishCurrent = true)
    }

}
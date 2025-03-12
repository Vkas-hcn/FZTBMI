package com.sophisticated.person.under.sun.ui.show.resu

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.sophisticated.person.under.sun.R
import com.sophisticated.person.under.sun.databinding.ActivityResultBinding
import com.sophisticated.person.under.sun.cc.BmiApp

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var viewModel: BmiResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupViewModel()
        setupListeners()
        processIntentData()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[BmiResultViewModel::class.java]

        // 观察BMI结果数据变化
        viewModel.bmiResultData.observe(this) { resultData ->
            updateUI(resultData)
        }
    }

    private fun setupListeners() {
        binding.imgBack.setOnClickListener {
            finish()
        }
    }

    private fun processIntentData() {
        val weight = intent.getStringExtra("weight") ?: ""
        val height = intent.getStringExtra("height") ?: ""
        val remark = intent.getStringExtra("remark") ?: ""
        val timestamp = intent.getLongExtra("timestamp", 0)

        viewModel.processBmiData(height, weight, remark, timestamp)
    }

    private fun updateUI(resultData: BmiResultData) {
        with(binding) {
            // 显示BMI值和状态
            tvBmiValue.text = resultData.bmiValue
            tvBmiValue.setTextColor(ContextCompat.getColor(BmiApp.appContext, resultData.stateColorResId))

            // 显示BMI状态文本
            tvState.text = resultData.bmiState
            tvState.setTextColor(ContextCompat.getColor(BmiApp.appContext, resultData.stateColorResId))

            // 设置状态颜色标识
            colorState = resultData.bmiStateCode

            // 显示建议文本
            tvJi.text = getText(resultData.recommendationResId)

            // 显示日期和备注
            tvDate.text = resultData.formattedDate
            tvNotes.text = resultData.remark
            llNote.isVisible = resultData.remark.isNotBlank()
        }
    }
}
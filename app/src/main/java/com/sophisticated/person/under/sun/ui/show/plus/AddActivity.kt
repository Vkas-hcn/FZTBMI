package com.sophisticated.person.under.sun.ui.show.plus

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sophisticated.person.under.sun.R
import com.sophisticated.person.under.sun.data.show.BmiBean
import com.sophisticated.person.under.sun.databinding.ActivityAddBinding
import com.sophisticated.person.under.sun.ui.show.resu.ResultActivity
import com.sophisticated.person.under.sun.utils.BmiUtils
import com.sophisticated.person.under.sun.utils.BmiUtils.navigateTo



import androidx.activity.viewModels
import com.sophisticated.person.under.sun.utils.BmiUtils.showToast


class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private val viewModel: AddViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWindowInsets()
        initUI()
        observeViewModel()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initUI() {
        binding.imgBack.setOnClickListener { finish() }
        binding.tvInquire.setOnClickListener { saveBmiRecord() }
        binding.llWeight.setOnClickListener { BmiUtils.showKeyboard(binding.aetWe) }
        binding.llHeight.setOnClickListener { BmiUtils.showKeyboard(binding.aetHe) }
    }

    private fun saveBmiRecord() {
        val weight = binding.aetWe.text?.trim().toString()
        val height = binding.aetHe.text?.trim().toString()
        val notes = binding.aetNotes.text?.trim().toString()

        if (!isInputValid(weight, height)) {
            showToast(getString(R.string.error_invalid_input))
            return
        }

        val bmiRecord = BmiBean(
            weight = weight,
            height = height,
            remark = notes,
            timestamp = System.currentTimeMillis()
        )

        viewModel.saveBmiRecord(bmiRecord)
    }

    private fun isInputValid(weight: String, height: String): Boolean {
        return weight.isNotBlank() && height.isNotBlank() &&
                BmiUtils.isValidNumber(weight) && BmiUtils.isValidNumber(height)
    }

    private fun observeViewModel() {
        viewModel.navigateToResult.observe(this) { bmiRecord ->
            navigateTo<ResultActivity>(finishCurrent = true) {
                putExtra("weight", bmiRecord.weight)
                putExtra("height", bmiRecord.height)
                putExtra("remark", bmiRecord.remark)
                putExtra("timestamp", bmiRecord.timestamp)
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            showToast(message)
        }
    }
}
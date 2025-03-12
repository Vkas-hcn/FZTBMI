package com.sophisticated.person.under.sun.ui.show.we

import android.os.Bundle
import android.text.InputFilter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.sophisticated.person.under.sun.R
import com.sophisticated.person.under.sun.data.show.UserWeightBean
import com.sophisticated.person.under.sun.databinding.ActivityWeightBinding
import com.sophisticated.person.under.sun.utils.BmiUtils
import java.util.Locale
import androidx.lifecycle.ViewModelProvider


class WeightActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeightBinding
    private lateinit var adapter: WeightAdapter
    private lateinit var viewModel: WeightViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWeightBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWindowInsets()

        // 初始化ViewModel
        viewModel = ViewModelProvider(this, WeightViewModelFactory())[WeightViewModel::class.java]

        setupListeners()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupListeners() {
        with(binding) {
            imgBack.setOnClickListener { finish() }
            imgCheck.text = BmiUtils.getUnitData()
            imgCheck.setOnClickListener {
                viewModel.toggleUnits()
                imgCheck.text = BmiUtils.getUnitData()
            }

            imgAdd.setOnClickListener {
                showAddWeightDialog()
            }

            conAddDialog.setOnClickListener {
                hideWeightDialog()
            }

            llDialog.setOnClickListener {
                // 阻止点击传递到背景
            }

            tvSave.setOnClickListener {
                saveWeightData()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = WeightAdapter(mutableListOf())
        binding.rvHis.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvHis.adapter = adapter

        adapter.setOnItemClickListener(object : WeightAdapter.OnItemClickListener {
            override fun onItemClick(bean: UserWeightBean) {
                showEditWeightDialog(bean)
            }

            override fun onItemDeleteClick(bean: UserWeightBean, position: Int) {
                showDeleteDialog(bean)
            }
        })
    }
    fun showDeleteDialog( bean: UserWeightBean) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Delete Record") // 设置标题
            .setMessage("Are you sure you want to delete this record?") // 设置提示信息
            .setPositiveButton("Yes") { dialog, which ->
                // 点击“确定”按钮时调用 deleteWeightRecord 方法
                viewModel.deleteWeightRecord(bean)
            }
            .setNegativeButton("No", null) // 点击“取消”按钮时不执行任何操作
            .create()

        alertDialog.show() // 显示弹框
    }
    private fun observeViewModel() {
        // 观察体重记录列表的变化
        viewModel.weightRecords.observe(this) { records ->
            adapter.upDataListData(records)
            updateStatsDisplay(records)
        }

        // 观察当前单位的变化
        viewModel.isKiloUnit.observe(this) { isKiloUnit ->
            updateUnitDisplay(isKiloUnit)
        }

        // 观察消息提示
        viewModel.toastMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        // 初始加载数据
        viewModel.loadWeightRecords()
    }

    private fun updateStatsDisplay(records: List<UserWeightBean>) {
        binding.noRecord = records.isEmpty()

        val stats = viewModel.calculateWeightStats(records)

        with(binding) {
            tvChData.text = String.format(Locale.US, "%.2f", stats["Change"])
            tvCurrentData.text = String.format(Locale.US, "%.2f", stats["Current"])
            tvStartData.text = String.format(Locale.US, "%.2f", stats["Start"])
            tvAverageData.text = String.format(Locale.US, "%.2f", stats["Average"])
        }
    }

    private fun updateUnitDisplay(isKiloUnit: Boolean) {
        binding.appUn.text = BmiUtils.getUnitData()

        // 更新输入框长度限制
        val maxLength = if (isKiloUnit) 6 else 3
        binding.aetWe.filters = arrayOf(InputFilter.LengthFilter(maxLength))
    }

    private fun showAddWeightDialog() {
        resetDialogFields()
        viewModel.setEditingTimestamp(0L)
        binding.tvSave.isVisible = true
        binding.tvSave.isVisible = true
        binding.aetWe.isFocusable = true
        binding.aetWe.setFocusableInTouchMode(true)
        binding.edNotes.isFocusable = true
        binding.edNotes.setFocusableInTouchMode(true)
        binding.llNote.isVisible = true
        binding.conAddDialog.isVisible = true
        focusWeightField()
    }

    private fun showEditWeightDialog(bean: UserWeightBean) {
        resetDialogFields()
        binding.tvSave.isVisible = false
        binding.aetWe.isFocusable = false
        binding.aetWe.setFocusableInTouchMode(false)
        binding.edNotes.isFocusable = false
        binding.edNotes.setFocusableInTouchMode(false)
        binding.conAddDialog.isVisible = true
        viewModel.setEditingTimestamp(bean.timestamp)
        binding.llNote.isVisible = bean.remark.isNotEmpty()
        binding.conAddDialog.isVisible = true
        binding.aetWe.setText(bean.weight)
        binding.edNotes.setText(bean.remark)
    }

    private fun resetDialogFields() {
        binding.aetWe.setText("")
        binding.edNotes.setText("")
        updateUnitDisplay(viewModel.isKiloUnit.value ?: false)
    }

    private fun focusWeightField() {
        binding.aetWe.apply {
            setFocusableInTouchMode(true)
            isFocusable = true
            requestFocus()
        }
        BmiUtils.showKeyboard(binding.aetWe)
    }

    private fun saveWeightData() {
        val weightText = binding.aetWe.text.toString().trim()
        val remark = binding.edNotes.text.toString()

        if (viewModel.saveWeightRecord(weightText, remark)) {
            hideWeightDialog()
        }
    }

    private fun hideWeightDialog() {
        binding.conAddDialog.isVisible = false
        BmiUtils.hideKeyboard(binding.aetWe)
        BmiUtils.hideKeyboard(binding.edNotes)
    }
}
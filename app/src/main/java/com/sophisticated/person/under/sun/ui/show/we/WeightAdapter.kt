package com.sophisticated.person.under.sun.ui.show.we

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sophisticated.person.under.sun.R
import com.sophisticated.person.under.sun.data.show.UserWeightBean
import com.sophisticated.person.under.sun.utils.BmiUtils
import java.util.Locale

class WeightAdapter(private var itemList: MutableList<UserWeightBean>) : RecyclerView.Adapter<WeightAdapter.MyViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(bean: UserWeightBean)
        fun onItemDeleteClick(bean: UserWeightBean, position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWeDate: TextView = itemView.findViewById(R.id.tv_we_date)
        val tvWeDate2: TextView = itemView.findViewById(R.id.tv_we_date_2)
        val tvWeDesc: TextView = itemView.findViewById(R.id.tv_we_desc)
        val tvWeValue: TextView = itemView.findViewById(R.id.tv_we_value)
        val imgNote: ImageView = itemView.findViewById(R.id.img_note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_weight_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bean = itemList[position]
        holder.tvWeValue.text = String.format(Locale.US,"%.2f",bean.weight.toDoubleOrNull())
        holder.tvWeDate.text =BmiUtils.formatTimestamp1(bean.timestamp)
        holder.tvWeDate2.text =BmiUtils.formatTimestamp2(bean.timestamp)
        holder.imgNote.isVisible = bean.remark.isNotBlank()
        holder.tvWeDesc.text = BmiUtils.getUnitData()
        holder.itemView.setOnLongClickListener {
            if (position != RecyclerView.NO_POSITION) {
                listener?.onItemDeleteClick(bean, position)
            }
            true
        }
        holder.itemView.setOnClickListener {
            listener?.onItemClick(bean)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }



    fun upDataListData(listData:MutableList<UserWeightBean>){
        itemList.clear()
        itemList = listData
        notifyDataSetChanged()
    }
}

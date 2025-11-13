package com.skd.pgmanagement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skd.pgmanagement.databinding.ItemHomeListBinding
import com.skd.pgmanagement.networks.dataModel.ActivityData

class HomeCategoryAdapter(private val items: List<ActivityData>, private val groupId: String?) :
    RecyclerView.Adapter<HomeCategoryAdapter.HomeCategoryViewHolder>() {

    inner class HomeCategoryViewHolder(val binding: ItemHomeListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCategoryViewHolder {
        val binding = ItemHomeListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HomeCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeCategoryViewHolder, position: Int) {
        val item = items[position]
//        holder.binding.tvActivityName.text = item.activity

        holder.binding.rvActivityName.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = FeatureAdapter(item.featureIcons, groupId)
        }
    }

    override fun getItemCount(): Int = items.size
}

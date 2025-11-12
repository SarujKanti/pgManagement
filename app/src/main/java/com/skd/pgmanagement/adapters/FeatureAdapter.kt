package com.skd.pgmanagement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skd.pgmanagement.databinding.ItemHomeListFeaturesBinding
import com.skd.pgmanagement.networks.dataModel.FeatureIcon

class FeatureAdapter(private val features: List<FeatureIcon>) :
    RecyclerView.Adapter<FeatureAdapter.FeatureViewHolder>() {

    inner class FeatureViewHolder(val binding: ItemHomeListFeaturesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {
        val binding = ItemHomeListFeaturesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FeatureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeatureViewHolder, position: Int) {
        val feature = features[position]
        holder.binding.featureName.text = feature.name

        Glide.with(holder.itemView.context)
            .load(feature.image)
            .into(holder.binding.featureIcon)

        holder.binding.root.setOnClickListener {
            handleFeatureClick(feature)
        }

    }
    private fun handleFeatureClick(featureIcon: FeatureIcon){

    }

    override fun getItemCount(): Int = features.size
}

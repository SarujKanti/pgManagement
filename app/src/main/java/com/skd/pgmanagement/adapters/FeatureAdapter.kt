package com.skd.pgmanagement.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skd.pgmanagement.activities.staffRegister.StaffRegisterActivity
import com.skd.pgmanagement.constants.StringConstants
import com.skd.pgmanagement.databinding.ItemHomeListFeaturesBinding
import com.skd.pgmanagement.networks.dataModel.FeatureIcon

/**
 * Adapter for displaying feature icons inside an activity.
 */
class FeatureAdapter(
    private val features: List<FeatureIcon>,
    private val groupId: String?
) : RecyclerView.Adapter<FeatureAdapter.FeatureViewHolder>() {

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
        with(holder.binding) {
            featureName.text = feature.name

            Glide.with(root.context)
                .load(feature.image)
                .into(featureIcon)

            root.setOnClickListener {
                navigateToFeature(feature, root.context)
            }
        }
    }

    /**
     * Handles navigation based on feature type.
     */
    private fun navigateToFeature(feature: FeatureIcon, context: Context) {
        val intent: Intent? = when (feature.type) {
            StringConstants.STAFF_REGISTER ->
                Intent(context, StaffRegisterActivity::class.java).apply {
                putExtra(StringConstants.GROUP_ID, groupId)
                putExtra(StringConstants.STAFF_REGISTER, true)
            }
            else -> null
        }

        intent?.let { context.startActivity(it) }
    }

    override fun getItemCount(): Int = features.size
}

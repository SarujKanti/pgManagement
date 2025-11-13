package com.skd.pgmanagement.fragments.staffRegister

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.skd.pgmanagement.R
import com.skd.pgmanagement.activities.staffRegister.StaffRegisterActivity
import com.skd.pgmanagement.adapters.GenericAdapter
import com.skd.pgmanagement.constants.StringConstants
import com.skd.pgmanagement.databinding.CommonFragmentBinding
import com.skd.pgmanagement.databinding.ItemUserDetailsBinding
import com.skd.pgmanagement.networks.RetrofitClient
import com.skd.pgmanagement.utils.showToast
import com.skd.pgmanagement.views.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StaffRegisterFragment : BaseFragment<CommonFragmentBinding>(R.layout.common_fragment) {
    override lateinit var binding: CommonFragmentBinding
    private var groupId: String? = null

    private fun containerActivity() = (activity as StaffRegisterActivity)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CommonFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        containerActivity().showProgressBar()
        getBundleData()
        viewLifecycleOwner.lifecycleScope.launch {
            getApiResponse()
        }
    }

    private fun getBundleData() {
        groupId = arguments?.getString(StringConstants.GROUP_ID)
    }

    private suspend fun getApiResponse() {
        groupId?.let { gId ->
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.getStaffApiService(requireContext()).getAllStaffService(gId)
                }

                withContext(Dispatchers.Main) {
                    containerActivity().dismissProgressBar()

                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()?.data

                        if (data.isNullOrEmpty()) {
                            binding.recyclerView.adapter = null
                            binding.txtEmpty.isVisible = true
                        } else {
                            binding.txtEmpty.isVisible = false

                            val adapter = GenericAdapter(
                                data = data.toMutableList(),
                                bind = { binding, item, _ ->
                                    binding.nameTextView.text = item.name
                                    binding.designationTextView.apply {
                                        text = item.designation.orEmpty()
                                        visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
                                    }
                                    if (!item.image.isNullOrEmpty()) {
                                        try {
                                            val decodedBytes = Base64.decode(item.image, Base64.DEFAULT)
                                            val decodedImageUrl = String(decodedBytes)
                                            if (decodedImageUrl.startsWith("http") && !decodedImageUrl.contains("undefined")) {
                                                Glide.with(binding.photoImageView.context)
                                                    .load(decodedImageUrl)
                                                    .placeholder(R.drawable.ic_launcher_background)
                                                    .error(R.drawable.ic_launcher_background)
                                                    .into(binding.photoImageView)
                                            }
                                        } catch (e: IllegalArgumentException) {
//                                            EmailValidation.setImageForName(item.name, binding.photoImageView)
                                        }
                                    }
                                },
                                inflater = { inflater, parent, _ ->
                                    ItemUserDetailsBinding.inflate(inflater, parent, false)
                                }
                            )

                            binding.recyclerView.layoutManager = LinearLayoutManager(binding.recyclerView.context)
                            binding.recyclerView.adapter = adapter
                        }
                    } else {
                        requireContext().showToast(
                            "${getString(R.string.txt_error)}: ${response.message()}"
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    containerActivity().dismissProgressBar()
                    requireContext().showToast("${getString(R.string.txt_failure)}: ${e.localizedMessage}")
                    Log.e("StaffRegisterFragment", "Error: ${e.localizedMessage}", e)
                }
            }
        }
    }

}
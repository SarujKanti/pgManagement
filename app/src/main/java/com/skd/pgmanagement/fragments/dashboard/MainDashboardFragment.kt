package com.skd.pgmanagement.fragments.dashboard

import com.skd.pgmanagement.R
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Base64
import android.view.*
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.skd.pgmanagement.activities.MainDashboardScreen
import com.skd.pgmanagement.activities.staffRegister.StaffRegisterActivity
import com.skd.pgmanagement.adapters.GenericAdapter
import com.skd.pgmanagement.adapters.HomeCategoryAdapter
import com.skd.pgmanagement.constants.StringConstants
import com.skd.pgmanagement.databinding.CommonFragmentBinding
import com.skd.pgmanagement.databinding.ItemGalleryImagesBinding
import com.skd.pgmanagement.databinding.ItemUserDetailsBinding
import com.skd.pgmanagement.networks.RetrofitClient
import com.skd.pgmanagement.networks.dataModel.ActivityData
import com.skd.pgmanagement.utils.EmailValidation
import com.skd.pgmanagement.utils.showToast
import com.skd.pgmanagement.views.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainDashboardFragment : BaseFragment<CommonFragmentBinding>(R.layout.common_fragment) {

    override lateinit var binding: CommonFragmentBinding

    private var groupId: String? = null
    private var homeDataList: List<ActivityData>? = null
    private var profileName: String? = null
    private var decodedImageUrl: String? = null

    private fun containerActivity() = (activity as MainDashboardScreen)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupId = arguments?.getString(StringConstants.GROUP_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CommonFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.appBar.isVisible=true
        initToolbar()
        loadDashboardData()
    }

    /** Toolbar Action */
    private fun initToolbar() {
        binding?.ivMore?.setOnClickListener {
            homeDataList?.let {
                showPartialFullscreenDialog(it, profileName)
            } ?: requireContext().showToast(getString(R.string.data_not_loaded))
        }
    }

    /**  Load Home & Profile Data */
    private fun loadDashboardData() {
        lifecycleScope.launch {
            val homeJob = launch { loadHomeData() }
            val profileJob = launch { loadProfileData() }
            val galleryJob = launch { getGalleryData() }

            homeJob.join()
            profileJob.join()
            galleryJob.join()
        }
    }

    /** Home API Call */
    private suspend fun loadHomeData() {
        groupId?.let { id ->
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.homeApiService(requireContext()).getHomeGroupsSuspend(id)
                }

                if (response.isSuccessful && response.body() != null) {
                    val originalData = response.body()?.data

                    homeDataList = originalData?.map { activity ->
                        val filteredIcons = activity.featureIcons.filter { feature ->
                            feature.type == StringConstants.SUBJECT_REGISTER ||
                                    feature.type == StringConstants.STAFF_REGISTER ||
                                    feature.type == StringConstants.FEED_BACK ||
                                    feature.type == StringConstants.GALLERY ||
                                    feature.type == StringConstants.HOSTEL ||
                                    feature.type == StringConstants.MESSAGE ||
                                    feature.type == StringConstants.NOTICE_BOARD
                        }
                        activity.copy(featureIcons = filteredIcons)
                    }

                } else {
                    requireContext().showToast("Error: ${response.message()}")
                }

            } catch (e: Exception) {
                requireContext().showToast("Failure: ${e.localizedMessage}")
            }
        }
    }

    /** Profile API Call */
    private suspend fun loadProfileData() {
        groupId?.let { id ->
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.profileApiService(requireContext()).getKidSProfileSuspend(id)
                }

                val firstItem = response.body()?.data?.firstOrNull()
                if (firstItem != null) {
                    profileName = firstItem.name

                    if (!firstItem.image.isNullOrEmpty()) {
                        val decodedBytes = android.util.Base64.decode(firstItem.image, android.util.Base64.DEFAULT)
                        val decodedString = String(decodedBytes)

                        if (decodedString.startsWith("http") && !decodedString.contains("undefined")) {
                            decodedImageUrl = decodedString
                        }
                    }
                }

            } catch (e: Exception) {
                requireContext().showToast("Failed: ${e.localizedMessage}")
            }
        }
    }

    private suspend fun getGalleryData(){
        groupId?.let { id ->
            try {
                val response = withContext(Dispatchers.IO){
                    RetrofitClient.getGalleryApiService(requireContext()).getGalleryPosts(id,1)
                }
                withContext(Dispatchers.Main) {
                    containerActivity().dismissProgressBar()

                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()?.data

                        if (data.isNullOrEmpty()) {
                            binding.recyclerView.adapter = null
                            binding.txtEmpty.isVisible = true
                        }
                        else {
                            binding.txtEmpty.isVisible = false

                            val adapter = GenericAdapter(
                                data = data.toMutableList(),
                                bind = { binding, item, _ ->

                                    val galleryBinding = binding as ItemGalleryImagesBinding

                                    // Decode Base64 image URL
                                    val decodedUrl = try {
                                        val decodedBytes = Base64.decode(
                                            item.fileName.first(),
                                            Base64.DEFAULT
                                        )
                                        String(decodedBytes)
                                    } catch (e: Exception) {
                                        ""
                                    }

                                    // Load into ImageView
                                    Glide.with(galleryBinding.imageViewBanner.context)
                                        .load(decodedUrl)
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .into(galleryBinding.imageViewBanner)
                                },
                                inflater = { inflater, parent, _ ->
                                    ItemGalleryImagesBinding.inflate(inflater, parent, false)
                                }
                            )

                            binding.recyclerView.layoutManager =
                                LinearLayoutManager(binding.recyclerView.context)
                            binding.recyclerView.adapter = adapter
                        }

                    } else {
                        requireContext().showToast(
                            "${getString(R.string.txt_error)}: ${response.message()}"
                        )
                    }
                }
            }catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    containerActivity().dismissProgressBar()
                    requireContext().showToast("${getString(R.string.txt_failure)}: ${e.localizedMessage}")
                }
            }
        }
    }

    /** Side Menu Dialog */
    private fun showPartialFullscreenDialog(list: List<ActivityData>, userName: String?) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_fullscreen_partial)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout((resources.displayMetrics.widthPixels * 0.7).toInt(), WindowManager.LayoutParams.MATCH_PARENT)
            setGravity(Gravity.START)
        }

        dialog.findViewById<TextView>(R.id.tvDialogContent).text = userName ?: ""

        val img = dialog.findViewById<ShapeableImageView>(R.id.leftImageView)
        decodedImageUrl?.let {
            Glide.with(requireContext()).load(it).into(img)
        }

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerViewCategories)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = HomeCategoryAdapter(list, groupId)

        dialog.show()
    }
}

package com.skd.pgmanagement.fragments.dashboard

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
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
import com.skd.pgmanagement.R
import com.skd.pgmanagement.activities.MainDashboardScreen
import com.skd.pgmanagement.activities.loginPage.LoginActivity
import com.skd.pgmanagement.adapters.GenericAdapter
import com.skd.pgmanagement.adapters.HomeCategoryAdapter
import com.skd.pgmanagement.constants.StringConstants
import com.skd.pgmanagement.databinding.CommonFragmentBinding
import com.skd.pgmanagement.databinding.ItemGalleryImagesBinding
import com.skd.pgmanagement.networks.RetrofitClient
import com.skd.pgmanagement.networks.dataModel.ActivityData
import com.skd.pgmanagement.networks.dataModel.AlbumData
import com.skd.pgmanagement.utils.showToast
import com.skd.pgmanagement.views.BaseFragment
import kotlinx.coroutines.*

class MainDashboardFragment : BaseFragment<CommonFragmentBinding>(R.layout.common_fragment) {

    override lateinit var binding: CommonFragmentBinding

    private var groupId: String? = null
    private var homeDataList: List<ActivityData>? = null
    private var profileName: String? = null
    private var decodedImageUrl: String? = null

    private lateinit var sharedPreferences: SharedPreferences

    private fun containerActivity() = (activity as MainDashboardScreen)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupId = arguments?.getString(StringConstants.GROUP_ID)
        sharedPreferences = requireContext().getSharedPreferences(StringConstants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CommonFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.appBar.isVisible = true

        containerActivity().showProgressBar()
        initToolbar()
        loadDashboardData()
        setupLogoutDialog()
    }

     /**Toolbar*/
    private fun initToolbar() {
        binding.ivMore.setOnClickListener {
            homeDataList?.let { list ->
                showPartialFullscreenDialog(list, profileName)
            } ?: requireContext().showToast(getString(R.string.data_not_loaded))
        }
    }

    /**Load all dashboard data IN PARALLEL using async/await*/
    private fun loadDashboardData() {
        lifecycleScope.launch {
            try {
                val homeDeferred = async { loadHomeData() }
                val profileDeferred = async { loadProfileData() }
                val galleryDeferred = async { loadGalleryData() }
                val galleryDeferred2 = async { totalPeopleData() }

                homeDeferred.await()
                profileDeferred.await()
                galleryDeferred.await()
                galleryDeferred2.await()

            } catch (e: Exception) {
                requireContext().showToast("${getString(R.string.txt_error)}: ${e.localizedMessage}")
            } finally {
                containerActivity().dismissProgressBar()
            }
        }
    }

    /**     Home API*/
    private suspend fun loadHomeData() = withContext(Dispatchers.IO) {
        val id = groupId ?: return@withContext

        val response = RetrofitClient.homeApiService(requireContext()).getHomeGroupsSuspend(id)

        if (response.isSuccessful && response.body() != null) {

            homeDataList = response.body()?.data?.map { activity ->
                val allowedTypes = listOf(
                    StringConstants.SUBJECT_REGISTER,
                    StringConstants.STAFF_REGISTER,
                    StringConstants.FEED_BACK,
                    StringConstants.GALLERY,
                    StringConstants.HOSTEL,
                    StringConstants.MESSAGE,
                    StringConstants.NOTICE_BOARD
                )

                val filteredIcons = activity.featureIcons.filter { it.type in allowedTypes }

                activity.copy(featureIcons = filteredIcons)
            }

        } else {
            withContext(Dispatchers.Main) {
                requireContext().showToast("Error: ${response.message()}")
            }
        }
    }

    /**     Profile API */
    private suspend fun loadProfileData() = withContext(Dispatchers.IO) {
        val id = groupId ?: return@withContext
        val response =
            RetrofitClient.profileApiService(requireContext()).getKidSProfileSuspend(id)

        val profile = response.body()?.data?.firstOrNull() ?: return@withContext

        profileName = profile.name

        profile.image?.let {
            try {
                val decodedString = String(Base64.decode(it, Base64.DEFAULT))
                if (decodedString.startsWith("http") && !decodedString.contains("undefined")) {
                    decodedImageUrl = decodedString
                }
            } catch (_: Exception) {}
        }
    }

    /** Gallery API*/
    private suspend fun loadGalleryData() = withContext(Dispatchers.IO) {
        val id = groupId ?: return@withContext
        val response = RetrofitClient.getGalleryApiService(requireContext()).getGalleryPosts(id, 1)

        withContext(Dispatchers.Main) {

            if (response.isSuccessful && response.body() != null) {
                val data = response.body()?.data ?: emptyList()
                binding.recyclerView2nd.isVisible= true
                binding.tvAddMore.isVisible= true

                if (data.isEmpty()) {
                    binding.recyclerView2nd.adapter = null
                    binding.txtEmpty.isVisible = true
                } else {
                    binding.txtEmpty.isVisible = false
                    setupGalleryAdapter(data)
                }

            } else {
                requireContext().showToast("Error: ${response.message()}")
            }
        }
    }

    private suspend fun totalPeopleData() = withContext(Dispatchers.IO) {
        val id = groupId ?: return@withContext
        val response = RetrofitClient.getTotalPeopleApiService(requireContext()).getTotalPeople(id)

        withContext(Dispatchers.Main) {

            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!.data
                binding.llCardDetails.isVisible= true
                binding.tvStaffCount.text = data.totalNoOfStaffs.toString()
                binding.tvStudentCount.text = data.totalNoOfClasses.toString()

            } else {
                requireContext().showToast("Error: ${response.message()}")
            }
        }
    }

    private fun setupGalleryAdapter(data: List<AlbumData>) {

        val allImages = mutableListOf<String>()

        data.forEach { album ->
            album.fileName.forEach { encoded ->
                val decodedUrl = try {
                    String(Base64.decode(encoded, Base64.DEFAULT))
                } catch (e: Exception) {
                    ""
                }
                allImages.add(decodedUrl)
            }
        }

        val adapter = GenericAdapter(
            data = allImages,
            bind = { binding, imageUrl, position ->

                val galleryBinding = binding as ItemGalleryImagesBinding
                val cardView = galleryBinding.cvItemView

                val screenWidth = Resources.getSystem().displayMetrics.widthPixels
                val layoutParams = cardView.layoutParams

                val eightyPercent = (screenWidth * 0.80).toInt()
                layoutParams.width = eightyPercent
                cardView.layoutParams = layoutParams

                Glide.with(galleryBinding.imageViewBanner.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(galleryBinding.imageViewBanner)
            },
            inflater = { inflater, parent, _ ->
                ItemGalleryImagesBinding.inflate(inflater, parent, false)
            }
        )

        binding.recyclerView2nd.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        binding.recyclerView2nd.adapter = adapter
        var currentIndex = 0
        lifecycleScope.launch {
            delay(1000)
            while (true) {
                delay(2000)
                val lastIndex = allImages.size - 1
                currentIndex = if (currentIndex >= lastIndex) 0 else currentIndex + 1
                binding.recyclerView2nd.smoothScrollToPosition(currentIndex)
            }
        }
    }



    /** Left Side Drawer*/
    private fun showPartialFullscreenDialog(list: List<ActivityData>, userName: String?) {

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_fullscreen_partial)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout((resources.displayMetrics.widthPixels * 0.7).toInt(),
                WindowManager.LayoutParams.MATCH_PARENT)
            setGravity(Gravity.START)
        }

        dialog.findViewById<TextView>(R.id.tvDialogContent).text = userName.orEmpty()

        val profileImg = dialog.findViewById<ShapeableImageView>(R.id.leftImageView)
        decodedImageUrl?.let {
            Glide.with(requireContext()).load(it).into(profileImg)
        }

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerViewCategories)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = HomeCategoryAdapter(list, groupId)

        dialog.show()
    }

    /**     Logout*/
    private fun setupLogoutDialog() {

        binding.ivDelete.setOnClickListener {

            AlertDialog.Builder(requireContext()).apply {
                setTitle(getString(R.string.txt_Logout))
                setMessage(getString(R.string.txt_sure_logout))
                setPositiveButton(getString(R.string.txt_ok)) { dialog, _ ->

                    sharedPreferences.edit().clear().apply()
                    requireContext().showToast(getString(R.string.txt_success_logout))
                    dialog.dismiss()

                    val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    requireActivity().finish()
                }

                setNegativeButton(getString(R.string.txt_Cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                show()
            }
        }
    }

}

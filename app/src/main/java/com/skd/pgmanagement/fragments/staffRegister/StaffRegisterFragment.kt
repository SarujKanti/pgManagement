package com.skd.pgmanagement.fragments.staffRegister

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.skd.pgmanagement.R
import com.skd.pgmanagement.activities.staffRegister.StaffRegisterActivity
import com.skd.pgmanagement.adapters.GenericAdapter
import com.skd.pgmanagement.constants.StringConstants
import com.skd.pgmanagement.databinding.CommonFragmentBinding
import com.skd.pgmanagement.databinding.ItemUserDetailsBinding
import com.skd.pgmanagement.networks.RetrofitClient
import com.skd.pgmanagement.networks.dataModel.AddStaffRequest
import com.skd.pgmanagement.networks.dataModel.StaffData
import com.skd.pgmanagement.networks.dataModel.StaffUserDetails
import com.skd.pgmanagement.networks.dataModel.StaffUserDetailsResponse
import com.skd.pgmanagement.utils.EmailValidation
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
                                    binding.tvDoj.apply {
                                        text = getString(R.string.txt_doj) +"\t"+ item.doj.orEmpty()
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
                                            else {
                                                EmailValidation.setImageForName(item.name, binding.photoImageView)
                                            }
                                        } catch (e: IllegalArgumentException) {
                                            EmailValidation.setImageForName(item.name, binding.photoImageView)
                                        }
                                    }else {
                                        EmailValidation.setImageForName(item.name, binding.photoImageView)
                                    }
                                    binding.ivCall.setOnClickListener {
                                        openDialer(item.phone)
                                    }
                                    binding.ivWhatsapp.setOnClickListener {
                                        openWhatsApp(item.phone)
                                    }
                                    binding.ivMessage.setOnClickListener {
                                        openSmsApp(item.phone)
                                    }
                                    binding.root.setOnClickListener {
//                                        showStaffDetailDialog(item)
                                        val staffId = item.staffId
                                        fetchStaffDetails(staffId)
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
                }
            }
        }
    }

    private fun openDialer(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("${getString(R.string.text_tel)} $phoneNumber")
            startActivity(intent)
        } catch (e: Exception) {
            requireContext().showToast(getString(R.string.unable_to_open_phone))
        }
    }

    private fun openWhatsApp(phoneNumber: String) {
        try {
            val formatted = phoneNumber.trim()

            val uri = Uri.parse("${getString(R.string.whatsapp_device)} $formatted")

            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage(getString(R.string.package_whatsapp))

            startActivity(intent)

        } catch (e: Exception) {
            requireContext().showToast(getString(R.string.whatsapp_not_installed))
        }
    }


    private fun openSmsApp(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("${getString(R.string.text_sms)} $phoneNumber") // Direct SMS
            startActivity(intent)
        } catch (e: Exception) {
            requireContext().showToast(getString(R.string.unable_to_open_message))
        }
    }


    private fun fetchStaffDetails(staffId: String) {

        // Inflate bottom sheet layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_details, null)
        val nameTextView = dialogView.findViewById<TextView>(R.id.studentName)
        val emailTextView = dialogView.findViewById<TextView>(R.id.designation)
        val imageView = dialogView.findViewById<ImageView>(R.id.profileImageView)

        val staffName = dialogView.findViewById<TextView>(R.id.etNameInput)
        val phoneNumber = dialogView.findViewById<TextView>(R.id.etPhoneNumber)
        val designation = dialogView.findViewById<TextView>(R.id.etDesignation)
        val profession = dialogView.findViewById<TextView>(R.id.etProfession)

        // Set loading defaults
        nameTextView.text = "Loading..."
        emailTextView.text = ""
        imageView.setImageResource(R.drawable.ic_launcher_background)

        // Create BottomSheetDialog
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        bottomSheetDialog.show()

        // Optional: expand full height
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        // Call API
        groupId?.let { gId ->
            containerActivity().showProgressBar()

            RetrofitClient.getStaffDetailsApiService(requireContext())
                .getStaffDetails(gId, staffId, "staff")
                .enqueue(object : retrofit2.Callback<StaffUserDetailsResponse> {

                    override fun onResponse(
                        call: retrofit2.Call<StaffUserDetailsResponse>,
                        response: retrofit2.Response<StaffUserDetailsResponse>
                    ) {
                        containerActivity().dismissProgressBar()
                        val data = response.body()?.data

                        if (response.isSuccessful && data != null) {

                            nameTextView.text = data.name ?: "N/A"
                            emailTextView.text = data.designation ?: "N/A"
                            staffName.text = data.name ?: "N/A"
                            phoneNumber.text = data.phone ?: "N/A"
                            designation.text = data.designation ?: "N/A"
                            profession.text = data.doj ?: "N/A"

                            // Load Image
                            if (!data.image.isNullOrEmpty()) {
                                try {
                                    val decodedBytes = Base64.decode(data.image, Base64.DEFAULT)
                                    val decodedImageUrl = String(decodedBytes)

                                    if (decodedImageUrl.startsWith("http") && !decodedImageUrl.contains("undefined")) {
                                        Glide.with(imageView.context)
                                            .load(decodedImageUrl)
                                            .placeholder(R.drawable.ic_launcher_background)
                                            .error(R.drawable.ic_launcher_background)
                                            .into(imageView)
                                    } else {
                                        data.name?.let { EmailValidation.setImageForName(it, imageView) }
                                    }

                                } catch (e: Exception) {
                                    data.name?.let { EmailValidation.setImageForName(it, imageView) }
                                }
                            } else {
                                data.name?.let { EmailValidation.setImageForName(it, imageView) }
                            }

                        } else {
                            requireContext().showToast("Staff details not found")
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<StaffUserDetailsResponse>, t: Throwable) {
                        containerActivity().dismissProgressBar()
                        requireContext().showToast("Failed to fetch: ${t.localizedMessage}")
                    }
                })
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all, menu)
        menu.findItem(R.id.menu_add_post)?.isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add_post -> {
                classTeacherListDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun classTeacherListDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_new_user, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(dialogView)

        bottomSheetDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        bottomSheetDialog.behavior.peekHeight = (resources.displayMetrics.heightPixels * 0.5).toInt()
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.behavior.skipCollapsed = true

        val etName = dialogView.findViewById<TextInputEditText>(R.id.etName)
        val etPhone = dialogView.findViewById<TextInputEditText>(R.id.etPhone)
        val etDesignation = dialogView.findViewById<TextInputEditText>(R.id.etDesignation)
        val btnSave = dialogView.findViewById<Button>(R.id.imgAddStudent)

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val designation = etDesignation.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty() || designation.isEmpty()) {
                requireContext().showToast(getString(R.string.fill_all_details))
                return@setOnClickListener
            }

            btnSave.isEnabled = false
            containerActivity().showProgressBar()

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val staff = StaffData(
                        countryCode = "IN",
                        designation = designation,
                        name = name,
                        permanent = true,
                        phone = phone
                    )

                    val request = AddStaffRequest(staffData = listOf(staff))

                    val response = withContext(Dispatchers.IO) {
                        RetrofitClient.addStaffApiService(requireContext())
                            .addStaff(
                                groupId = groupId ?: "",
                                request = request,
                                type = "teaching"
                            )
                    }

                    if (response.isSuccessful && response.body() != null) {
                        containerActivity().showProgressBar()
                        bottomSheetDialog.dismiss()
                        getApiResponse()
                    } else {
                        requireContext().showToast("${getString(R.string.failed_to_add)}: ${response.message()}")
                        btnSave.isEnabled = true
                    }

                } catch (e: Exception) {
                    requireContext().showToast("${getString(R.string.txt_error)}: ${e.localizedMessage}")
                    btnSave.isEnabled = true
                }
            }
        }
        bottomSheetDialog.show()
    }



}
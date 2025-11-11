package com.skd.pgmanagement.fragments.login

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.skd.pgmanagement.R
import com.skd.pgmanagement.activities.MainDashboardScreen
import com.skd.pgmanagement.databinding.LoginFragmentBinding
import com.skd.pgmanagement.views.BaseFragment
import com.skd.pgmanagement.networks.RetrofitClient
import com.skd.pgmanagement.networks.dataModel.LoginRequest
import com.skd.pgmanagement.networks.dataModel.LoginResponse
import com.skd.pgmanagement.networks.dataModel.UserExistRequest
import com.skd.pgmanagement.networks.dataModel.UserExistResponse
import com.skd.pgmanagement.networks.dataModel.UserName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : BaseFragment<LoginFragmentBinding>(R.layout.login_fragment) {

    override lateinit var binding: LoginFragmentBinding
    private var isPasswordVisible = false
    private var isUserExist = false
    private var enteredPassword: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitButton.isEnabled = false
        binding.llPassword.visibility = View.GONE

        binding.phoneNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val phone = s.toString().trim()
                if (phone.length == 10) {
                    binding.submitButton.isEnabled = true
                    binding.submitButton.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_button_enabler
                    )
                } else {
                    binding.submitButton.isEnabled = false
                    binding.llPassword.visibility = View.GONE
                }
            }
        })

        binding.imageButtonShowPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.password.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.imageButtonShowPassword.setImageResource(R.drawable.ic_eye_open)
            } else {
                binding.password.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.imageButtonShowPassword.setImageResource(R.drawable.ic_eye_hide)
            }
            binding.password.setSelection(binding.password.text?.length ?: 0)
        }

        binding.submitButton.setOnClickListener {
            val phone = binding.phoneNumberEditText.text.toString().trim()

            if (phone.length != 10) {
                Toast.makeText(requireContext(), "Please enter valid phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isUserExist) {
                // First time → check user existence
                checkUserExist(phone)
            } else {
                // If user already exists → proceed with password check
                enteredPassword = binding.password.text.toString().trim()
                if (enteredPassword.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Please enter your password", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                userPasswordCheck(phone, "IN", enteredPassword!!)
            }
        }
    }

    private fun checkUserExist(phone: String) {
        val request = UserExistRequest(countryCode = "IN", phone = phone)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val api = RetrofitClient.loginApiService(requireContext())
                val response = api.checkUserExist(request)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val userExistResponse: UserExistResponse? = response.body()
                        if (userExistResponse != null && userExistResponse.data.isUserExist) {
                            isUserExist = true
                            binding.llPassword.visibility = View.VISIBLE
                        } else {
                            Toast.makeText(requireContext(), "User not found. Please register first.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failure: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun userPasswordCheck(phone: String, countryCode: String, password: String) {
        val appVersion = requireContext().packageManager
            .getPackageInfo(requireContext().packageName, 0).versionName

        val request = LoginRequest(
            userName = UserName(phone = phone, countryCode = countryCode),
            password = password,
            deviceType = "Android",
            deviceToken = "fwqHSReQQX-Xa1YvVD9YUe:APA91bHaM7QUSlyO0ZW7eBdBOwtxznDuoYbyWB9lZDvE5sVeAazMJM42JkfWE6CkMjrsX1zkY7AjsBGylK8VtKWtGNFOkVfwgcvaLJMTYSOks_VfAfPwzlE",
            deviceModel = Build.MODEL,
            appVersion = appVersion.toString(),
            osVersion = Build.VERSION.SDK_INT.toString(),
            udid = "dummy_udid"
        )

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val api = RetrofitClient.loginPasswordApiService(requireContext())
                val response = api.loginISTrue(
                    "fwqHSReQQX-Xa1YvVD9YUe:APA91bHaM7QUSlyO0ZW7eBdBOwtxznDuoYbyWB9lZDvE5sVeAazMJM42JkfWE6CkMjrsX1zkY7AjsBGylK8VtKWtGNFOkVfwgcvaLJMTYSOks_VfAfPwzlE",
                    "Android",
                    request
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val loginResponse: LoginResponse? = response.body()

                        if (loginResponse != null) {
                            val sharedPrefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                            sharedPrefs.edit()
                                .putString("auth_token", loginResponse.token)
                                .apply()

                            Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(requireContext(), MainDashboardScreen::class.java)
                            intent.putExtra("PreferencesConstants.GROUP_ID", loginResponse.groupId)
                            startActivity(intent)
                            requireActivity().finish()
                        } else {
                            Toast.makeText(requireContext(), "Login response is null", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failure: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

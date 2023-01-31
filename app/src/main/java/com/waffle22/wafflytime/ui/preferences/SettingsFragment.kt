package com.waffle22.wafflytime.ui.preferences


import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.waffle22.wafflytime.BuildConfig
import com.waffle22.wafflytime.MainActivity
import com.waffle22.wafflytime.R
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream

class SettingsFragment: PreferenceFragmentCompat() {

    private lateinit var prefs: SharedPreferences
    private lateinit var getResult: ActivityResultLauncher<Intent>
    private val profileViewModel: SetProfilePicViewModel by viewModel()
    private val logoutViewModel: LogoutViewModel by viewModel()
    private lateinit var profileCard: ProfileCardPreference
    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            changeProfilePictureReceiver(res)
        }
        alertDialog = MaterialAlertDialogBuilder(this.requireContext())
            .setView(ProgressBar(this.requireContext()))
            .setMessage("Loading...")
            .create()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)
        findPreference<Preference>("app_version")?.summary = BuildConfig.VERSION_NAME
        findPreference<Preference>("logout")?.setOnPreferenceClickListener {
            logout()
            true
        }
        findPreference<Preference>("change_profile_image")?.setOnPreferenceClickListener {
            createSetProfileDialog()
            true
        }
        profileCard = findPreference<ProfileCardPreference>("profile_card")!!
        displayProfilePicture()
    }

    fun logout() {
        logoutViewModel.logout()
        alertDialog.show()
        lifecycleScope.launch {
            logoutViewModel.logoutState.collect {
                if(it.status != "0") {
                    alertDialog.dismiss()
                    if(it.status == "200") {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.putExtra("doLogout", true)
                        requireActivity().setResult(RESULT_OK, intent)
                        requireActivity().finish()
                    }
                    else {
                        Toast.makeText(context, it.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    coroutineContext.job.cancel()
                }
            }
        }
    }

    fun createSetProfileDialog() {
        val str = arrayOf("프로필 이미지 변경", "프로필 이미지 삭제")
        val builder = AlertDialog.Builder(requireContext())
            .setItems(str) { dialog: DialogInterface, position: Int ->
                when(position) {
                    0 -> {
                        changeProfilePicture()
                    }
                    1 -> {
                        deleteProfilePicture()
                    }
                }
            }
        val dialog = builder.create()
        dialog.show()
    }

    fun changeProfilePicture() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType("image/*")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
        getResult.launch(intent)
    }

    fun changeProfilePictureReceiver(result: ActivityResult) {
        if(result.resultCode == RESULT_OK) {
            try {
                result.data?.data?.let { returnUri ->
                    val filename = requireContext().contentResolver.query(
                        returnUri,
                        null,
                        null,
                        null,
                        null
                    )!!.use { c ->
                        val nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        c.moveToFirst()
                        c.getString(nameIndex)
                    }
                    val byteArray =
                        requireContext().contentResolver.openInputStream(returnUri)!!
                            .use { stream ->
                                val byteStream = ByteArrayOutputStream()
                                val buffer = ByteArray(1000)
                                var size: Int
                                while (true) {
                                    size = stream.read(buffer)
                                    if (size == -1) break
                                    byteStream.write(buffer, 0, size)
                                }
                                byteStream.toByteArray()
                            }

                    profileViewModel.setProfilePic(filename, byteArray)
                    alertDialog.show()
                    lifecycleScope.launch {
                        profileViewModel.state.collect {
                            if (it.status != "0") {
                                alertDialog.dismiss()
                                if (it.errorCode == null && it.errorMessage == null) {
//                                    alertDialog.dismiss()
                                    Toast.makeText(
                                        requireContext(),
                                        "프로필 사진이 설정되었습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    displayProfilePicture()
                                } else {
//                                    alertDialog.dismiss()
                                    Toast.makeText(
                                        requireContext(),
                                        it.errorMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                this.coroutineContext.job.cancel()
                            }
                        }
                    }
                }
            }
            catch (e: NullPointerException) {
                Toast.makeText(requireContext(), "Error while opening file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteProfilePicture() {
        profileViewModel.deleteProfilePic()
        alertDialog.show()
        lifecycleScope.launch {
            profileViewModel.state.collect {
                if (it.status != "0") {
                    alertDialog.dismiss()
                    if (it.errorCode == null && it.errorMessage == null) {
//                                    alertDialog.dismiss()
                        Toast.makeText(
                            requireContext(),
                            "프로필 사진이 삭제되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        displayProfilePicture()
                    } else {
//                                    alertDialog.dismiss()
                        Toast.makeText(
                            requireContext(),
                            it.errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    this.coroutineContext.job.cancel()
                }
            }
        }
    }

    fun displayProfilePicture() {
        profileViewModel.getProfileUrl()
        lifecycleScope.launch {
            profileViewModel.profileUrl.collect() {
                if(it.status != "0") {
                    if(it.errorCode == null && it.errorMessage == null) {
                        profileCard.profileUrl = it.dataHolder
                    }
                    else {
                        Toast.makeText(
                            requireContext(),
                            it.errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    coroutineContext.job.cancel()
                }
            }
        }
    }
}
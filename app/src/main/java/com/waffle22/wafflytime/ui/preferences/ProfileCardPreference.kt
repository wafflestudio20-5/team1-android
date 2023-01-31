package com.waffle22.wafflytime.ui.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.bumptech.glide.Glide
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.ProfileCardBinding
import com.waffle22.wafflytime.util.AuthStorage

class ProfileCardPreference(context: Context, attrs: AttributeSet?): Preference(context, attrs) {

    private lateinit var binding: ProfileCardBinding
    private val sharedPref =
        context.getSharedPreferences(AuthStorage.SharedPreferenceName, Context.MODE_PRIVATE)
    var profileUrl: String? = null
        set(value) {
            field = value
            if(this::binding.isInitialized) {
                displayProfilePic()
            }
        }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        binding = ProfileCardBinding.bind(holder.itemView)
        var id = sharedPref.getString(AuthStorage.UserLoginIdKey, "")
        if(id!!.isEmpty()) id = sharedPref.getString(AuthStorage.UserSocialEmailKey, "")
        binding.useridText.text = id
        binding.usernameText.text = sharedPref.getString(AuthStorage.UserNickNameKey, "NULL")

        displayProfilePic()
    }

    private fun displayProfilePic() {
        if(profileUrl == null) {
            binding.profilePic.setImageResource(R.drawable.ic_person)
        }
        else {
            Glide.with(context)
                .load(profileUrl)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .fallback(R.drawable.ic_person)
                .into(binding.profilePic)
        }
    }
}
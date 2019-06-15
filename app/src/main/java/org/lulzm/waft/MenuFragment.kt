package org.lulzm.waft

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import io.reactivex.annotations.NonNull
import io.reactivex.annotations.Nullable
import org.lulzm.waft.databinding.FragmentMenuBinding
import java.util.*

class MenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMenuBinding = FragmentMenuBinding.inflate(inflater, container, false)
        //        getActivity().getActionBar().hide();

        return fragmentMenuBinding.root
    }

    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        val sharedPreferences = Objects.requireNonNull<FragmentActivity>(activity)
            .getSharedPreferences("change_theme", MODE_PRIVATE)
        if (sharedPreferences.getBoolean("dark_theme", false)) {
            activity!!.setTheme(R.style.darktheme)
        } else {
            activity!!.setTheme(R.style.AppTheme)
        }
        super.onViewCreated(view, savedInstanceState)

        fragmentMenuBinding.lilPost.setOnClickListener { view1 ->
            if (MainActivity.HomeFragmentHandler != null) {
                MainActivity.HomeFragmentHandler.sendEmptyMessage(0)
            }

            Glide.with(this@MenuFragment).load(R.drawable.home_blue)
                .into(fragmentMenuBinding.iconPost)
            Glide.with(this@MenuFragment).load(R.drawable.profile)
                .into(fragmentMenuBinding.iconAccounts)
            Glide.with(this@MenuFragment).load(R.drawable.settings)
                .into(fragmentMenuBinding.iconHistory)
            Glide.with(this@MenuFragment).load(R.drawable.sos1)
                .into(fragmentMenuBinding.iconShareus)
            Glide.with(this@MenuFragment).load(R.drawable.logout)
                .into(fragmentMenuBinding.iconLogout)

            fragmentMenuBinding.textPost.isSelected = true
            fragmentMenuBinding.textAccounts.isSelected = false
            fragmentMenuBinding.textHistory.isSelected = false
            fragmentMenuBinding.textShareus.isSelected = false
            fragmentMenuBinding.textLogout.isSelected = false

        }

        fragmentMenuBinding.lilAccounts.setOnClickListener { view2 ->
            if (MainActivity.HomeFragmentHandler != null) {
                MainActivity.HomeFragmentHandler.sendEmptyMessage(1)
            }

            Glide.with(this@MenuFragment).load(R.drawable.home).into(fragmentMenuBinding.iconPost)
            Glide.with(this@MenuFragment).load(R.drawable.profile_blue).into(fragmentMenuBinding.iconAccounts)
            Glide.with(this@MenuFragment).load(R.drawable.settings).into(fragmentMenuBinding.iconHistory)
            Glide.with(this@MenuFragment).load(R.drawable.sos1).into(fragmentMenuBinding.iconShareus)
            Glide.with(this@MenuFragment).load(R.drawable.logout).into(fragmentMenuBinding.iconLogout)

            fragmentMenuBinding.textPost.isSelected = false
            fragmentMenuBinding.textAccounts.isSelected = true
            fragmentMenuBinding.textHistory.isSelected = false
            fragmentMenuBinding.textShareus.isSelected = false
            fragmentMenuBinding.textLogout.isSelected = false


        }
        fragmentMenuBinding.lilHistory.setOnClickListener { view3 ->
            if (MainActivity.HomeFragmentHandler != null) {
                MainActivity.HomeFragmentHandler.sendEmptyMessage(2)
            }

            Glide.with(this@MenuFragment).load(R.drawable.home).into(fragmentMenuBinding.iconPost)
            Glide.with(this@MenuFragment).load(R.drawable.profile).into(fragmentMenuBinding.iconAccounts)
            Glide.with(this@MenuFragment).load(R.drawable.settings_blue).into(fragmentMenuBinding.iconHistory)
            Glide.with(this@MenuFragment).load(R.drawable.sos1).into(fragmentMenuBinding.iconShareus)
            Glide.with(this@MenuFragment).load(R.drawable.logout).into(fragmentMenuBinding.iconLogout)

            fragmentMenuBinding.textPost.isSelected = false
            fragmentMenuBinding.textAccounts.isSelected = false
            fragmentMenuBinding.textHistory.isSelected = true
            fragmentMenuBinding.textShareus.isSelected = false
            fragmentMenuBinding.textLogout.isSelected = false

        }

        fragmentMenuBinding.lilShareus.setOnClickListener { view4 ->
            if (MainActivity.HomeFragmentHandler != null) {
                MainActivity.HomeFragmentHandler.sendEmptyMessage(3)
            }
            Glide.with(this@MenuFragment).load(R.drawable.home).into(fragmentMenuBinding.iconPost)
            Glide.with(this@MenuFragment).load(R.drawable.profile).into(fragmentMenuBinding.iconAccounts)
            Glide.with(this@MenuFragment).load(R.drawable.settings).into(fragmentMenuBinding.iconHistory)
            Glide.with(this@MenuFragment).load(R.drawable.sos1).into(fragmentMenuBinding.iconShareus)
            Glide.with(this@MenuFragment).load(R.drawable.logout).into(fragmentMenuBinding.iconLogout)

            fragmentMenuBinding.textPost.isSelected = false
            fragmentMenuBinding.textAccounts.isSelected = false
            fragmentMenuBinding.textHistory.isSelected = false
            fragmentMenuBinding.textShareus.isSelected = true
            fragmentMenuBinding.textLogout.isSelected = false

        }

        fragmentMenuBinding.lilLogout.setOnClickListener { view5 ->
            if (MainActivity.HomeFragmentHandler != null) {
                MainActivity.HomeFragmentHandler.sendEmptyMessage(4)
            }
            Glide.with(this@MenuFragment).load(R.drawable.home).into(fragmentMenuBinding.iconPost)
            Glide.with(this@MenuFragment).load(R.drawable.profile).into(fragmentMenuBinding.iconAccounts)
            Glide.with(this@MenuFragment).load(R.drawable.settings).into(fragmentMenuBinding.iconHistory)
            Glide.with(this@MenuFragment).load(R.drawable.sos1).into(fragmentMenuBinding.iconShareus)
            Glide.with(this@MenuFragment).load(R.drawable.logout).into(fragmentMenuBinding.iconLogout)

            fragmentMenuBinding.textPost.isSelected = false
            fragmentMenuBinding.textAccounts.isSelected = false
            fragmentMenuBinding.textHistory.isSelected = false
            fragmentMenuBinding.textShareus.isSelected = false
            fragmentMenuBinding.textLogout.isSelected = true


        }
    }

    companion object {


        @SuppressLint("StaticFieldLeak")
        lateinit var fragmentMenuBinding: FragmentMenuBinding
    }
}

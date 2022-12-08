package id.celenganku.app.ui.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import id.celenganku.app.R
import id.celenganku.app.base.BaseFragment
import id.celenganku.app.databinding.MainFeatureFragmentBinding
import id.celenganku.app.ui.home.current.CurrentFragment
import id.celenganku.app.ui.home.done.SavingDoneFragment
import id.celenganku.app.utils.PreferenceHelper
import id.celenganku.app.utils.changeTheme

class HomeFragment : BaseFragment<MainFeatureFragmentBinding>(MainFeatureFragmentBinding::inflate) {

    private lateinit var preference: PreferenceHelper

    override fun renderView(context: Context, savedInstanceState: Bundle?) {
        preference = PreferenceHelper(context)

        binding.toolbar.apply {
            inflateMenu(R.menu.home)
            setOnMenuItemClickListener {
                showThemeOptionDialog()
                true
            }
        }
        binding.viewPager.adapter = ViewPagerAdapter()

        val tabsTitle = listOf("Berlangsung", "Tercapai")
        TabLayoutMediator(binding.tabLayout, binding.viewPager){tab,position ->
            tab.text = tabsTitle[position]
        }.attach()
    }

    private fun showThemeOptionDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Pilih Tema")
            .setSingleChoiceItems(arrayOf("Default", "Terang", "Gelap"), preference.theme){dialog, which ->
                preference.theme = which
                changeTheme(preference)
                dialog.dismiss()
            }
            .show()
    }

    inner class ViewPagerAdapter  : FragmentStateAdapter(
        childFragmentManager, viewLifecycleOwner.lifecycle
    ) {

        private val fragmentList = listOf(
            CurrentFragment(),
            SavingDoneFragment()
        )

        override fun getItemCount(): Int = fragmentList.size

        override fun createFragment(position: Int): Fragment = fragmentList[position]
    }
}
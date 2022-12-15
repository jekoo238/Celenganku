package id.celenganku.app.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import id.celenganku.app.R
import id.celenganku.app.base.BaseFragment
import id.celenganku.app.databinding.MainFeatureFragmentBinding
import id.celenganku.app.ui.home.current.CurrentFragment
import id.celenganku.app.ui.home.done.SavingDoneFragment
import id.celenganku.app.utils.*
import org.koin.android.ext.android.inject

class HomeFragment : BaseFragment<MainFeatureFragmentBinding>(MainFeatureFragmentBinding::inflate) {

    private val dataStore: DataStore<Preferences> by inject()

    override fun renderView(context: Context, savedInstanceState: Bundle?) {
        binding.toolbar.apply {
            inflateMenu(R.menu.home)
            setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.change_theme -> showThemeOptionDialog()
                    else -> openPlayStore()
                }
                true
            }
        }
        binding.viewPager.adapter = ViewPagerAdapter()

        val tabsTitle = listOf("Berlangsung", "Tercapai")
        TabLayoutMediator(binding.tabLayout, binding.viewPager){tab,position ->
            tab.text = tabsTitle[position]
        }.attach()
    }

    private fun openPlayStore() {
        try {
            startActivity(
                Intent(Intent.ACTION_VIEW)
                    .setData("https://play.google.com/store/apps/details?id=id.celenganku.app".toUri())
                    .setPackage("com.android.vending")
            )
        }catch (e: Exception) {
            showToast("Error : ${e.message}")
        }
    }

    private fun showThemeOptionDialog() {
        dataStore.getData(lifecycleScope) { pref ->
            val theme = pref.currentTheme()

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Pilih Tema")
                .setSingleChoiceItems(arrayOf("Default", "Terang", "Gelap"), theme){dialog, which ->
                    dialog.dismiss()
                    dataStore.editData(lifecycleScope) { mutablePref ->
                        mutablePref.setCurrentTheme(which)
                    }
                }
                .show()
        }
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
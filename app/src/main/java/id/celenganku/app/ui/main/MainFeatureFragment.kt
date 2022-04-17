package id.celenganku.app.ui.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.Hold
import id.celenganku.app.R
import id.celenganku.app.databinding.MainFeatureFragmentBinding
import id.celenganku.app.ui.home.CurrentFragment
import id.celenganku.app.ui.hsavingsHistory.FinishedSavingFragment
import id.celenganku.app.utils.PreferenceHelper
import id.celenganku.app.utils.changeTheme
import java.util.concurrent.TimeUnit

class MainFeatureFragment : Fragment() {

    private var _binding: MainFeatureFragmentBinding? = null
    private val binding: MainFeatureFragmentBinding get() = _binding!!
    private lateinit var preference: PreferenceHelper

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.changeTheme -> {
                showThemeOptionDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        exitTransition = Hold().apply {
            addTarget(R.id.container)
            duration = 300L
        }
        preference = PreferenceHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = MainFeatureFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition(300, TimeUnit.MILLISECONDS)
        binding.viewPager.adapter = ViewPagerAdapter()
        val tabsTitle = listOf("Berlangsung", "Tercapai")
        TabLayoutMediator(binding.tabLayout, binding.viewPager){tab,position ->
            tab.text = tabsTitle[position]
        }.attach()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class ViewPagerAdapter : FragmentStateAdapter(this) {

        private val fragmentList = listOf(
            CurrentFragment(),
            FinishedSavingFragment()
        )

        override fun getItemCount(): Int = fragmentList.size

        override fun createFragment(position: Int): Fragment = fragmentList[position]
    }
}
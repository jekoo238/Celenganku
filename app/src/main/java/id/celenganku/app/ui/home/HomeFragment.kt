package id.celenganku.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.FadeProvider
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import id.celenganku.app.R
import id.celenganku.app.databinding.MainFeatureFragmentBinding
import id.celenganku.app.ui.home.current.CurrentFragment
import id.celenganku.app.ui.home.done.SavingDoneFragment
import id.celenganku.app.utils.PreferenceHelper
import id.celenganku.app.utils.changeTheme

class HomeFragment : Fragment() {

    private var _binding: MainFeatureFragmentBinding? = null
    private val binding: MainFeatureFragmentBinding get() = _binding!!
    private lateinit var preference: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            secondaryAnimatorProvider = FadeProvider()
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z,false)
        preference = PreferenceHelper(requireContext())
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFeatureFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class ViewPagerAdapter : FragmentStateAdapter(this) {

        private val fragmentList = listOf(
            CurrentFragment(),
            SavingDoneFragment()
        )

        override fun getItemCount(): Int = fragmentList.size

        override fun createFragment(position: Int): Fragment = fragmentList[position]
    }
}
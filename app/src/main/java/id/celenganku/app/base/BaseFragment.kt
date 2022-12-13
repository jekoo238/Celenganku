package id.celenganku.app.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.transition.FadeProvider
import com.google.android.material.transition.MaterialSharedAxis

abstract class BaseFragment<T : ViewBinding>(
    private val setupBinding: (LayoutInflater, ViewGroup?, Boolean) -> T
) : Fragment() {

    private var _binding: T? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = setupBinding(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) Log.v("State", "Restored From Previous State")
        renderView(view.context, savedInstanceState)
    }

    abstract fun renderView(context: Context, savedInstanceState: Bundle?)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = 600
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            secondaryAnimatorProvider = FadeProvider()
            duration = 600
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z,false).apply {
            duration = 600
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = 600
        }
    }
}
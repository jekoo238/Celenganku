package id.celenganku.app.ui.home.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import id.celenganku.app.R
import id.celenganku.app.databinding.CurrentFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class CurrentFragment : Fragment() {

    private val viewModel: CurrentViewModel by viewModel()
    private var _binding: CurrentFragmentBinding? = null
    private val binding: CurrentFragmentBinding get() = _binding!!
    private lateinit var adapter: CurrentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = CurrentAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CurrentFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = adapter
        viewModel.allSaving.observe(viewLifecycleOwner){
            if (it.isEmpty()) binding.emptyData.isVisible = true
            adapter.submitList(it)
        }

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFeatureFragment_to_addSavingFragment)
        }

        binding.recyclerView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) binding.floatingActionButton.shrink() else binding.floatingActionButton.extend()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
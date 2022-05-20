package id.celenganku.app.ui.home.done

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import id.celenganku.app.databinding.HistoryFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class

SavingDoneFragment : Fragment() {

    private val viewModel: SavingDoneViewModel by viewModel()
    private var _binding: HistoryFragmentBinding? = null
    private val binding: HistoryFragmentBinding get() = _binding!!
    private val adapter = SavingDoneAdapter()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = HistoryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.historyRv.adapter = adapter
        viewModel.finishedSaving.observe(viewLifecycleOwner){
            if (it.isEmpty()) binding.emptyData.isVisible = true
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
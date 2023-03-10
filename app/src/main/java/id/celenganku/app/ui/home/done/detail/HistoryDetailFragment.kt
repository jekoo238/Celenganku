package id.celenganku.app.ui.home.done.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import id.celenganku.app.databinding.HistoryDetailFragmentBinding
import id.celenganku.app.ui.home.current.detail.SavingDetailAdapter
import id.celenganku.app.utils.format
import id.celenganku.app.utils.formatNumber
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class HistoryDetailFragment : Fragment() {

    private val viewModel: HistoryDetailViewModel by viewModel()
    private var _binding: HistoryDetailFragmentBinding? = null
    private val binding: HistoryDetailFragmentBinding get() = _binding!!
    private val args: HistoryDetailFragmentArgs by navArgs()
    private lateinit var savingAdapter: SavingDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        savingAdapter = SavingDetailAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HistoryDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.historyScroll.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            binding.appBarLayout.isLifted = scrollY > 20
        }
        viewModel.savingId = args.savingId
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.listSavingLogsRv.apply {
            isNestedScrollingEnabled = false
            adapter = savingAdapter
        }

        viewModel.savingsLogs.observe(viewLifecycleOwner){
            savingAdapter.submitList(it)
        }

        viewModel.savingsDetail.observe(viewLifecycleOwner){
            it?.let { saving ->
                with(binding){

                    toolbar.title = saving.title

                    if (saving.image != null){
                        savingImage.setImageURI(saving.image.toUri())
                    }
                    target.text = formatNumber(saving.target)
                    created2.text = saving.dateCreated.format("dd MMM yyyy")
                    complete.text = saving.dateFinished?.format("dd MMM yyyy")
                    saving.dateFinished?.let { finished ->
                        val day = TimeUnit.MILLISECONDS.toDays(finished-saving.dateCreated)
                        binding.completeCount.text = "Tercapai Dalam Waktu $day Hari"
                    }

                    binding.deleteButton.setOnClickListener {
                        MaterialAlertDialogBuilder(requireContext()).apply {
                            setTitle("Hapus tabungan?")
                            setPositiveButton("Hapus"){ dialog, _ ->
                                viewModel.deleteSavingById(args.savingId, saving.image)
                                findNavController().navigateUp()
                            }
                            setNegativeButton("Batal", null)
                            create()
                            show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
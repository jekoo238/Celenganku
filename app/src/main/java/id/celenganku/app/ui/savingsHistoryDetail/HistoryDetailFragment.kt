package id.celenganku.app.ui.savingsHistoryDetail

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialSharedAxis
import com.squareup.picasso.Picasso
import id.celenganku.app.databinding.HistoryDetailFragmentBinding
import id.celenganku.app.ui.savingsDetail.SavingDetailAdapter
import id.celenganku.app.utils.formatNumber
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class HistoryDetailFragment : Fragment() {

    private val viewModel: HistoryDetailViewModel by viewModel()
    private var _binding: HistoryDetailFragmentBinding? = null
    private val binding: HistoryDetailFragmentBinding get() = _binding!!
    private val args: HistoryDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
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
        viewModel.savingId = args.savingId

        val savingAdapter = SavingDetailAdapter()
        binding.listSavingLogsRv.adapter = savingAdapter

        viewModel.savingsLogs.observe(viewLifecycleOwner){
            savingAdapter.submitList(it)
        }

        viewModel.savingsDetail.observe(viewLifecycleOwner){
            it?.let { saving ->
                with(binding){
                    if (saving.image != null){
                        Picasso.get().load(Uri.parse(saving.image)).into(savingImage)
                    }
                    target.text = formatNumber(saving.target)
                    saving.dateFinished?.let { finished ->
                        val day = TimeUnit.MILLISECONDS.toDays(finished-saving.dateCreated)
                        completeDay.text = "Tercapai Dalam Waktu $day Hari"
                    }

                    fabOptions.setOnClickListener {
                        AlertDialog.Builder(requireContext()).apply {
                            setMessage("Hapus tabungan?")
                            setPositiveButton("Hapus"){ dialog, _ ->
                                viewModel.deleteSavingById(args.savingId, saving.image)

                                dialog.dismiss()
                                findNavController().navigateUp()
                            }
                            setNegativeButton("Batal"){ dialog, _ ->
                                dialog.dismiss()
                            }
                            create()
                            show()
                        }}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
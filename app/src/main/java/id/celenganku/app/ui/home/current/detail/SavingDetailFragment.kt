package id.celenganku.app.ui.home.current.detail

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialFade
import id.celenganku.app.base.BaseFragment
import id.celenganku.app.databinding.DialogSavingsFormBinding
import id.celenganku.app.databinding.FragmentSavingDoneBinding
import id.celenganku.app.databinding.SavingDetailFragmentBinding
import id.celenganku.app.model.SavingFormModel
import id.celenganku.app.model.SavingsEntity
import id.celenganku.app.model.SavingsLogEntity
import id.celenganku.app.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.math.roundToInt

class SavingDetailFragment : BaseFragment<SavingDetailFragmentBinding>(
    SavingDetailFragmentBinding::inflate
) {

    private val viewModel: SavingDetailViewModel by viewModel()
    private val args: SavingDetailFragmentArgs by navArgs()
    private lateinit var savingAdapter: SavingDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        savingAdapter = SavingDetailAdapter()
    }

    override fun renderView(context: Context, savedInstanceState: Bundle?) {
        viewModel.savingId = args.savingId

        binding.detailContent.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            binding.appBarLayout.isLifted = scrollY > 20
        }
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        setupFab()
        binding.listSavingLogsRv.adapter = savingAdapter

        viewModel.savingsDetail.observe(viewLifecycleOwner){
            if (it != null){
                setupView(it)
            }else{
                showToast("Terjadi Kesalahan")
            }
        }

        viewModel.savingsLogs.observe(viewLifecycleOwner){
            savingAdapter.submitList(it)
        }

        binding.removeSavingsLayout.setOnClickListener {
            viewModel.setShowOptionFab()
            showDeleteDialog()
        }
    }

    private fun setupView(saving: SavingsEntity) {
        with(binding){
            toolbar.title = saving.title

            if (saving.image != null){
                savingImage.setImageURI(saving.image.toUri())
            }
            target.text = formatNumber(saving.target)
            collected.text = formatNumber(saving.collected)

            targetProgressBar.apply {
                max = saving.target
                setProgressCompat(saving.collected, true)
            }

            val percent = ((saving.collected.toFloat()/saving.target.toFloat())*100F).roundToInt()
            if (percent == 100) showCompleteDialog()
            percentage.text = "$percent%"
            mins.text = formatNumber(saving.target-saving.collected)
            val suffix = when (saving.fillingType) {
                0L -> "Perhari"
                1L -> "Perminggu"
                else -> "Perbulan"
            }
            targetPerDay.text = "${formatNumber(saving.targetPerDay)} $suffix"
            val estimationDay = ((saving.target-saving.collected)/saving.targetPerDay)
            created.text = saving.dateCreated.format("dd MMM yyyy")
            estimation.text = "$estimationDay ${saving.fillingTypeText}"
        }

        binding.addSavingsLayout.setOnClickListener{
            val form = SavingFormModel("Menabung", saving.target, saving.collected, true)
            showSavingFormDialog(form)
        }
        binding.minSavingsLayout.setOnClickListener{
            val form = SavingFormModel("Ambil Tabungan", saving.target, saving.collected, false)
            showSavingFormDialog(form)
        }

    }

    private fun showCompleteDialog() {
        val dialogView = FragmentSavingDoneBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setCancelable(false)
            setView(dialogView.root)
        }.show()
        dialogView.closeButton.setOnClickListener {
            dialog.dismiss()
            findNavController().navigateUp()
        }
    }

    private fun setupFab() {
        binding.fabOptions.setOnClickListener {
            viewModel.setShowOptionFab()
        }
        binding.fabClickedBg.setOnClickListener {
            viewModel.setShowOptionFab()
        }
        viewModel.showOptionFab.observe(viewLifecycleOwner){ isFabOpen ->
            binding.fabOptions.isSelected = isFabOpen
            binding.fabClickedBg.isVisible = isFabOpen
            TransitionManager.beginDelayedTransition(binding.root, MaterialFade())
            binding.addSavingsLayout.isVisible = isFabOpen
            binding.minSavingsLayout.isVisible = isFabOpen
            binding.removeSavingsLayout.isVisible = isFabOpen
        }


    }

    private fun showSavingFormDialog(form: SavingFormModel){
        if (!form.isIncrease && form.collected < 1){
            Toast.makeText(requireContext(), "Belum ada tabungan yang terkumpul", Toast.LENGTH_SHORT).show()
            viewModel.setShowOptionFab()
            return
        }

        viewModel.setShowOptionFab()

        val formView = DialogSavingsFormBinding.inflate(LayoutInflater.from(requireContext()))
        formView.nominalEt.addAutoConverterToMoneyFormat(formView.nominalLayout)

        val formDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(form.title)
            .setCancelable(false)
            .setView(formView.root)
            .setPositiveButton("Simpan", null)
            .setNegativeButton("Batal", null)
            .showWithSoftInput()

        formDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val nominalValue = formView.nominalEt.text.getNumber()
            val notes = formView.infoEt.text?.toString()

            if (nominalValue < 1){
                formView.nominalLayout.apply {
                    error = "Nominal tidak boleh kosong"
                    requestFocus()
                }
                return@setOnClickListener
            }

            if (form.isIncrease && nominalValue > (form.target-form.collected)){
                formView.nominalLayout.error = "Nominal yang ditabung melebihi kekurangan"
                return@setOnClickListener
            }

            if (!form.isIncrease && nominalValue > form.collected){
                formView.nominalLayout.error = "Nominal tidak boleh melebihi total tabungan terkumpul"
                return@setOnClickListener
            }

            val savLogEntity = SavingsLogEntity(
                logId = null,
                savingsId = args.savingId,
                timestamp = Calendar.getInstance().timeInMillis,
                nominal = nominalValue,
                increase = form.isIncrease,
                notes = notes
            )

            formDialog.dismiss()
            if (form.isIncrease && nominalValue == (form.target-form.collected)){
                viewModel.completeSavings(savLogEntity, Calendar.getInstance().timeInMillis)
            }else{
                viewModel.addOrMinSaving(savLogEntity)
            }
        }
    }

    private fun showDeleteDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hapus tabungan?")
            .setPositiveButton("Hapus"){ _, _ ->
                viewModel.deleteSavings{ status ->
                    when(status){
                        is SUCCESS -> {
                            showToast("Berhasil dihapus")
                            findNavController().navigateUp()
                        }
                        is ERROR -> showToast(status.message)
                    }
                }
            }
            .setNegativeButton("Batal"){ _, _ ->}
            .show()
    }
}
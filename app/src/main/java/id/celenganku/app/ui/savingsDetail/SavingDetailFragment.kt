package id.celenganku.app.ui.savingsDetail

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.AlertDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.use
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform
import com.squareup.picasso.Picasso
import id.celenganku.app.R
import id.celenganku.app.databinding.DialogSavingsFormBinding
import id.celenganku.app.databinding.SavingDetailFragmentBinding
import id.celenganku.app.model.SavingFormModel
import id.celenganku.app.model.SavingsEntity
import id.celenganku.app.model.SavingsLogEntity
import id.celenganku.app.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SavingDetailFragment : Fragment() {

    private val viewModel: SavingDetailViewModel by viewModel()
    private var _binding: SavingDetailFragmentBinding? = null
    private val binding: SavingDetailFragmentBinding get() = _binding!!
    private var isFabOpen: Boolean = false
    private val args: SavingDetailFragmentArgs by navArgs()
    private lateinit var savingAdapter: SavingDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.navHost
            duration = 300L
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().obtainStyledAttributes(intArrayOf(R.attr.colorSurface)).use {
                it.getColor(0, Color.MAGENTA)
            })
        }
        savingAdapter = SavingDetailAdapter()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = SavingDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.savingId = args.savingId
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
            if (saving.image != null){
                Picasso.get().load(Uri.parse(saving.image)).into(savingImage)
            }
            target.text = formatNumber(saving.target)
            collected.text = formatNumber(saving.collected)
            targetProgressBar.apply {
                max = saving.target
                progress = saving.collected
            }
            mins.text = formatNumber(saving.target-saving.collected)
            targetPerDay.text = "${formatNumber(saving.targetPerDay)} / Hari"
            val estimationDay = ((saving.target-saving.collected)/saving.targetPerDay)
            estimation.text = "$estimationDay Hari Lagi"
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

    private fun setupFab() {
        binding.fabOptions.setOnClickListener {
            viewModel.setShowOptionFab()
        }
        binding.fabClickedBg.setOnClickListener {
            viewModel.setShowOptionFab()
        }
        viewModel.showOptionFab.observe(viewLifecycleOwner){ isShow ->
            isFabOpen = isShow
            if (isShow){
                showOptionsFab()
            }else{
                hideOptionsFab()
            }
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
            .setNegativeButton("Batal"){ _, _ ->}
            .showWithSoftInput()

        formDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val nominalValue = formView.nominalEt.text.getNumber()
            val notes = formView.infoEt.text?.toString()

            if (nominalValue < 0){
                formView.nominalLayout.error = "Nominal tidak boleh kosong"
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

            if (form.isIncrease && nominalValue == (form.target-form.collected)){
                viewModel.completeSavings(savLogEntity, Calendar.getInstance().timeInMillis)
            }else{
                viewModel.addOrMinSaving(savLogEntity)
            }
            formDialog.dismiss()
        }
    }

    private fun showDeleteDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Hapus tabungan?")
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

    private fun hideOptionsFab() {
        with(binding){
            fabOptions.isSelected = false
            addSavingsLayout.animate().translationY(0f)
            minSavingsLayout.animate().translationY(0f)
            removeSavingsLayout.animate().translationY(0f).setListener(object : AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    if (!isFabOpen){
                        addSavingsLayout.visibility = View.GONE
                        minSavingsLayout.visibility = View.GONE
                        removeSavingsLayout.visibility = View.GONE
                    }
                }
            })
            binding.fabClickedBg.visibility = View.GONE
        }
    }

    private fun showOptionsFab() {
        with(binding){
            fabOptions.isSelected = true
            binding.fabClickedBg.visibility = View.VISIBLE
            addSavingsLayout.visibility = View.VISIBLE
            minSavingsLayout.visibility = View.VISIBLE
            removeSavingsLayout.visibility = View.VISIBLE
            addSavingsLayout.animate().translationY(
                resources.getDimension(R.dimen.value_350).unaryMinus())
            minSavingsLayout.animate().translationY(
                resources.getDimension(R.dimen.value_250).unaryMinus())
            removeSavingsLayout.animate().translationY(
                resources.getDimension(R.dimen.value_150).unaryMinus())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package id.celenganku.app.ui.form

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.celenganku.app.R
import id.celenganku.app.base.BaseFragment
import id.celenganku.app.databinding.DialogChoosePhotoBinding
import id.celenganku.app.databinding.SavingFormFragmentBinding
import id.celenganku.app.model.SavingsEntity
import id.celenganku.app.ui.imagecrop.ImageCropFragment
import id.celenganku.app.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.util.*

class SavingFormFragment : BaseFragment<SavingFormFragmentBinding>(SavingFormFragmentBinding::inflate) {

    private val viewModel: SavingFormViewModel by viewModel()
    private lateinit var requestPickImageFromGallery: ActivityResultLauncher<String>
    private lateinit var requestPickImageFromCamera: ActivityResultLauncher<String>

    override fun renderView(context: Context, savedInstanceState: Bundle?) {
        savedInstanceState?.parcelable<Uri>("imageUri")?.let {
            binding.savingImage.setImageURI(it)
            viewModel.imageUri = it
        }

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.formContent.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            binding.appBarLayout.isLifted = scrollY > 20
        }
        binding.target.addAutoConverterToMoneyFormat(binding.targetLayout)
        binding.targetPerDay.addAutoConverterToMoneyFormat(binding.targetPerDayLayout)

        binding.savingImage.setOnClickListener {
            showImageChooserDialog()
        }

        binding.saveButton.setOnClickListener {
            saveSavings()
        }

        setFragmentResultListener(ImageCropFragment.CROP_IMAGE_RESULT){ _, bundle ->
            bundle.parcelable<Uri>(ImageCropFragment.CROPPED_BITMAP).let {
                viewModel.imageUri = it
                binding.savingImage.setImageURI(it)
            }
        }
    }

    private fun openImageCropFragment(uri: Uri?) {
        uri ?: return
        val direction = SavingFormFragmentDirections.actionAddSavingFragmentToImageCropFragment(uri)
        findNavController().navigate(direction)
    }

    private fun showImageChooserDialog() {

        val chooserBinding = DialogChoosePhotoBinding.inflate(layoutInflater)

        val dialog = BottomSheetDialog(requireContext()).apply {
            setContentView(chooserBinding.root)
            behavior.maxWidth = 480.dotPixel()
        }

        chooserBinding.root.doOnPreDraw {
            dialog.behavior.peekHeight = it.height
        }

        chooserBinding.camera.setOnClickListener {
            dialog.dismiss()
            try {
                requestPickImageFromCamera.launch("image_captured.jpg")
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }

        chooserBinding.gallery.setOnClickListener {
            dialog.dismiss()
            try {
                requestPickImageFromGallery.launch("image/*")
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }

        dialog.show()
    }

    private fun saveSavings() {
        val title = binding.title.text
        val target = binding.target.text.getNumber()
        val targetPerDay = binding.targetPerDay.text.getNumber()

        if (title.isNullOrEmpty()){
            binding.titleLayout.apply {
                error = "Nama tabungan harus diisi"
                requestFocus()
            }
            return
        }

        if (target < 1 ){
            binding.targetLayout.error = "Target tidak boleh kosong"
            return
        }

        if (targetPerDay < 1){
            binding.targetPerDayLayout.error = "Target per hari tidak boleh kosong"
            return
        }

        if (targetPerDay >= target){
            binding.targetPerDayLayout.error = "Target per hari harus kurang dari target tabungan"
            return
        }

        binding.savingProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO){
            try {
                val imageUri = viewModel.imageUri?.let { convertCacheImageToExternalFileImage(it) }
                val savingModel =  SavingsEntity(
                    title.toString(),
                    imageUri?.toString(),
                    target.toString().toInt(),
                    targetPerDay.toString().toInt(),
                    0,
                    Calendar.getInstance().timeInMillis,
                    null,
                    getCheckedIndex()
                )
                viewModel.addSaving(savingModel)

                withContext(Dispatchers.Main){
                    findNavController().navigateUp()
                }
            } catch (e: Exception){
                withContext(Dispatchers.Main){
                    showToast("Error: ${e.message}")
                }
            }
        }
    }

    private fun convertCacheImageToExternalFileImage(cacheImageUri: Uri): Uri {
        val fileName = "${Calendar.getInstance().timeInMillis}+${Random().nextInt(100)}.webp"
        val parentPath = requireContext()
            .getExternalFilesDirs(DIRECTORY_PICTURES)
            .first()
            .absolutePath

        val destinationFile = File(parentPath, fileName)

        FileOutputStream(destinationFile).use { out ->
            requireContext().contentResolver.openInputStream(cacheImageUri).use {
                it?.copyTo(out)
            }
        }

        return destinationFile.toUri()
    }

    private fun getCheckedIndex() = when(binding.fillingType.checkedButtonId) {
        R.id.dailyButton -> 0L
        R.id.weeklyButton -> 1L
        else -> 3L
    }

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        requestPickImageFromCamera = registerForActivityResult(TakePicture()) {
            openImageCropFragment(it)
        }

        requestPickImageFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) {
            openImageCropFragment(it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("imageUri", viewModel.imageUri)
    }
}
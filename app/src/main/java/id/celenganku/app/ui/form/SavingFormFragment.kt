package id.celenganku.app.ui.form

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import id.celenganku.app.R
import id.celenganku.app.databinding.SavingFormFragmentBinding
import id.celenganku.app.model.SavingsEntity
import id.celenganku.app.utils.addAutoConverterToMoneyFormat
import id.celenganku.app.utils.getNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.util.*

class SavingFormFragment : Fragment() {

    private val viewModel: SavingFormViewModel by viewModel()
    private var _binding: SavingFormFragmentBinding? = null
    private val binding: SavingFormFragmentBinding get() = _binding!!
    private var requestCreator: RequestCreator? = null

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = SavingFormFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun saveSavings() {
        val title = binding.title.text
        val target = binding.target.text.getNumber()
        val targetPerDay = binding.targetPerDay.text.getNumber()

        if (title.isNullOrEmpty() or title.isNullOrEmpty()){
            binding.titleLayout.error = "Nama tabungan harus diisi"
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

        val imageName = Calendar.getInstance().timeInMillis.toString() +"_"+ Random().nextInt(100).toString() + ".webp"

        lifecycleScope.launch(Dispatchers.IO){
            var uri: Uri? = null

            if (requestCreator != null){
                val image = File(ContextCompat.getExternalFilesDirs(requireContext(), DIRECTORY_PICTURES).first().absolutePath, imageName)
                val fos = FileOutputStream(image)
                @Suppress("DEPRECATION")
                requestCreator?.get()?.compress(Bitmap.CompressFormat.WEBP, 100, fos)
                fos.close()
                uri = Uri.fromFile(image)
            }

            val savingModel =  SavingsEntity(
                null,
                title.toString(),
                uri?.toString(),
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
        }


    }

    private fun getCheckedIndex() = when(binding.fillingType.checkedButtonId) {
        R.id.dailyButton -> 0L
        R.id.weeklyButton -> 1L
        else -> 3L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.target.addAutoConverterToMoneyFormat(binding.targetLayout)
        binding.targetPerDay.addAutoConverterToMoneyFormat(binding.targetPerDayLayout)

        binding.pickImageButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                pickImageFromGallery()
            }else{
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
        }

        binding.saveButton.setOnClickListener {
            saveSavings()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.first() == PackageManager.PERMISSION_GRANTED){
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery(){
        Intent(Intent.ACTION_PICK).apply {
            type = "image/"
        }.also {
            startActivityForResult(it, 2)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 2){
            val uri = data?.data
            requestCreator = Picasso.get().load(uri)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .centerCrop()
                    .resize(binding.savingImage.width, binding.savingImage.height)

            requestCreator?.into(binding.savingImage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
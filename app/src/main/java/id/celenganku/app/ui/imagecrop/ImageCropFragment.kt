package id.celenganku.app.ui.imagecrop

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import id.celenganku.app.R
import id.celenganku.app.base.BaseFragment
import id.celenganku.app.databinding.FragmentImageCropBinding
import id.celenganku.app.utils.showToast
import id.celenganku.app.utils.webp

class ImageCropFragment : BaseFragment<FragmentImageCropBinding>(
    FragmentImageCropBinding::inflate
){

    private val args: ImageCropFragmentArgs by navArgs()

    override fun renderView(context: Context, savedInstanceState: Bundle?) {
        with(binding.toolbar){
            inflateMenu(R.menu.crop_image)
            setNavigationOnClickListener { findNavController().navigateUp() }
            setOnMenuItemClickListener {
                with(binding.cropImageView){
                    when(it.itemId){
                        R.id.rotate -> rotateImage(90)
                        else -> croppedImageAsync(
                            saveCompressFormat = webp(),
                            reqWidth = 1280,
                            reqHeight = 720
                        )
                    }
                }
                true
            }
        }

        binding.cropImageView.apply {
            setImageUriAsync(args.uri)
            setOnCropImageCompleteListener{ _, result ->
                if (!result.isSuccessful){
                    showToast(result.error?.localizedMessage)
                    return@setOnCropImageCompleteListener
                }

                setFragmentResult(CROP_IMAGE_RESULT, Bundle().apply {
                    putParcelable(CROPPED_BITMAP, result.uriContent)
                })

                findNavController().popBackStack()
            }
        }
    }

    companion object{
        const val CROP_IMAGE_RESULT = "crop_image_result"
        const val CROPPED_BITMAP = "bitmap"
    }
}
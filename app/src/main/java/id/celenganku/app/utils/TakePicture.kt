package id.celenganku.app.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.FileProvider
import java.io.File

class TakePicture: ActivityResultContract<String, Uri?>() {

    private lateinit var imageUri: Uri

    override fun createIntent(context: Context, input: String): Intent {
        val imgFile = File(context.cacheDir, input)
        imageUri = FileProvider.getUriForFile(context, "id.celenganku.app", imgFile)
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            .putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode == Activity.RESULT_OK) {
            return imageUri
        }
        return null
    }
}

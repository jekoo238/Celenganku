package id.celenganku.app.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun Long.format(pattern: String): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(this))
}

fun changeTheme(preference: PreferenceHelper) {
    val mode = when(preference.theme){
        1 -> AppCompatDelegate.MODE_NIGHT_NO
        2 -> AppCompatDelegate.MODE_NIGHT_YES
        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
    AppCompatDelegate.setDefaultNightMode(mode)
}

fun Editable?.getNumber(): Int = this?.filter { it.isDigit() }?.toString()?.toIntOrNull() ?: 0

fun Fragment.hideSoftKeyboardIfAvailable(){
    with(requireActivity()){
        currentFocus?.windowToken?.let {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it, 0)
        }
    }
}


fun TextInputEditText.addAutoConverterToMoneyFormat(layout: TextInputLayout){
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.filter { it.isDigit() }?.toString()?.let {
                removeTextChangedListener(this)
                if (it.isNotEmpty()){
                    val result = it.toIntOrNull()
                    if (result != null){
                        val format = NumberFormat.getInstance(Locale("in")).format(result)
                        setText(format)
                        setSelection(format.length)
                    }else{
                        layout.error = "Melebihi kapasitas"
                    }
                }else{
                    text?.clear()
                }
                addTextChangedListener(this)
                layout.error = null
            }
        }
        override fun afterTextChanged(s: Editable?) {}
    })
}

fun MaterialAlertDialogBuilder.showWithSoftInput(): AlertDialog {
    with(show()){
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        return this
    }
}

fun formatNumber(number: Int): String{
    val formatter = NumberFormat.getNumberInstance(Locale("in")).format(number)
    return "Rp. $formatter"
}

fun formatNumberTok(number: Int): String{
    return NumberFormat.getNumberInstance(Locale("in")).format(number)
}

fun Fragment.showToast(message: String?){
    Toast.makeText(requireContext(), message ?: "Unknown message", Toast.LENGTH_SHORT).show()
}


fun ViewModel.runInBackground(action: suspend CoroutineScope.()-> Unit) =
    viewModelScope.launch(Dispatchers.IO){ action() }

fun Fragment.runInBackground(action: suspend CoroutineScope.()-> Unit) =
    lifecycleScope.launch(Dispatchers.IO){ action() }

suspend fun switchToMain(action: suspend CoroutineScope.()-> Unit) =
    withContext(Dispatchers.Main){ action() }

fun Int.dotPixel() = (this.toFloat() * Resources.getSystem().displayMetrics.density).toInt()

inline fun <reified T: Parcelable> Bundle.parcelable(key: String): T? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        return getParcelable(key, T::class.java)
    }
    return getParcelable(key)
}

fun webp(): CompressFormat {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        return CompressFormat.WEBP_LOSSLESS
    }
    return CompressFormat.WEBP
}

fun Bitmap.compressToWebP(stream: OutputStream) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        compress(Bitmap.CompressFormat.WEBP_LOSSY, 90, stream)
        return
    }
    compress(Bitmap.CompressFormat.WEBP, 90, stream)
}
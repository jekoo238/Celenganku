package id.celenganku.app.ui.form

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.celenganku.app.db.SavingDao
import id.celenganku.app.model.SavingsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavingFormViewModel(private val savingDao: SavingDao) : ViewModel() {

    var imageUri: Uri? = null

    fun addSaving(savings: SavingsEntity) = viewModelScope.launch(Dispatchers.IO){
        try {
            savingDao.addSaving(savings)
        }catch (e: Exception){

        }
    }
}
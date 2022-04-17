package id.celenganku.app.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.celenganku.app.db.SavingDao
import id.celenganku.app.model.SavingsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddSavingViewModel(private val savingDao: SavingDao) : ViewModel() {

    fun addSaving(savings: SavingsEntity) = viewModelScope.launch(Dispatchers.IO){
        try {
            savingDao.addSaving(savings)
        }catch (e: Exception){

        }
    }
}
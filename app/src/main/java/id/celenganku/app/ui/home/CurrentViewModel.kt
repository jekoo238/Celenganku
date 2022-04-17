package id.celenganku.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import id.celenganku.app.db.SavingDao
import id.celenganku.app.model.SavingsEntity

class CurrentViewModel(private val savingDao: SavingDao) : ViewModel() {

    val allSaving: LiveData<List<SavingsEntity>>
        get() = savingDao.getAllSaving()
}
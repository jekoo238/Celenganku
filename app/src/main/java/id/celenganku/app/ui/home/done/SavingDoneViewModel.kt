package id.celenganku.app.ui.home.done

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import id.celenganku.app.db.SavingDao
import id.celenganku.app.model.SavingsEntity

class SavingDoneViewModel(private val savingDao: SavingDao) : ViewModel() {

    val finishedSaving: LiveData<List<SavingsEntity>> get() = savingDao.getFinishedSaving()
}
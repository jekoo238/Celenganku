package id.celenganku.app.ui.hsavingsHistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import id.celenganku.app.db.SavingDao
import id.celenganku.app.model.SavingsEntity

class HistoryViewModel(private val savingDao: SavingDao) : ViewModel() {

    val finishedSaving: LiveData<List<SavingsEntity>> get() = savingDao.getFinishedSaving()
}
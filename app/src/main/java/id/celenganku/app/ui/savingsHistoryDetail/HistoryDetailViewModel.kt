package id.celenganku.app.ui.savingsHistoryDetail

import android.net.Uri
import androidx.lifecycle.*
import id.celenganku.app.db.SavingDao
import id.celenganku.app.model.SavingsEntity
import id.celenganku.app.model.SavingsLogEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class HistoryDetailViewModel(private val savingDao: SavingDao) : ViewModel() {

    var savingId = 0
    val savingsLogs: LiveData<List<SavingsLogEntity>> get() = savingDao.getSavingsLogs(savingId)
    val savingsDetail: LiveData<SavingsEntity?> get() = savingDao.getSavingById(savingId)

    fun deleteSavingById(id: Int, image: String?) = viewModelScope.launch(Dispatchers.IO){
        try {
            savingDao.deleteSavingWithLogs(id)
            image ?: return@launch
            val path = Uri.parse(image).path
            if (path != null){
                val imageFile = File(path)
                if (imageFile.exists()) imageFile.delete()
            }
        }catch (e: Exception){

        }
    }
}
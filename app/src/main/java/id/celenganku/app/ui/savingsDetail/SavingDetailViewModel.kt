package id.celenganku.app.ui.savingsDetail

import android.net.Uri
import androidx.lifecycle.*
import androidx.room.withTransaction
import id.celenganku.app.db.AppDB
import id.celenganku.app.db.SavingDao
import id.celenganku.app.model.SavingsEntity
import id.celenganku.app.model.SavingsLogEntity
import id.celenganku.app.utils.*
import java.io.File

class SavingDetailViewModel(private val db: AppDB, private val savingDao: SavingDao) : ViewModel() {

    var savingId = 0
    private var isShowOptionFab = false
    private val _showOptionFab = MutableLiveData<Boolean>()
    val showOptionFab: LiveData<Boolean> get() = _showOptionFab
    val savingsLogs: LiveData<List<SavingsLogEntity>> get() = savingDao.getSavingsLogs(savingId)
    val savingsDetail: LiveData<SavingsEntity?> get() = savingDao.getSavingById(savingId)

    fun deleteSavings(setStatus: (status: Status)-> Unit) {
        runInBackground {
            try {
                savingDao.getImagePathById(savingId)?.let {
                    Uri.parse(it).path?.let { path ->
                        File(path).delete()
                    }
                }
                savingDao.deleteSavingWithLogs(savingId)
                switchToMain {
                    setStatus(SUCCESS())
                }
            }catch (e: Exception){
                switchToMain {
                    setStatus(ERROR(e.localizedMessage))
                }
            }
        }
    }

    fun setShowOptionFab(){
        isShowOptionFab = !isShowOptionFab
        _showOptionFab.value = isShowOptionFab
    }

    fun addOrMinSaving(savingsLogEntity: SavingsLogEntity){
        runInBackground {
            try {
                savingDao.updateSavingLog(savingsLogEntity)
            }catch (e: Exception){

            }
        }
    }

    fun completeSavings(savingsLogEntity: SavingsLogEntity, dateFinished: Long){
        runInBackground {
            try {
                db.withTransaction {
                    savingDao.updateSavingLog(savingsLogEntity)
                    savingDao.setSavingDone(savingsLogEntity.savingsId, dateFinished)
                }
            }catch (e: Exception){

            }
        }
    }

}
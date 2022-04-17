package id.celenganku.app.db

import androidx.lifecycle.LiveData
import androidx.room.*
import id.celenganku.app.model.SavingsEntity
import id.celenganku.app.model.SavingsLogEntity

@Dao
interface SavingDao {

    @Query("SELECT * FROM saving WHERE id=:id")
    fun getSavingById(id: Int): LiveData<SavingsEntity?>

    @Query("SELECT * FROM saving WHERE dateFinished IS NULL")
    fun getAllSaving(): LiveData<List<SavingsEntity>>

    @Query("SELECT * FROM saving WHERE dateFinished IS NOT NULL")
    fun getFinishedSaving(): LiveData<List<SavingsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSaving(savings: SavingsEntity)

    @Update
    suspend fun updateSaving(savings: SavingsEntity)

    @Query("UPDATE saving SET dateFinished = :dateFinished WHERE id = :id")
    suspend fun setSavingDone(id:Int, dateFinished: Long)

    @Query("DELETE FROM saving WHERE id = :id")
    suspend fun deleteSaving(id: Int)

    @Query("DELETE FROM savingsLogs WHERE savingsId = :id")
    suspend fun deleteSavingLog(id: Int)

    @Query("SELECT image FROM saving WHERE id = :id")
    suspend fun getImagePathById(id: Int): String?

    @Transaction
    suspend fun deleteSavingWithLogs(id: Int){
        deleteSaving(id)
        deleteSavingLog(id)
    }

    @Query("SELECT * FROM savingsLogs WHERE savingsId = :id")
    fun getSavingsLogs(id: Int): LiveData<List<SavingsLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrMinSaving(savingsLogEntity: SavingsLogEntity)

    @Query("UPDATE saving SET collected = collected+:nominalTarget WHERE id = :id")
    suspend fun addNominal(id: Int, nominalTarget: Int)

    @Query("UPDATE saving SET collected = collected-:nominalTarget WHERE id = :id")
    suspend fun minNominal(id: Int, nominalTarget: Int)

    @Transaction
    suspend fun updateSavingLog(savingsLogEntity: SavingsLogEntity){
        addOrMinSaving(savingsLogEntity)
        if (savingsLogEntity.increase){
            addNominal(savingsLogEntity.savingsId, savingsLogEntity.nominal)
        }else{
            minNominal(savingsLogEntity.savingsId, savingsLogEntity.nominal)
        }
    }
}
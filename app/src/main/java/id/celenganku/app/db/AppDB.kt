package id.celenganku.app.db

import androidx.room.Database
import androidx.room.RoomDatabase
import id.celenganku.app.model.SavingsEntity
import id.celenganku.app.model.SavingsLogEntity

@Database(
    entities = [
        SavingsEntity::class,
        SavingsLogEntity::class
    ],
    version = 1,
    exportSchema = false
)

abstract class AppDB: RoomDatabase() {

    abstract fun savingDao(): SavingDao

}
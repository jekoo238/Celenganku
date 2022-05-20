package id.celenganku.app.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import id.celenganku.app.model.SavingsEntity
import id.celenganku.app.model.SavingsLogEntity

@Database(
    entities = [
        SavingsEntity::class,
        SavingsLogEntity::class
    ],
    version = 2,
    autoMigrations = [ AutoMigration(from = 1, to = 2) ],
    exportSchema = true
)

abstract class AppDB: RoomDatabase() {

    abstract fun savingDao(): SavingDao

}
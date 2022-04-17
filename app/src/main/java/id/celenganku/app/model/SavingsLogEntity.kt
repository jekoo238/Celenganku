package id.celenganku.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savingsLogs")
data class SavingsLogEntity(
        @PrimaryKey(autoGenerate = true)
        val logId: Int?,
        val savingsId: Int,
        val timestamp: Long,
        val nominal: Int,
        val increase: Boolean,
        val notes: String?
)
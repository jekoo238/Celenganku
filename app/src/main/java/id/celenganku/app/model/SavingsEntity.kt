package id.celenganku.app.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saving")
data class SavingsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val title: String,
    val image: String?,
    val target: Int,
    val targetPerDay: Int,
    val collected: Int,
    val dateCreated: Long,
    val dateFinished: Long?,
    @ColumnInfo(defaultValue = "0")
    val fillingType: Long
) {
    val  fillingTypeText get() = when(fillingType) {
        0L -> "Hari"
        1L -> "Minggu"
        else -> "Bulan"
    }
}
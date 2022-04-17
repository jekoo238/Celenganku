package id.celenganku.app.model

data class SavingFormModel(
    val title: String,
    val target: Int,
    val collected: Int,
    val isIncrease: Boolean
)
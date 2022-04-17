package id.celenganku.app.utils

sealed class Status
data class SUCCESS(val message: String? = null): Status()
data class ERROR(val message: String? = null): Status()
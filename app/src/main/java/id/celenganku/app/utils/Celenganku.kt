package id.celenganku.app.utils

import android.app.Application
import androidx.room.Room
import id.celenganku.app.db.AppDB
import id.celenganku.app.ui.form.SavingFormViewModel
import id.celenganku.app.ui.home.current.CurrentViewModel
import id.celenganku.app.ui.currentSavingsDetail.SavingDetailViewModel
import id.celenganku.app.ui.home.done.SavingDoneViewModel
import id.celenganku.app.ui.savingsDoneDetail.HistoryDetailViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

@Suppress("unused", "SpellCheckingInspection")
class Celenganku: Application() {

    override fun onCreate() {
        super.onCreate()

        val mainModule = module {
            single {
                Room.databaseBuilder(androidContext(), AppDB::class.java, "app.db")
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .fallbackToDestructiveMigration()
                    .build()
            }

            single { get<AppDB>().savingDao() }
            viewModel { SavingFormViewModel(get()) }
            viewModel { CurrentViewModel(get()) }
            viewModel { SavingDetailViewModel(get(), get()) }
            viewModel { SavingDoneViewModel(get()) }
            viewModel { HistoryDetailViewModel(get()) }
        }

        startKoin {
            androidContext(this@Celenganku)
            modules(mainModule)
        }
    }


}
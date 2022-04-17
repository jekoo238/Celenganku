package id.celenganku.app.utils

import android.app.Application
import androidx.room.Room
import id.celenganku.app.db.AppDB
import id.celenganku.app.ui.add.AddSavingViewModel
import id.celenganku.app.ui.home.CurrentViewModel
import id.celenganku.app.ui.savingsDetail.SavingDetailViewModel
import id.celenganku.app.ui.hsavingsHistory.HistoryViewModel
import id.celenganku.app.ui.savingsHistoryDetail.HistoryDetailViewModel
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
            viewModel { AddSavingViewModel(get()) }
            viewModel { CurrentViewModel(get()) }
            viewModel { SavingDetailViewModel(get(), get()) }
            viewModel { HistoryViewModel(get()) }
            viewModel { HistoryDetailViewModel(get()) }
        }

        startKoin {
            androidContext(this@Celenganku)
            modules(mainModule)
        }
    }


}
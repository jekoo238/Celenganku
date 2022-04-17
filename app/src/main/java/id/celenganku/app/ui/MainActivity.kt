package id.celenganku.app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import id.celenganku.app.R
import id.celenganku.app.utils.PreferenceHelper
import id.celenganku.app.utils.changeTheme

class MainActivity : AppCompatActivity() {


    private lateinit var appBarConfiguration : AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Celenganku)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        changeTheme(PreferenceHelper(this))

        val hostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        val navController = hostFragment.navController

        appBarConfiguration = AppBarConfiguration(setOf(R.id.mainFeatureFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.navHost).navigateUp(appBarConfiguration)
    }
}
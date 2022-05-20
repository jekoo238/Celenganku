package id.celenganku.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.celenganku.app.R
import id.celenganku.app.utils.PreferenceHelper
import id.celenganku.app.utils.changeTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Celenganku)
        setContentView(R.layout.activity_main)
        changeTheme(PreferenceHelper(this))
    }

}
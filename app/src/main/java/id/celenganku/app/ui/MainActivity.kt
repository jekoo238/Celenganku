package id.celenganku.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import id.celenganku.app.databinding.ActivityMainBinding
import id.celenganku.app.utils.PreferenceHelper
import id.celenganku.app.utils.changeTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)
        changeTheme(PreferenceHelper(this))
    }

}
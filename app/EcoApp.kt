package my.com.a221491_amiraizatbinharith_nelson_project2

import android.app.Application
import com.google.firebase.FirebaseApp

class EcoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Ensures Firebase is initialized before any ViewModel or screen starts
        FirebaseApp.initializeApp(this)
    }
}

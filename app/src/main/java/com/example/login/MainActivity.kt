package com.example.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.login.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar el NavController y AppBarConfiguration
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Configurar BottomNavigationView con NavController
        NavigationUI.setupWithNavController(binding.navView, navController)

        // Obtener los datos del Intent (si se pasaron al iniciar la actividad)
        val userEmail = intent.getStringExtra("USER_EMAIL")
        val userName = intent.getStringExtra("USER_NAME")

        // Mostrar los datos en los TextViews
        binding.userEmailTextView.text = userEmail
        binding.userNameTextView.text = userName

        // Inicializar FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Configurar el TextView para cerrar sesión
        binding.logoutTextView.setOnClickListener {
            firebaseAuth.signOut()
            // Desconectar la cuenta de Google
            FirebaseAuth.getInstance().signOut()

            // Forzar la selección de cuenta
            val googleSignInClient =
                GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
            googleSignInClient.signOut().addOnCompleteListener {
                // Redirigir al usuario a la pantalla de inicio de sesión
                val signInIntent = Intent(this, LoginActivity::class.java)
                startActivity(signInIntent)

                // Cerrar la actividad actual
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}


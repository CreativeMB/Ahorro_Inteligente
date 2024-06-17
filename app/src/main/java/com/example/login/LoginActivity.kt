package com.example.login

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.login.databinding.LoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var binding: LoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val reqCode: Int = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Encontrar el botón de inicio de sesión de Google
        val signInButton: SignInButton = findViewById(R.id.sign_in)
        signInButton.setSize(SignInButton.SIZE_STANDARD) // Personalizar el tamaño del botón
        signInButton.setColorScheme(SignInButton.COLOR_DARK) // Personalizar el esquema de colores del botón

        // Cambiar el texto del botón
        for (i in 0 until signInButton.childCount) {
            val v = signInButton.getChildAt(i)
            if (v is TextView) {
                v.text = "Bienvenido Comencemos"
                v.textSize = 20f // Ajustar el tamaño del texto (en sp)
                break
            }
        }

        // Configurar el botón de inicio de sesión
        binding.signIn.setOnClickListener {
            Toast.makeText(this, "Escoge Tu Cuenta Google Para Conectar Su Bace De Datos", Toast.LENGTH_SHORT).show()
            signInWithGoogle()
        }

    }

    private fun signInWithGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, reqCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == reqCode) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(
                    this,
                    "Lo Siento Selecciona Una Cuenta Google",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                handleResult(task)
            }
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                updateUI(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(cuenta: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(cuenta.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("USER_EMAIL", cuenta.email)
                    putExtra("USER_NAME", cuenta.displayName)
                }
                startActivity(intent)
                finish()
            }
        }
    }

}


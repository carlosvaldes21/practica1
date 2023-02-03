package com.carlosvaldes.practica1.views.activities

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.carlosvaldes.practica1.databinding.ActivityAuthBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException


class AuthActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAuthBinding

    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()
    }

    private fun setup()
    {
        with(binding) {
            btRegister.setOnClickListener {

                if(!validateFields()) return@setOnClickListener

                startLoadingState()

                //Si los campos no son vacíos registramos el usuario
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(
                        tietEmail.text.toString(), tietPassword.text.toString()
                ).addOnCompleteListener { authResultTask ->

                        if ( authResultTask.isSuccessful ) {
                            showMain()
                        } else {
                            finishLoadingState()
                            handleFirebaseErrors(authResultTask)
                        }
                    }

            }

            btLogin.setOnClickListener {

                if(!validateFields()) return@setOnClickListener

                startLoadingState()

                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(
                        tietEmail.text.toString(), tietPassword.text.toString()
                    ).addOnCompleteListener { authResultTask ->
                        if ( authResultTask.isSuccessful ) {
                            showMain()
                        } else {
                            finishLoadingState()
                            handleFirebaseErrors(authResultTask)
                        }
                    }

            }

            tvForgotPassword.setOnClickListener {
                val resetMail = EditText(it.context)

                resetMail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                val passwordResetDialog = AlertDialog.Builder(it.context)
                passwordResetDialog.setTitle("Restablecer contraseña")
                passwordResetDialog.setMessage("Ingrese su correo para recibir el enlace para restablecer")
                passwordResetDialog.setView(resetMail)

                passwordResetDialog.setPositiveButton("Enviar", DialogInterface.OnClickListener { dialog, which ->
                    val mail = resetMail.text.toString().trim()
                    
                    //Verify that email is correct
                    if(mail.isNotEmpty() && isValidEmail(mail)){
                        FirebaseAuth.getInstance().sendPasswordResetEmail(mail).addOnSuccessListener {
                            Toast.makeText(this@AuthActivity, "El enlace para restablecer la contraseña ha sido enviado a su correo", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(this@AuthActivity, "El enlace no se ha podido enviar: ${it.message}", Toast.LENGTH_SHORT).show() //it tiene la excepción
                        }
                    }else{
                        Toast.makeText(this@AuthActivity, "Favor de ingresar la dirección de correo", Toast.LENGTH_SHORT).show() //it tiene la excepción
                    }
                })

                passwordResetDialog.setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, which ->

                })

                passwordResetDialog.create().show()
            }
        }
    }

    /**
     * Function to validate fields
     * @return boolean
     */
    private fun validateFields(): Boolean
    {
        resetErrors()
        email = binding.tietEmail.text.toString().trim()
        password = binding.tietPassword.text.toString().trim()

        with(binding) {
            if ( email.isEmpty() ) {
                tietEmail.error = ("El email no puede ser vacío")
                tietEmail.requestFocus()
                return false
            }

            //Si no es un email válido
            if ( !isValidEmail(email) ) {
                tietEmail.error = "Verifica tu email"
                tietEmail.requestFocus()
                return false
            }

            if ( password.isEmpty() || password.length < 6 ) {
                tietPassword.error = ("El password no puede ser vacío ni tener menos de 6 carácteres")
                tietPassword.requestFocus()
                return false
            }

            return true
        }
    }

    private fun resetErrors()
    {
        binding.tietPassword.error = null
        binding.tietEmail.error = null

    }

    /**
     * Funtion to show progress bar and disable buttons
     */
    private fun startLoadingState()
    {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            btRegister.isEnabled = false
            btLogin.isEnabled = false
        }
    }

    /**
     * Function to hide progress bar and enable button
     */
    private fun finishLoadingState()
    {
        with(binding) {
            progressBar.visibility = View.GONE
            btRegister.isEnabled = true
            btLogin.isEnabled = true
        }
    }

    /**
     * Verify if email is valid
     * @return Boolean
     */
    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    /**
     * Function to show main activity
     */
    private fun showMain()
    {
        val mainIntent = Intent(this, MainActivity::class.java).apply {}

        startActivity(mainIntent)
        finish()
    }

    private fun handleFirebaseErrors(task: Task<AuthResult>){
        var errorCode = ""

        try{
            errorCode = (task.exception as FirebaseAuthException).errorCode
        }catch(e: Exception){
            errorCode = "NO_NETWORK"
        }

        when(errorCode){
            "ERROR_INVALID_EMAIL" -> {
                Toast.makeText(this, "Error: El correo electrónico no tiene un formato correcto", Toast.LENGTH_SHORT).show()
                binding.tietEmail.error = "Error: El correo electrónico no tiene un formato correcto"
                binding.tietEmail.requestFocus()
            }
            "ERROR_WRONG_PASSWORD" -> {
                Toast.makeText(this, "Error: La contraseña no es válida", Toast.LENGTH_SHORT).show()
                binding.tietPassword.error = "La contraseña no es válida"
                binding.tietPassword.requestFocus()
                binding.tietPassword.setText("")

            }
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> {
                //An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.
                Toast.makeText(this, "Error: Una cuenta ya existe con el mismo correo, pero con diferentes datos de ingreso", Toast.LENGTH_SHORT).show()
            }
            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                Toast.makeText(this, "Error: el correo electrónico ya está en uso con otra cuenta.", Toast.LENGTH_LONG).show()
                binding.tietEmail.error = ("Error: el correo electrónico ya está en uso con otra cuenta.")
                binding.tietEmail.requestFocus()
            }
            "ERROR_USER_TOKEN_EXPIRED" -> {
                Toast.makeText(this, "Error: La sesión ha expirado. Favor de ingresar nuevamente.", Toast.LENGTH_LONG).show()
            }
            "ERROR_USER_NOT_FOUND" -> {
                Toast.makeText(this, "Error: No existe el usuario con la información proporcionada.", Toast.LENGTH_LONG).show()
            }
            "ERROR_WEAK_PASSWORD" -> {
                Toast.makeText(this, "La contraseña porporcionada es inválida", Toast.LENGTH_LONG).show()
                binding.tietPassword.error = "La contraseña debe de tener por lo menos 6 caracteres"
                binding.tietPassword.requestFocus()
            }
            "NO_NETWORK" -> {
                Toast.makeText(this, "Red no disponible o se interrumpió la conexión", Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this, "Error. No se pudo autenticar exitosamente.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
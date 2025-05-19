package com.example.practica2


import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.example.practica2.R
//import com.example.practica2.models.User

class EditUserActivity : AppCompatActivity() {

    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var db: FirebaseFirestore
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        editName = findViewById(R.id.editName)
        editEmail = findViewById(R.id.editEmail)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        db = FirebaseFirestore.getInstance()

        // Obtener datos del usuario desde el Intent
        userId = intent.getStringExtra("USER_ID")
        val name = intent.getStringExtra("USER_NAME")
        val email = intent.getStringExtra("USER_EMAIL")

        // Mostrar datos en los campos de texto
        editName.setText(name)
        editEmail.setText(email)

        btnSave.setOnClickListener {
            updateUser()
        }

        btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun updateUser() {
        val newName = editName.text.toString().trim()
        val newEmail = editEmail.text.toString().trim()

        if (newName.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        userId?.let {
            db.collection("users").document(it)
                .update("name", newName, "email", newEmail)
                .addOnSuccessListener {
                    Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show()
                }
        }
    }
}

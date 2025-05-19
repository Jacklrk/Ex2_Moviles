package com.example.practica2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.practica2.R

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var updateButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        nameEditText = findViewById(R.id.profileNameEditText)
        emailEditText = findViewById(R.id.profileEmailEditText)
        updateButton = findViewById(R.id.updateProfileButton)
        cancelButton = findViewById(R.id.cancelProfileButton)

        // Cargar datos del usuario actual
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    nameEditText.setText(document.getString("name"))
                    emailEditText.setText(document.getString("email"))
                }
            }
        }

        updateButton.setOnClickListener {
            val newName = nameEditText.text.toString().trim()
            val newEmail = emailEditText.text.toString().trim()

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId != null) {
                db.collection("users").document(userId)
                    .update("name", newName, "email", newEmail)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }
}

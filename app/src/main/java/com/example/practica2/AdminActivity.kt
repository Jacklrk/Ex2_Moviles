package com.example.practica2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practica2.adapters.UserAdapter
import com.example.practica2.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class AdminActivity : AppCompatActivity(), UserAdapter.OnUserActionListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private val usersList = mutableListOf<User>()
    private lateinit var addUserButton: Button
    private lateinit var logoutButton: Button
    private lateinit var sendNotificationAllButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        usersRecyclerView = findViewById(R.id.usersRecyclerView)
        addUserButton = findViewById(R.id.addUserButton)
        logoutButton = findViewById(R.id.logoutButtonAdmin)
        sendNotificationAllButton = findViewById(R.id.sendNotificationAllButton)

        usersRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UserAdapter(usersList, this)
        usersRecyclerView.adapter = adapter

        loadUsers()

        addUserButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        sendNotificationAllButton.setOnClickListener {
            enviarNotificacionATodosLosUsuarios()
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    fun loadUsers() {
        val currentUserId = auth.currentUser?.uid

        db.collection("users")
            .whereEqualTo("role", "Usuario")
            .get()
            .addOnSuccessListener { documents ->
                usersList.clear()
                for (document in documents) {
                    val user = document.toObject(User::class.java)
                    if (user.id != currentUserId) {
                        usersList.add(user)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            loadUsers()
        }
    }

    private fun sendNotificationToToken(token: String, title: String, message: String) {
        val json = """
            {
                "token": "$token",
                "title": "$title",
                "body": "$message"
            }
        """.trimIndent()

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("http://192.168.0.102:3000/send-notification") // Ajusta la IP si es necesario
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FCM_SEND", "Error al enviar notificación: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this@AdminActivity, "Error al enviar", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                Log.d("FCM_SEND", "Respuesta del servidor: $body")
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AdminActivity, "Notificación enviada", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@AdminActivity, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun enviarNotificacionATodosLosUsuarios() {
        db.collection("users")
            .whereEqualTo("role", "Usuario")
            .get()
            .addOnSuccessListener { result ->
                // Obtener un solo token (el primero disponible) para enviar la notificación general
                val firstDoc = result.documents.firstOrNull()
                val token = firstDoc?.getString("fcmToken")

                if (!token.isNullOrEmpty()) {
                    sendNotificationToToken(
                        token,
                        title = "Nueva notificación",
                        message = "Mensaje enviado desde el panel del administrador."
                    )
                } else {
                    Toast.makeText(this, "No hay tokens disponibles", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al obtener usuarios", Toast.LENGTH_SHORT).show()
            }
    }


    // 👉 NUEVO: Enviar notificación a un usuario específico
    override fun onNotifyClicked(user: User) {
        val token = user.fcmToken
        if (!token.isNullOrEmpty()) {
            sendNotificationToToken(
                token,
                title = "Hola ${user.name}",
                message = "Este mensaje es solo para ti"
            )
        }
    }

    // 👉 Redirigir a edición
    override fun onEditClicked(user: User) {
        val intent = Intent(this, EditUserActivity::class.java)
        intent.putExtra("USER_ID", user.id)
        intent.putExtra("USER_NAME", user.name)
        intent.putExtra("USER_EMAIL", user.email)
        startActivityForResult(intent, 1001)
    }

    // 👉 Eliminar usuario
    override fun onDeleteClicked(user: User) {
        db.collection("users").document(user.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                usersList.remove(user)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar usuario", Toast.LENGTH_SHORT).show()
            }
    }
}

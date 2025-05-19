const express = require('express');
const admin = require('firebase-admin');
const app = express();
app.use(express.json());

// Reemplaza con la ruta a tu JSON de cuenta de servicio
const serviceAccount = require('./proyecto1-addf4-firebase-adminsdk-fbsvc-ccfd087c3e.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

app.post('/send-notification', async (req, res) => {
    console.log("Solicitud recibida:", req.body);
  const { token, title, body } = req.body;

  const message = {
    token: token,
    notification: {
      title: title,
      body: body,
    }
  };

  try {
    const response = await admin.messaging().send(message);
    res.status(200).json({ success: true, messageId: response });
  } catch (error) {
    console.error('Error al enviar notificación:', error);
    res.status(500).json({ success: false, error });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Servidor escuchando en puerto ${PORT}`));

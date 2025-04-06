package com.example.safetyapp

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.RecognizerIntent
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.safetyapp.ui.theme.SafetyAppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.math.sqrt

private lateinit var fusedLocationClient: FusedLocationProviderClient
private lateinit var speechLauncher: ActivityResultLauncher<Intent>

class MainActivity : ComponentActivity(), SensorEventListener {

    private val SMS_PERMISSION_CODE = 101
    private lateinit var sensorManager: SensorManager
    private var accel = 0f
    private var accelCurrent = 0f
    private var accelLast = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        speechLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val result = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val spokenPhone = result?.get(0)
                spokenPhone?.let {
                    sendSms(it, "This is an emergency SMS via voice!")
                }
            }
        }

        fun startVoiceRecognition() {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Say the phone number")
            }
            speechLauncher.launch(intent)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI
        )

        accel = 10f
        accelCurrent = SensorManager.GRAVITY_EARTH
        accelLast = SensorManager.GRAVITY_EARTH

        setContent {
            SafetyAppTheme {
                SmsSenderUI(
                    onSendSmsClick = { phone -> sendSms(phone, "This is an emergency SMS") },
                    onMicClick = { startVoiceRecognition() }
                )
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val x = event?.values?.get(0) ?: 0f
        val y = event?.values?.get(1) ?: 0f
        val z = event?.values?.get(2) ?: 0f

        accelLast = accelCurrent
        accelCurrent = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val delta = accelCurrent - accelLast
        accel = accel * 0.9f + delta

        if (accel > 12) {
            sendStoredEmergencySms()
        }
    }

    private fun sendStoredEmergencySms() {
        val sharedPrefs = getSharedPreferences("safety_app_prefs", Context.MODE_PRIVATE)
        val phone = sharedPrefs.getString("phone", "")
        val message = sharedPrefs.getString("message", "")

        if (!phone.isNullOrEmpty() && !message.isNullOrEmpty()) {
            sendSms(phone, message)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onPause() {
        sensorManager.unregisterListener(this)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI
        )
    }

    private fun sendSms(phone: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phone, null, message, null, null)

            // VIBRATION
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(500)
            }

            // TOAST
            Toast.makeText(this, "SMS Sent!", Toast.LENGTH_SHORT).show()

            // Optional: PLAY SOUND
            val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
            toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 200)

        } catch (e: Exception) {
            Toast.makeText(this, "SMS failed to send", Toast.LENGTH_SHORT).show()
        }
    }
}
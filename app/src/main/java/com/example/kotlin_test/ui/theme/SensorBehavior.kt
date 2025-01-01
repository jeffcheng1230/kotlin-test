package com.example.kotlin_test

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import kotlinx.coroutines.*

import android.media.MediaPlayer
import kotlin.math.abs

class SensorBehavior : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyro: Sensor? = null

    private var xAccel: Float = 0F
    private var yAccel: Float = 0F
    private var zAccel: Float = 0F

    private var xGyro: Float = 0F
    private var yGyro: Float = 0F
    private var zGyro: Float = 0F

    private lateinit var xTextView: TextView
    private lateinit var yTextView: TextView
    private lateinit var zTextView: TextView

    private lateinit var xGyroTextView: TextView
    private lateinit var yGyroTextView: TextView
    private lateinit var zGyroTextView: TextView

    private lateinit var editText: EditText
    private lateinit var button: Button
    private lateinit var coroutineTextView: TextView

    private lateinit var helpPlayer: MediaPlayer
    private lateinit var weePlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize TextViews
        xTextView = findViewById(R.id.xTextView)
        yTextView = findViewById(R.id.yTextView)
        zTextView = findViewById(R.id.zTextView)

        xGyroTextView = findViewById(R.id.xGyroTextView)
        yGyroTextView = findViewById(R.id.yGyroTextView)
        zGyroTextView = findViewById(R.id.zGyroTextView)

        // Initialize SensorManager and the accelerometer
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        if (accelerometer == null) {
            xTextView.text = "Accelerometer not available"
            yTextView.text = ""
            zTextView.text = ""
        }

        // ========= Coroutine Example =========

        editText = findViewById(R.id.editText)
        button = findViewById(R.id.button)
        coroutineTextView = findViewById(R.id.coroutineTextView)

        helpPlayer = MediaPlayer.create(this, R.raw.help)
        weePlayer = MediaPlayer.create(this, R.raw.wee)
        button.setOnClickListener {
            // Get text from EditText
            val inputText = editText.text.toString()

            // Display input text in TextView
            coroutineTextView.text = "You entered: $inputText"

            if (!weePlayer.isPlaying) {
                weePlayer.start()
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            checkAccel();
        }
    }

    private suspend fun checkAccel() = coroutineScope {
        while (isActive) {
            if (zAccel < -2F) {
                if (!helpPlayer.isPlaying) {
                    coroutineTextView.text = "Dropped!";
                    launch {
                        delay(100);
                        if (zAccel < -2F) {
                            // helpPlayer.start()
                        }
                    }
                }
            }
            else {
                coroutineTextView.text = "Reset Dropped!";
            }
            delay(50);
        }
    }

    override fun onResume() {
        super.onResume()
        // Register the listener for the accelerometer
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyro?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        // Unregister the listener to save battery
        sensorManager.unregisterListener(this)
    }

    // Called when sensor data changes
    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                val x = event.values[0] // Acceleration along the X axis
                val y = event.values[1] // Acceleration along the Y axis
                val z = event.values[2] // Acceleration along the Z axis

                xTextView.text = "X: $x"
                yTextView.text = "Y: $y"
                zTextView.text = "Z: $z"

                xAccel = x;
                yAccel = y;
                zAccel = z;
            }
            Sensor.TYPE_GYROSCOPE -> {
                val x = event.values[0] // Acceleration along the X axis
                val y = event.values[1] // Acceleration along the Y axis
                val z = event.values[2] // Acceleration along the Z axis

                xGyroTextView.text = "X: $x"
                yGyroTextView.text = "Y: $y"
                zGyroTextView.text = "Z: $z"

                xGyro = x;
                yGyro = y;
                zGyro = z;

                if (abs(zGyro) > 0.5) {
                    coroutineTextView.setBackgroundColor(Color.RED)
                    if (!weePlayer.isPlaying) {
                        weePlayer.start()
                    }
                }
                else {
                    coroutineTextView.setBackgroundColor(Color.WHITE)
                }
            }
        }
    }

    // Required but not used
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        if (::helpPlayer.isInitialized) {
            helpPlayer.release()  // Release the MediaPlayer resource
        }
    }
}
package com.example.kotlin_test

// import android.os.Bundle
// import androidx.activity.ComponentActivity
// import androidx.activity.compose.setContent
// import androidx.compose.foundation.layout.fillMaxSize
// import androidx.compose.material3.MaterialTheme
// import androidx.compose.material3.Surface
// import androidx.compose.material3.Text
// import androidx.compose.runtime.Composable
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.tooling.preview.Preview
// import com.example.kotlin_test.ui.theme.KotlintestTheme

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private lateinit var xTextView: TextView
    private lateinit var yTextView: TextView
    private lateinit var zTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize TextViews
        xTextView = findViewById(R.id.xTextView)
        yTextView = findViewById(R.id.yTextView)
        zTextView = findViewById(R.id.zTextView)

        // Initialize SensorManager and the accelerometer
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            xTextView.text = "Accelerometer not available"
            yTextView.text = ""
            zTextView.text = ""
        }
    }

    override fun onResume() {
        super.onResume()
        // Register the listener for the accelerometer
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        // Unregister the listener to save battery
        sensorManager.unregisterListener(this)
    }

    // Called when sensor data changes
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val x = event.values[0] // Acceleration along the X axis
            val y = event.values[1] // Acceleration along the Y axis
            val z = event.values[2] // Acceleration along the Z axis

            xTextView.text = "X: $x"
            yTextView.text = "Y: $y"
            zTextView.text = "Z: $z"
        }
    }

    // Required but not used
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
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
import android.util.Log
import android.view.View
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.IOException
import kotlin.math.abs

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager : SensorManager
    private var accelerometer : Sensor? = null
    private var magnetometer : Sensor? = null
    private var gyro : Sensor? = null

    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)
    private val rotVelocity = FloatArray(3)
    private val orientation = FloatArray(3)

    private lateinit var rootView : View
    private var curColor = "white"

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Displays Pokemon using PokeAPI when phone is looked at
        // Gestures include pointing phone left/right and back to center

        // TODO
        // nav button to switch activities
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        rootView = findViewById<View>(android.R.id.content)

        CoroutineScope(Dispatchers.Main).launch {
            run("https://pokeapi.co/api/v2/pokemon/serperior/")
        }
    }

    fun run(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                var body : ResponseBody? = response.body()
                if (body != null) {
                    var jsonObj = JSONObject(body.string())
                    Log.i("MainActivity", jsonObj.getJSONObject("sprites").getString("front_default"))
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        magnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        gyro?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) { return }

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, gravity, 0, event.values.size)
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, geomagnetic, 0, event.values.size)
            }
            Sensor.TYPE_GYROSCOPE -> {
                System.arraycopy(event.values, 0, rotVelocity, 0, event.values.size)
            }
        }

        if (gravity.isNotEmpty() && geomagnetic.isNotEmpty()) {
            val r = FloatArray(9)
            val i = FloatArray(9)
            if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                SensorManager.getOrientation(r, orientation)
            }
        }

        if (abs(rotVelocity[0]) > 0.5) {
            if (curColor != "green") {
                rootView.setBackgroundColor(Color.GREEN)
                curColor = "green"
            }
        }
        else {
            if (-0.9 < orientation[1] && orientation[1] < -0.2) {
                if (curColor != "red") {
                    rootView.setBackgroundColor(Color.RED)
                    curColor = "red"
                }
            }
            else {
                if (curColor != "white") {
                    rootView.setBackgroundColor(Color.WHITE)
                    curColor = "white"
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, acurracy: Int) {
    }

}
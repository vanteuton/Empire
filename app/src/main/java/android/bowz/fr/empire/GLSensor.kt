package android.bowz.fr.empire

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.Matrix

/**
 * Defines a OpenGL tool for updating rotation matrix from sensors.
 *
 * @param context the context
 * @param rate the rate (SensorManager.SENSOR_DELAY_XXX)
 * @param noiseReductionFactor The noise reduction factor (0: no noise reduction, 1: no more variation).
 * @param listener the listener
 */
class GLSensor(context: Context,
               private val rate: Int,
               private val noiseReductionFactor: Float,
               private val onChanged: () -> Unit) : SensorEventListener {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magneticField: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private var accelerometerValues: FloatArray? = null
    private var magneticFieldValues: FloatArray? = null

    val rotationMatrix = FloatArray(MATRIX_SIZE)

    init {
        Matrix.setIdentityM(rotationMatrix, 0)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> accelerometerValues = reduceNoise(accelerometerValues, event.values)
            Sensor.TYPE_MAGNETIC_FIELD -> magneticFieldValues = reduceNoise(magneticFieldValues, event.values)
        }
        if (accelerometerValues != null && magneticFieldValues != null) {
            SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magneticFieldValues)
            onChanged()
        }
    }

    fun onResume() {
        sensorManager.registerListener(this, accelerometer, rate)
        sensorManager.registerListener(this, magneticField, rate)
    }

    fun onPause() {
        sensorManager.unregisterListener(this)
    }

    private fun reduceNoise(previousValues: FloatArray?, newValues: FloatArray): FloatArray {
        return (previousValues ?: return newValues).zip(newValues).map {
            noiseReductionFactor * it.first + (1f - noiseReductionFactor) * it.second
        }.toFloatArray()
    }
}

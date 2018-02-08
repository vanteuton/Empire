package android.bowz.fr.empire

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.hardware.SensorManager
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLSurfaceView.Renderer
import android.opengl.Matrix
import android.renderscript.Element.createVector
import android.view.MotionEvent
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class HexagonView(context: Context) : GLSurfaceView(context), Renderer {
    var onClick: ((row: Int, colunm: Int) -> Unit)? = null

    // todo attributes
    private val level: Int = 5

    private val size = 2 * level + 1
    private val centers = size * size
    private val rowSize = 2 * (size + 1)
    private val points = rowSize * (size + 1)
    private val colors = ByteBuffer.allocateDirect(centers)

    private val hexVectorCount = 8

    private val vectors = ByteBuffer.allocateDirect(2 * (centers + points)).apply {
        for (r in -level..level) {
            for (c in -level..level) {
                put(getX(r, c).toByte())
                put(getY(r, c).toByte())
            }
        }
        for (r in -level - 1..level) {
            put((getX(r, -level) - 2).toByte())
            put((getY(r, -level) + 2).toByte())
            put((getX(r, -level) - 1).toByte())
            put((getY(r, -level) + 1).toByte())
            for (c in -level..level) {
                put(getX(r, c).toByte())
                put((getY(r, c) + 2).toByte())
                put((getX(r, c) + 1).toByte())
                put((getY(r, c) + 1).toByte())
            }
        }
        flip()
    }

    private val indices = ByteBuffer.allocateDirect(2 * hexVectorCount * centers).order(ByteOrder.nativeOrder()).apply {
        for (r in 0 until size) {
            for (c in 0 until size) {
                putShort(getHexagonCenterIndex(r, c).toShort())
                var pointRow = r
                var pointColumn = 2 * c
                putShort(getHexagonPointsIndex(pointRow, pointColumn).toShort())
                putShort(getHexagonPointsIndex(pointRow, ++pointColumn).toShort())
                putShort(getHexagonPointsIndex(pointRow, ++pointColumn).toShort())
                putShort(getHexagonPointsIndex(++pointRow, ++pointColumn).toShort())
                putShort(getHexagonPointsIndex(pointRow, --pointColumn).toShort())
                putShort(getHexagonPointsIndex(pointRow, --pointColumn).toShort())
                putShort(getHexagonPointsIndex(--pointRow, --pointColumn).toShort())
            }
        }
        flip()
    }

    private val planZ = floatArrayOf(0f, 0f, 1f, 0f)
    private val viewport = createViewport()
    private val rescaleMatrix = createMatrix()
    private val povMatrix = createMatrix()
    private val frustumMatrix = createMatrix()
    private val mvpMatrix = createMatrix()
    private val tmp1Matrix = createMatrix()
    private val tmp2Matrix = createMatrix()
    private val tmpVector = createVector()

    private var sensor = GLSensor(context, SensorManager.SENSOR_DELAY_GAME, 0.95f) {
        queueEvent { updateMVP() }
    }
    private lateinit var colorBuffer: GLES20Buffer
    private var mvpLocation: Int = 0

    init {
        setEGLContextClientVersion(2)

        // transparent background
        val colorSize = 8
        setEGLConfigChooser(colorSize, colorSize, colorSize, colorSize, 0, 0)
        holder.setFormat(PixelFormat.TRANSLUCENT)

        // init scale matrix
        Matrix.setIdentityM(rescaleMatrix, 0)
        Matrix.scaleM(rescaleMatrix, 0, Math.sqrt(0.1875).toFloat(), 0.25f, 1f)

        setRenderer(this)
    }

    override fun onResume() {
        super.onResume()
        sensor.onResume()
    }

    override fun onPause() {
        sensor.onPause()
        super.onPause()
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        for (i in 0 until centers) {
            GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, hexVectorCount, GLES20.GL_UNSIGNED_SHORT, 2 * hexVectorCount * i)
        }
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewport, 0)
        frustum(frustumMatrix, width, height, 1f, 100f)
        updateMVP()
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        // Set the background frame color
        GLES20.glClearColor(0f, 0f, 0f, 0f)

        // create program
        try {
            val vertexShader = context.getString(R.string.hex_vertex_shader)
            val fragmentShader = context.getString(R.string.hex_fragment_shader)
            val program = GLES20Program(vertexShader, fragmentShader)
            program.use()

            mvpLocation = program.getUniformLocation("uMVPMatrix")
            updateMVP()

            // initialize buffers
            colorBuffer = GLES20Buffer(GLES20.GL_ARRAY_BUFFER, GLES20.GL_STATIC_DRAW)
            colorBuffer.setData(ByteBuffer.allocateDirect(centers + points).apply {
                colors.rewind()
                for (i in 0 until centers)
                    put(colors)
                for (i in 0 until points)
                    put(-1) // black
                flip()
            })
            program.setVertexAttribPointer("aColor", 1, GLES20.GL_BYTE, false, 0, 0)
            val vertexBuffer = GLES20Buffer(GLES20.GL_ARRAY_BUFFER, GLES20.GL_STATIC_DRAW)
            vertexBuffer.setData(vectors)
            program.setVertexAttribPointer("aPosition", 2, GLES20.GL_BYTE, false, 0, 0)
            val indiceBuffer = GLES20Buffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, GLES20.GL_STATIC_DRAW)
            indiceBuffer.setData(indices)
        } catch (e: GLES20Program.GLES20ProgramException) {
            e.printStackTrace()
        }
    }

    // touch

    private var positionDown: Pair<Int, Int>? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> positionDown = getPosition(event.rawX, event.rawY)
            MotionEvent.ACTION_UP -> positionDown?.also {
                if (getPosition(event.rawX, event.rawY) == it)
                    onClick?.invoke(it.first, it.second)
                positionDown = null
            }
        }
        return true
    }

    // Point of view

    fun setPointOfView(eyeZ: Float) {
        Matrix.setLookAtM(povMatrix, 0, 0f, 0f, eyeZ, 0f, 0f, 0f, 0f, 1f, 0f)
        queueEvent { updateMVP() }
    }

    // colors

    fun getColor(row: Int, column: Int) = colors.get(getHexagonCenterIndex(row, column))
    fun setColor(row: Int, column: Int, color: Byte) = setColor(getHexagonCenterIndex(row, column), color)


    private fun setColor(index: Int, color: Byte) {
        colors.put(index, color)
        colors.rewind()
        queueEvent { colorBuffer.setSubData(colors, 0) }
    }

    // position

    private fun getPosition(x: Float, y: Float): Pair<Int, Int>? {
        unproject(tmpVector, viewport, mvpMatrix, planZ, x, y)
        val row = round(tmpVector[VECTOR_Y] / tmpVector[VECTOR_W] / 3) ?: return null
        val column = round((tmpVector[VECTOR_X] / tmpVector[VECTOR_W] + row) / 2) ?: return null
        return Pair(row + level, column + level)
    }

    private fun round(value: Float): Int? {
        val rounded = Math.round(value)
        if (Math.abs(rounded) <= level && Math.abs(value - rounded) < 0.4f)
            return rounded
        return null
    }

    // tools

    private fun updateMVP() {
        Matrix.multiplyMM(tmp1Matrix, 0, frustumMatrix, 0, povMatrix, 0)
        Matrix.multiplyMM(tmp2Matrix, 0, tmp1Matrix, 0, sensor.rotationMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, tmp2Matrix, 0, rescaleMatrix, 0)
        GLES20.glUniformMatrix4fv(mvpLocation, 1, false, mvpMatrix, 0)
    }

    private fun getHexagonCenterIndex(row: Int, column: Int) = row * size + column
    private fun getHexagonPointsIndex(row: Int, column: Int) = centers + row * rowSize + column
    private fun getX(row: Int, column: Int) = column * 2 - row
    private fun getY(row: Int, @Suppress("UNUSED_PARAMETER") column: Int) = row * 3
}

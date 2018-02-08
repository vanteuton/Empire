package android.bowz.fr.empire

import android.opengl.GLES20
import java.io.Closeable

class GLES20Program(vertexSource: String, fragmentSource: String) : Closeable {
    class GLES20ProgramException(message: String) : Exception(message)

    private var program: Int = GLES20.glCreateProgram()

    init {
        try {
            loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
            loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
            GLES20.glLinkProgram(program)
            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] != GLES20.GL_TRUE) {
                throw GLES20ProgramException(GLES20.glGetProgramInfoLog(program))
            }
        } catch (e: GLES20ProgramException) {
            GLES20.glDeleteProgram(program)
            throw e
        }
    }

    override fun close() {
        GLES20.glDeleteProgram(program)
        program = 0
    }

    fun use() {
        checkProgram()
        GLES20.glUseProgram(program)
    }

    fun getUniformLocation(name: String) = checkLocation(GLES20.glGetUniformLocation(program, name))
    fun getAttribLocation(name: String) = checkLocation(GLES20.glGetAttribLocation(program, name))

    fun setVertexAttribPointer(name: String, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int) {
        val location = getAttribLocation(name)
        GLES20.glEnableVertexAttribArray(location)
        GLES20.glVertexAttribPointer(location, size, type, normalized, stride, offset)
    }

    // private part

    private fun checkLocation(location: Int): Int {
        if (location == -1) {
            checkProgram()
            throw GLES20ProgramException("Invalid location") //$NON-NLS-1$
        }
        return location
    }

    private fun checkProgram() {
        if (program == 0) {
            throw GLES20ProgramException("Program deleted") //$NON-NLS-1$
        }
    }

    private fun loadShader(type: Int, source: String) {
        val shader = GLES20.glCreateShader(type)
        try {
            GLES20.glShaderSource(shader, source)
            GLES20.glCompileShader(shader)
            val compiled = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] != GLES20.GL_TRUE) {
                GLES20.glDeleteProgram(program)
                throw GLES20ProgramException(GLES20.glGetShaderInfoLog(shader))
            }
            GLES20.glAttachShader(program, shader)
        } finally {
            GLES20.glDeleteShader(shader)
        }
    }
}

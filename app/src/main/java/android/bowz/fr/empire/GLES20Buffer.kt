package android.bowz.fr.empire

import android.opengl.GLES20
import java.io.Closeable
import java.nio.Buffer

class GLES20Buffer(private val target: Int, private val usage: Int) : Closeable {
    private val bufferId: Int

    init {
        val b = IntArray(1)
        GLES20.glGenBuffers(1, b, 0)
        bufferId = b[0]
    }

    override fun close() {
        GLES20.glDeleteBuffers(1, IntArray(bufferId), 0)
    }

    fun setSize(size: Int) {
        GLES20.glBindBuffer(target, bufferId)
        GLES20.glBufferData(target, size, null, usage)
    }

    fun setData(data: Buffer) {
        GLES20.glBindBuffer(target, bufferId)
        GLES20.glBufferData(target, data.remaining(), data, usage)
    }

    fun setSubData(data: Buffer, offset: Int) {
        GLES20.glBindBuffer(target, bufferId)
        GLES20.glBufferSubData(target, offset, data.remaining(), data)
    }
}

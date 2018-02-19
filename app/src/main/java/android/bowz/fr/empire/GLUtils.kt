@file:Suppress("unused")

package android.bowz.fr.empire

import android.opengl.Matrix

const val VECTOR_X = 0
const val VECTOR_Y = 1
const val VECTOR_Z = 2
const val VECTOR_W = 3
const val VECTOR_SIZE = 4

const val VIEWPORT_ORIGIN_X = 0
const val VIEWPORT_ORIGIN_Y = 1
const val VIEWPORT_WIDTH = 2
const val VIEWPORT_HEIGHT = 3
const val VIEWPORT_SIZE = 4

const val MATRIX_SIZE = 16

fun createVector() = FloatArray(VECTOR_SIZE)
fun createViewport() = IntArray(VIEWPORT_SIZE)
fun createMatrix() = FloatArray(MATRIX_SIZE)

fun unproject(result: FloatArray, viewport: IntArray, mvp: FloatArray, plan: FloatArray, x: Float, y: Float) {
    val mvpInvMatrix = createMatrix()
    val tmpMatrix = createMatrix()
    val planVector = createVector()
    Matrix.invertM(mvpInvMatrix, 0, mvp, 0)
    Matrix.transposeM(tmpMatrix, 0, mvpInvMatrix, 0)
    Matrix.multiplyMV(planVector, 0, tmpMatrix, 0, plan, 0)

    // normalize screen coordinates (with Z set to 0)
    // viewport origin is top left, vector origin is computed to be bottom left
    val screenVector = createVector()
    screenVector[VECTOR_X] = 2f * (x - viewport[VIEWPORT_ORIGIN_X]) / viewport[VIEWPORT_WIDTH] - 1f
    screenVector[VECTOR_Y] = 2f * (viewport[VIEWPORT_ORIGIN_Y] - y) / viewport[VIEWPORT_HEIGHT] + 1f
    screenVector[VECTOR_Z] = 0f
    screenVector[VECTOR_W] = 1f

    // find the Z which cause intersection between a line from screen coordinates and the plan
    screenVector[VECTOR_Z] = -scalar(planVector, screenVector) / planVector[VECTOR_Z]

    // unproject the point
    Matrix.multiplyMV(result, 0, mvpInvMatrix, 0, screenVector, 0)
}

fun scalar(vector1: FloatArray, vector2: FloatArray) = vector1.zip(vector2, Float::times).sum()

fun difference(resultVector: FloatArray, vector1: FloatArray, vector2: FloatArray) {
    resultVector.forEachIndexed { i, _ ->
        resultVector[i] = vector1[i] * vector2[VECTOR_W] - vector1[VECTOR_W] * vector2[i]
    }
}

/**Alors ça pour ce que j'en comprends ça adapte l'affichage à la taille d'écrans
 * Me demande MEME PAS pour les paramètres
 */
fun frustum(frustum: FloatArray, width: Int, height: Int, near: Float, far: Float) {
    if (width > height) {
        val ratio = height.toFloat() / width
        Matrix.frustumM(frustum, 0, -1f, 1f, -ratio, ratio, near, far)
    } else {
        val ratio = width.toFloat() / height
        Matrix.frustumM(frustum, 0, -ratio, ratio, -1f, 1f, near, far)
    }
}

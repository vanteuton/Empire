package android.bowz.fr.empire

import android.app.Activity
import android.os.Bundle
import android.view.View

class MainActivity : Activity() {
    private lateinit var hexagonView: HexagonView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE)

        hexagonView = HexagonView(this)
        hexagonView.onClick = { row, column ->
            hexagonView.setColor(row, column, ((hexagonView.getColor(row, column) + 1) % 3).toByte())
        }
        hexagonView.setPointOfView(4f)

        setContentView(hexagonView)
    }

    override fun onResume() {
        super.onResume()
        hexagonView.onResume()
    }

    override fun onPause() {
        hexagonView.onPause()
        super.onPause()
    }
}

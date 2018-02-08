package android.bowz.fr.empire

class Retrofit(private val mEmail: String, private val mPassword: String) {
    private val serverUrl = "http://lfbn-1-9328-179.w86-237.abo.wanadoo.fr/"
    private val urlWithCredencials = serverUrl + "api/login?email=" + mEmail + "&password=" + mPassword

    fun execute() {

    }
}
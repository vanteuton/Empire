package android.bowz.fr.empire

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.reflect.TypeToken





/**
*Activité de login simple, email et password.
*/
class LoginActivity : AppCompatActivity() {

    private var mService: EmpiresService? = null
    val gson = Gson()
    var userTypeToken = object : TypeToken<ArrayList<User>>(){}.type
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email.setText("quentin.duteil@gmail.com")
        password.setText("bobowz")
        mService = ApiUtils.empiresService
        email_sign_in_button.setOnClickListener { attemptLogin() }
    }

    /**
    * Essaie de faire une connexion en récupérant les infos des textEdit. Si la co échoue on ne retente pas et on présente l'erreur.
    */
    private fun attemptLogin() {

        // Reset errors.
        email.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (passwordStr.isEmpty() && !isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            email.error = getString(R.string.error_invalid_email)
            focusView = email
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true)
            loadToken()
        }
    }


    /**
     * Vérifie si l'email est valide
     *
     * @param email La String de l'email à tester
     */
    private fun isEmailValid(email: String): Boolean {
        //TODO: Replace this with your own logic
        return email.contains("@")
    }

    /**
     * Vérifie si le password est valide
     *
     * @param password La String du password à tester
     */
    private fun isPasswordValid(password: String): Boolean {
        //TODO: Replace this with your own logic
        return password.length > 4
    }


    /**
     * Affiche un spinner et cache le formulaire
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            login_form.visibility = if (show) View.GONE else View.VISIBLE
            login_form.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_form.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }


    /**
     * Envoi le mail et le pass au serveur et essaie de récupérer le Token
     * Si le token est recupéré la fonction appelle le chargement de la partie en cours : loadWorld
     */
    fun loadToken() {
        mService!!.getLogin("bobowz", "quentin.duteil@gmail.com").enqueue(object : Callback<ReturnMessage> {
            override fun onResponse(call: Call<ReturnMessage>, response: Response<ReturnMessage>) {

                if (response.isSuccessful()) {
                    Log.d("MainActivityLoadAnswers", "posts loaded from API")
                    val accessToken = response.body()?.data?.accessToken
                    loadplayer(accessToken!!)
                } else {
                    val statusCode = response.code()
                    // handle request errors depending on status code
                }
            }

            override fun onFailure(call: Call<ReturnMessage>, t: Throwable) {
                Log.d("MainActivityLoadAnswers", "error loading from API")

            }
        })
    }



    /**
     * Envoi le token au serveur et essaie de récupérer les variables du jeu en cours
     * Si tout est recupéré la fonction charge la partie en cours : MainActivity
     */
    fun loadplayer(accessToken : String){
        mService!!.getUser("Bearer $accessToken").enqueue(object : Callback<ReturnMessage> {
            override fun onResponse(call: Call<ReturnMessage>, response: Response<ReturnMessage>) {
                if (response.isSuccessful) {
                    val user = response.body()?.user
                    print(response.body())
                    val map = response.body()?.user?.map
//                    val province = Province(response.body()?.user?.provinces!![0].name)
                    Log.d("jsonResponse",map.toString())
//                    val intent = Intent(this@LoginActivity,MainActivity::class.java)
//                    val user = gson.fromJson<User>(response.body()?.user.toString(),userTypeToken)
//                    intent.putExtra("user",response.body()?.user.toString())
//                    Toast.makeText(this@LoginActivity,"user -> ${user!!.provinces.toString()}",Toast.LENGTH_LONG).show()
//                    startActivity(intent)
                } else {
                    val statusCode = response.code()
                    // handle request errors depending on status code
                }
            }

            override fun onFailure(call: Call<ReturnMessage>, t: Throwable) {
                Log.d("MainActivity loadplayer", "error loading from API")

            }
        })
    }

}

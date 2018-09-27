package ar.com.demo.firebase.firebasedemo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.widget.Toast
import android.util.Log
import android.view.View
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var validator = EmailPassValidator()
    private lateinit var analyticsEngine: AnalyticsEngine

    enum class ScreenMode { LOGIN, REGISTRATION }

    var mode = ScreenMode.LOGIN

    companion object {
        val TAG = "TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance();
        analyticsEngine = AnalyticsEngine(this)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth?.getCurrentUser()
        updateUI(currentUser)
    }


    fun updateUI(user: FirebaseUser?, justRegistered: Boolean = false) {

        if (user == null) {

            when (mode) {
                ScreenMode.LOGIN -> {
                    setupLogin()
                }
                ScreenMode.REGISTRATION -> {
                    setupRegistration()
                }
            }

        } else {
            // LoggedIn continue
            if (justRegistered) analyticsEngine.signUp() else analyticsEngine.login()
            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
        }
    }

    fun setupRegistration() {
        sectionTitle.text = "Email Registration"
        registration_button_register.visibility = View.VISIBLE
        registration_button_login.visibility = View.GONE
        registration_link.visibility = View.GONE
        registration_button_register.setOnClickListener({ validator.validate(email.str(), pass.str(), ::registerUser, ::failedFirebase) })
    }

    fun setupLogin() {
        sectionTitle.text = "Email login"
        registration_button_register.visibility = View.GONE
        registration_button_login.visibility = View.VISIBLE
        registration_link.visibility = View.VISIBLE
        registration_link.setOnClickListener { mode = ScreenMode.REGISTRATION; updateUI(null) }
        registration_button_login.setOnClickListener({ validator.validate(email.str(), pass.str(), ::loginUser, ::failedFirebase) })
    }

    fun failedFirebase(fail: FirebaseError) {
        Toast.makeText(this@MainActivity, fail.msj, Toast.LENGTH_SHORT).show()
    }

    fun loginUser(email: String, password: String) {
        mAuth?.let {
            it.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            val user = it.getCurrentUser()
                            updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(this@MainActivity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                            updateUI(null)
                        }
                    }
        }
    }

    fun registerUser(email: String, password: String) {
        mAuth?.let {
            it.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val user = it.getCurrentUser()
                            updateUI(user, true)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(this@MainActivity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                            updateUI(null)
                        }
                    }.addOnFailureListener { failedFirebase(FirebaseError(it.message)) }
        }

    }

    override fun onBackPressed() {
        if (mode == ScreenMode.REGISTRATION) {
            mode = ScreenMode.LOGIN
            updateUI(null)
        } else super.onBackPressed()
    }
}

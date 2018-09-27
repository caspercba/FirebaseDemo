package ar.com.demo.firebase.firebasedemo

class EmailPassValidator {


    fun validate(email: String?, password: String?, success: (email: String, pass: String) -> Unit, fail: (error: FirebaseError) -> Unit) {
        if(email.isNullOrEmpty()) {
            fail(FirebaseError("Email is empty"))
            return
        }
        if(password.isNullOrEmpty()) {
            fail(FirebaseError("Password is empty"))
            return
        }
        if(email!!.length < 6) {
            fail(FirebaseError("Email too short"))
            return
        }
        if(email!!.length < 6 || password!!.length < 6) {
            fail(FirebaseError("Email or Password too short"))
            return
        }
        success.invoke(email, password)
    }

}
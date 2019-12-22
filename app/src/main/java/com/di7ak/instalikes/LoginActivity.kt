package com.di7ak.instalikes

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import com.di7ak.instalikes.net.insta.InstaApi
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CONFIRM_LOGIN = 0
    }

    private var isProgress: Boolean = false
        set(value) {
            field = value

            progress.visibility = if(value) View.VISIBLE else View.GONE
            btnLogin.isEnabled = !value
            username.isEnabled = !value
            password.isEnabled = !value
        }

    @Suppress("deprecation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin.setOnClickListener { attemptLogin() }

        privacy_policy.movementMethod = LinkMovementMethod.getInstance()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            privacy_policy.text = Html.fromHtml(getString(R.string.privacy_policy_label), Html.FROM_HTML_MODE_LEGACY)
        } else {
            privacy_policy.text = Html.fromHtml(getString(R.string.privacy_policy_label))
        }

        privacy_policy.setOnCheckedChangeListener { _, isChecked ->
            btnLogin.isEnabled = isChecked
        }
    }

    private fun attemptLogin() {
        isProgress = true
        InstaApi.login(
            username.text.toString().trim(),
            password.text.toString().trim()
        ) { result, error ->
            result?.let { resultNonNull ->
                isProgress = false

                when {
                    resultNonNull.authenticated -> {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }

                    resultNonNull.twoFactorRequired -> {
                        startActivityForResult(
                            Intent(
                                this,
                                ConfirmCodeActivity::class.java
                            ), REQUEST_CONFIRM_LOGIN
                        )
                    }

                    resultNonNull.message == "checkpoint_required" -> checkpoint(resultNonNull.checkpointUrl)

                    else -> Toast.makeText(this, R.string.auth_error, Toast.LENGTH_SHORT).show()
                }
            }
            error?.let {
                isProgress = false
                Toast.makeText(this, R.string.connection_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkpoint(url: String) {
        isProgress = true
        InstaApi.checkpoint(url) { result, error ->
            isProgress = false
            if(result != null) {
                if (result.message?.isNotEmpty() == true) {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                } else {
                    startActivityForResult(
                        Intent(
                            this,
                            ConfirmCodeActivity::class.java
                        ).apply {
                            putExtra(ConfirmCodeActivity.EXTRA_URL, result.navigation?.forward)
                        }, REQUEST_CONFIRM_LOGIN
                    )
                }
            }
            if(error != null) {
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CONFIRM_LOGIN && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}

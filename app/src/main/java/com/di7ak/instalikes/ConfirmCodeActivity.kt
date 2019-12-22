package com.di7ak.instalikes

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.di7ak.instalikes.net.insta.InstaApi
import kotlinx.android.synthetic.main.activity_confirm_code.*
import java.lang.StringBuilder

class ConfirmCodeActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val EXTRA_URL = "challenge_url"
    }
    private var isProgress: Boolean = false
        set(value) {
            field = value

            progress.visibility = if(value) View.VISIBLE else View.GONE
            pinTitle.visibility = if(!value) View.VISIBLE else View.GONE
            backspace.isEnabled = !value
            num0.isEnabled = !value
            num1.isEnabled = !value
            num2.isEnabled = !value
            num3.isEnabled = !value
            num4.isEnabled = !value
            num5.isEnabled = !value
            num6.isEnabled = !value
            num7.isEnabled = !value
            num8.isEnabled = !value
            num9.isEnabled = !value
        }
    private var inputCode: StringBuilder = StringBuilder()
    private var challengeUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_code)

        challengeUrl = intent.getStringExtra(EXTRA_URL) ?: ""

        setupButtons()
    }

    private fun checkCode() {
        isProgress = true

        if(challengeUrl.isEmpty()) {
            InstaApi.confirmCode(inputCode.toString()) { result, error ->
                result?.let { resultNonNull ->
                    isProgress = false
                    if (resultNonNull.authenticated) {
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        pinTitle.text = resultNonNull.message
                        inputCode.clear()
                        updatePinView("")
                    }
                }
                error?.let {
                    isProgress = false
                    pinTitle.setText(R.string.connection_error)
                }
                if (result == null && error == null) {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        } else {
            InstaApi.checkPointConfirmCode(challengeUrl, inputCode.toString()) { result, error ->
                if(result != null) {
                    isProgress = false
                    if(result.authenticated) {
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        pinTitle.text = result.message
                        inputCode.clear()
                        updatePinView("")
                    }
                } else {
                    isProgress = false
                    pinTitle.setText(R.string.connection_error)
                }
            }
        }
    }

    private fun setupButtons() {
        num0.setOnClickListener(this)
        num1.setOnClickListener(this)
        num2.setOnClickListener(this)
        num3.setOnClickListener(this)
        num4.setOnClickListener(this)
        num5.setOnClickListener(this)
        num6.setOnClickListener(this)
        num7.setOnClickListener(this)
        num8.setOnClickListener(this)
        num9.setOnClickListener(this)
        backspace.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.num0 -> onInput(0)
            R.id.num1 -> onInput(1)
            R.id.num2 -> onInput(2)
            R.id.num3 -> onInput(3)
            R.id.num4 -> onInput(4)
            R.id.num5 -> onInput(5)
            R.id.num6 -> onInput(6)
            R.id.num7 -> onInput(7)
            R.id.num8 -> onInput(8)
            R.id.num9 -> onInput(9)
            R.id.backspace -> onBackspace()
        }
    }

    private fun updatePinView(code: String) {
        pinCode.text = code
    }

    private fun onInput(num: Int) {
        inputCode.append(num)
        updatePinView(inputCode.toString())
        if (inputCode.length == 6) checkCode()
    }

    private fun onBackspace() {
        if (inputCode.isEmpty()) return
        inputCode.deleteCharAt(inputCode.length - 1)
        updatePinView(inputCode.toString())
    }

}

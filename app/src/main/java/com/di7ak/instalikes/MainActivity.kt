package com.di7ak.instalikes

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.di7ak.instalikes.net.insta.InstaApi
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_LOGIN = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnLogout.setOnClickListener { logout() }


    }

    private fun logout() {
        JobsService.stop(this)
        InstaApi.logout()
        startActivityForResult(Intent(this, LoginActivity::class.java), REQUEST_LOGIN)
    }

    private fun checkAuth() {
        if (!InstaApi.isLoggedIn) {
            startActivityForResult(Intent(this, LoginActivity::class.java), REQUEST_LOGIN)
        } else {
            JobsService.start(this)
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == Activity.RESULT_CANCELED) finish()
            else JobsService.start(this)
        }
    }


}
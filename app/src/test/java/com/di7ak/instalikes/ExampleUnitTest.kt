package com.di7ak.instalikes

import com.di7ak.instalikes.net.insta.InstaApi
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun login() {
        InstaApi.login("di7ak", "q8jb2pj3")
        print(InstaApi.userId)
        assert(InstaApi.isLoggedIn)
    }
}

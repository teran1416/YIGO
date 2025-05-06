package com.example.yigo

import com.example.yigo.controller.LoginController
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class LoginTest {

    @Test
    fun testInicioSesion() {
        val correo = "mariomd2807@gmail.com"
        val password = "test1234"

        LoginController.iniciarSesion(correo, password)

        Thread.sleep(8000) //
    }
}
import com.example.yigo.controller.RegisterController
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class RegisterControllerTest {

    @Test
    fun testRegistrarPersonaSuccess() = runTest {
        val result = RegisterController.registrarPersona(
            correo = "test@example.com",
            password = "123456",
            telefono = "3000000000",
            nombre = "Juan",
            apellido = "Pérez"
        )

        assertTrue(result)
    }

    @Test
    fun testRegistrarEmpresaSuccess() = runTest {
        val result = RegisterController.registrarEmpresa(
            correo = "empresa@example.com",
            password = "empresa123",
            telefono = "3100000000",
            razon_social = "Mi Empresa S.A.S",
            nit = "123456789",
            rut_url = "https://mi-storage.com/rut.pdf"
        )

        assertTrue(result)
    }
}

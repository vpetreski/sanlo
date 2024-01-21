import io.vanja.Sanlo
import org.junit.jupiter.api.Test

class SanloTest {
    private val sanlo: Sanlo = Sanlo()

    @Test
    fun testExecute() {
        sanlo.execute(true)
    }
}
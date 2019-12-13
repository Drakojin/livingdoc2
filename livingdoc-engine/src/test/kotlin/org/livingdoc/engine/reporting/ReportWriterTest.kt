package org.livingdoc.engine.reporting

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import strikt.api.Assertion
import strikt.api.expectThat
import strikt.assertions.endsWith
import strikt.assertions.startsWith
import java.io.File

private fun Assertion.Builder<Array<out File>?>.isNullOrEmpty() =
    assertThat("is null or empty") {
        it == null || it.isEmpty()
    }

private fun Assertion.Builder<Array<out File>>.hasSize(size: Int) =
    assertThat("is null or empty") {
        it.size == size
    }

internal class ReportWriterTest {

    val cut = ReportWriter()

    @AfterEach
    fun cleanUp() {
        File("build/livingdoc").deleteRecursively()
    }

    @Test
    fun `report file is correctly written to build directory`() {
        val textToWrite = """
            <html>
                <body>
                    <p>TEXT</p>
                </body>
            </html>
        """.trimIndent()

        var listFiles = File("build/livingdoc/reports").listFiles()
        expectThat(listFiles).isNullOrEmpty()

        cut.writeToFile(textToWrite)

        listFiles = File("build/livingdoc/reports").listFiles()
        expectThat(listFiles).hasSize(1)
        expectThat(listFiles[0].name).startsWith("result").endsWith(".html")
        expectThat(listFiles[0].readText()).isEqualIgnoreWhitespace(textToWrite)
    }
}

package io.ghostbuster91.jsondiff

import org.junit.Assert
import org.junit.Test

class DifferTest {

    @Test
    fun shouldReturnTrueForTwoIdenticalJsons() {
        val first = """{
         "id": 1
        }""".trimIndent()

        val second = """{
         "id": 1
        }""".trimIndent()
        Assert.assertTrue(compare(first,second))
    }

    fun compare(first: String, second: String): Boolean {
        return true
    }
}
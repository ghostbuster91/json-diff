package io.ghostbuster91.jsondiff

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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
        Assert.assertEquals(emptyList<DiffResult>(), compare(first, second))
    }

    @Test
    fun shouldDetectValueDifference() {
        val first = """{
         "id": 1
        }""".trimIndent()

        val second = """{
         "id": 2
        }""".trimIndent()
        Assert.assertEquals(DiffResult(
                key = "id",
                firstValue = 1.0,
                secondValue = 2.0,
                firstObject = mapOf("id" to 1.0),
                secondObject = mapOf("id" to 2.0)
        ), compare(first, second).first())
    }

    @Test
    fun shouldDetectMissingKey() {
        val first = """{
         "id": 1
        }""".trimIndent()

        val second = """{
        }""".trimIndent()
        Assert.assertEquals(DiffResult(
                key = "id",
                firstValue = 1.0,
                secondValue = null,
                firstObject = mapOf("id" to 1.0),
                secondObject = mapOf()
        ), compare(first, second).first())
    }


    fun compare(first: String, second: String): List<DiffResult> {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val type = Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
        val adapter = moshi.adapter<Map<String, Any>>(type)
        val firstJson = adapter.fromJson(first)!!
        val secondJson = adapter.fromJson(second)!!
        return firstJson.entries.fold(emptyList()) { acc, entry ->
            if (secondJson[entry.key] != entry.value) acc + DiffResult(key = entry.key,
                    firstValue = entry.value,
                    secondValue = secondJson[entry.key],
                    firstObject = firstJson,
                    secondObject = secondJson)
            else acc
        }
    }

    data class DiffResult(
            val key: String,
            val firstValue: Any?,
            val secondValue: Any?,
            val firstObject: Map<String, Any?>,
            val secondObject: Map<String, Any?>
    )
}
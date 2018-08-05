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
        Assert.assertEquals(emptyList<DiffResult.ValueDifference>(), compare(first, second))
    }

    @Test
    fun shouldDetectValueDifference() {
        val first = """{
         "id": 1
        }""".trimIndent()

        val second = """{
         "id": 2
        }""".trimIndent()
        Assert.assertEquals(DiffResult.ValueDifference(
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
        Assert.assertEquals(DiffResult.ValueDifference(
                key = "id",
                firstValue = 1.0,
                secondValue = null,
                firstObject = mapOf("id" to 1.0),
                secondObject = mapOf()
        ), compare(first, second).first())
    }

    @Test
    fun shouldDetectMissingKeyInFirst() {
        val first = """{
        }""".trimIndent()

        val second = """{
         "id": 1
        }""".trimIndent()
        Assert.assertEquals(DiffResult.ValueDifference(
                key = "id",
                firstValue = null,
                secondValue = 1.0,
                firstObject = mapOf(),
                secondObject = mapOf("id" to 1.0)
        ), compare(first, second).first())
    }

    @Test
    fun shouldDetectDifferenceRecursively() {
        val first = """{
         "id": {
            "key": "1"
         }
        }""".trimIndent()

        val second = """{
         "id": {
            "key": "2"
         }
        }""".trimIndent()
        Assert.assertEquals(DiffResult.ValueDifference(
                key = "key",
                firstValue = "1",
                secondValue = "2",
                firstObject = mapOf("key" to "1"),
                secondObject = mapOf("key" to "2")
        ), compare(first, second).first())
    }


    @Test
    fun shouldDetectDifferenceInListOfObjects() {
        val first = """{ "items":[
        {
         "id": 1
         }
        ]}""".trimIndent()

        val second = """{ "items":[
        {
         "id": 2
         }
        ]}""".trimIndent()
        Assert.assertEquals(DiffResult.ValueDifference(
                key = "id",
                firstValue = 1.0,
                secondValue = 2.0,
                firstObject = mapOf("id" to 1.0),
                secondObject = mapOf("id" to 2.0)
        ), compare(first, second).first())
    }

    @Test
    fun shouldDetectTypesMismatch() {
        val first = """{ "items":
        {
         "id": 1
         }
        }""".trimIndent()

        val second = """{ "items":[
        {
         "id": 2
         }
        ]}""".trimIndent()
        Assert.assertEquals(DiffResult.TypesMismatch(
                key = "items",
                firstObject = mapOf("items" to mapOf("id" to 1.0)),
                secondObject = mapOf("items" to listOf(mapOf("id" to 2.0)))
        ), compare(first, second).first())
    }

    @Test
    fun shouldDetectDifferenceInListOfPrimitives() {
        val first = """{ "items":[
         1,2,3
        ]}""".trimIndent()

        val second = """{ "items":[
         1,2,4
        ]}""".trimIndent()
        Assert.assertEquals(DiffResult.ValueDifference(
                key = "items",
                firstValue = 3.0,
                secondValue = 4.0,
                firstObject = mapOf("items" to listOf(1.0, 2.0, 3.0)),
                secondObject = mapOf("items" to listOf(1.0, 2.0, 4.0))
        ), compare(first, second).first())
    }

    @Test
    fun shouldDetectMissingItemInListOfPrimitives() {
        val first = """{ "items":[
         1,2,3
        ]}""".trimIndent()

        val second = """{ "items":[
         1,2
        ]}""".trimIndent()
        Assert.assertEquals(DiffResult.ValueDifference(
                key = "items",
                firstValue = 3.0,
                secondValue = null,
                firstObject = mapOf("items" to listOf(1.0, 2.0, 3.0)),
                secondObject = mapOf("items" to listOf(1.0, 2.0))
        ), compare(first, second).first())
    }

    @Test
    fun shouldDetectMissingItemInListOfPrimitivesMirrored() {
        val first = """{ "items":[
         1,2
        ]}""".trimIndent()

        val second = """{ "items":[
         1,2,3
        ]}""".trimIndent()
        Assert.assertEquals(DiffResult.ValueDifference(
                key = "items",
                firstValue = null,
                secondValue = 3.0,
                firstObject = mapOf("items" to listOf(1.0, 2.0)),
                secondObject = mapOf("items" to listOf(1.0, 2.0, 3.0))
        ), compare(first, second).first())
    }

    @Test
    fun shouldDetectNestedListDifferences() {
        val first = """{ "items":[
         [1,2]
        ]}""".trimIndent()

        val second = """{ "items":[
         [1,2,3]
        ]}""".trimIndent()
        Assert.assertEquals(DiffResult.ValueDifference(
                key = "items",
                firstValue = null,
                secondValue = 3.0,
                firstObject = mapOf("items" to listOf(listOf(1.0, 2.0))),
                secondObject = mapOf("items" to listOf(listOf(1.0, 2.0, 3.0)))
        ), compare(first, second).first())
    }

    @Test
    fun complexTest() {
        val first = """{ "items":
        [{
         "id": 2,
         "labels": ["2l1"]
         },
         {
         "id": 3,
         "labels": ["3l1", "3l2"]
         }]
        }""".trimIndent()

        val second = """{ "items":
        [{
         "id": 1,
         "labels": ["2l1"]
         },
         {
         "id": 3,
         "labels": ["3l1", "3l3"]
         }]
        }""".trimIndent()
        Assert.assertEquals(setOf(DiffResult.ValueDifference(
                key = "id",
                firstValue = 2.0,
                secondValue = 1.0,
                firstObject = mapOf("id" to 2.0, "labels" to listOf("2l1")),
                secondObject = mapOf("id" to 1.0, "labels" to listOf("2l1"))
        ), DiffResult.ValueDifference(
                key = "labels",
                firstValue = "3l2",
                secondValue = "3l3",
                firstObject = mapOf("id" to 3.0, "labels" to listOf("3l1", "3l2")),
                secondObject = mapOf("id" to 3.0, "labels" to listOf("3l1", "3l3"))
        )
        ), compare(first, second).toSet())
    }


    fun compare(first: String, second: String): List<DiffResult> {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val type = Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
        val adapter = moshi.adapter<Map<String, Any>>(type)
        val firstJson = adapter.fromJson(first)!!
        val secondJson = adapter.fromJson(second)!!
        return computeObjectDiff(emptyList(), firstJson, secondJson)
    }

    private fun computeObjectDiff(acc: List<DiffResult>, firstJson: Map<String, Any>, secondJson: Map<String, Any>): List<DiffResult> {
        return (firstJson.keys + secondJson.keys).distinct().fold(acc) { acc, key ->
            dispatchByType(key, acc, firstJson, secondJson)
        }
    }

    private fun dispatchByType(key: String, acc: List<DiffResult>, firstJson: Map<String, Any>, secondJson: Map<String, Any>): List<DiffResult> {
        return when {
            firstJson[key] != null && secondJson[key] != null && firstJson[key]!!.javaClass != secondJson[key]!!.javaClass -> {
                acc + DiffResult.TypesMismatch(key, firstJson, secondJson)
            }
            secondJson[key] is Map<*, *> && firstJson[key] is Map<*, *> ->
                computeObjectDiff(acc, firstJson[key] as Map<String, Any>, secondJson[key] as Map<String, Any>)
            secondJson[key] is List<*> && firstJson[key] is List<*> ->
                computeListDiff(acc, key, firstJson[key] as List<Any>, secondJson[key] as List<Any>, firstJson, secondJson)
            else -> computeValueDifference(key, acc, firstJson[key], secondJson[key], firstJson, secondJson)
        }
    }

    private fun computeListDiff(acc: List<DiffResult>, key: String, firstList: List<Any?>, secondList: List<Any?>, firstJson: Map<String, Any>, secondJson: Map<String, Any>): List<DiffResult> {
        return (0..Math.max(firstList.size, secondList.size)).map { firstList.getOrNull(it) to secondList.getOrNull(it) }
                .fold(acc) { acc, (first, second) ->
                    when {
                        first != null && second != null && first.javaClass != second.javaClass -> {
                            acc + DiffResult.TypesMismatch(key, firstJson, secondJson)
                        }
                        first is Map<*, *> && second is Map<*, *> -> computeObjectDiff(acc, first as Map<String, Any>, second as Map<String, Any>)
                        first is List<*> && second is List<*> ->
                            computeListDiff(acc, key, first as List<Any>, second as List<Any>, firstJson, secondJson)
                        else -> computeValueDifference(key, acc, first, second, firstJson, secondJson)
                    }
                }
    }

    private fun computeValueDifference(key: String, acc: List<DiffResult>, firstValue: Any?, secondValue: Any?, firstJson: Map<String, Any>, secondJson: Map<String, Any>): List<DiffResult> {
        return if (secondValue != firstValue) {
            acc + DiffResult.ValueDifference(key = key,
                    firstValue = firstValue,
                    secondValue = secondValue,
                    firstObject = firstJson,
                    secondObject = secondJson)
        } else acc
    }

    sealed class DiffResult {
        data class ValueDifference(
                val key: String,
                val firstValue: Any?,
                val secondValue: Any?,
                val firstObject: Map<String, Any?>,
                val secondObject: Map<String, Any?>
        ) : DiffResult()

        data class TypesMismatch(
                val key: String,
                val firstObject: Map<String, Any?>,
                val secondObject: Map<String, Any?>
        ) : DiffResult()
    }

}
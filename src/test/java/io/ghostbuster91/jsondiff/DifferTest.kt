package io.ghostbuster91.jsondiff

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert
import org.junit.Test

class DifferTest {
    private val REMOVED_STRING = "**Removed**"

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
                key = ".id",
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
                key = ".id",
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
                key = ".id",
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
                key = ".id.key",
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
                key = ".items[].id",
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
        Assert.assertEquals(DiffResult.ValueDifference(
                key = ".items",
                firstValue = "object",
                secondValue = "list",
                firstObject = mapOf("items" to mapOf("id" to 1.0)),
                secondObject = mapOf("items" to listOf(mapOf("id" to 2.0)))
        ), compare(first, second).first())
    }

    @Test
    fun shouldDetectTypesMismatch2() {
        val first = """{ "items":
        {
         "id": 1
         }
        }""".trimIndent()

        val second = """{ "items": 1 }""".trimIndent()
        Assert.assertEquals(DiffResult.ValueDifference(
                key = ".items",
                firstValue = "object",
                secondValue = "primitive",
                firstObject = mapOf("items" to mapOf("id" to 1.0)),
                secondObject = mapOf("items" to 1.0)
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
                key = ".items[]",
                firstValue = 3.0,
                secondValue = 4.0,
                firstObject = mapOf("items" to REMOVED_STRING),
                secondObject = mapOf("items" to REMOVED_STRING)
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
                key = ".items[]",
                firstValue = 3.0,
                secondValue = null,
                firstObject = mapOf("items" to REMOVED_STRING),
                secondObject = mapOf("items" to REMOVED_STRING)
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
                key = ".items[]",
                firstValue = null,
                secondValue = 3.0,
                firstObject = mapOf("items" to REMOVED_STRING),
                secondObject = mapOf("items" to REMOVED_STRING)
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
                key = ".items[][]",
                firstValue = null,
                secondValue = 3.0,
                firstObject = mapOf("items" to REMOVED_STRING),
                secondObject = mapOf("items" to REMOVED_STRING)
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
                key = ".items[].id",
                firstValue = 2.0,
                secondValue = 1.0,
                firstObject = mapOf("id" to 2.0, "labels" to REMOVED_STRING),
                secondObject = mapOf("id" to 1.0, "labels" to REMOVED_STRING)
        ), DiffResult.ValueDifference(
                key = ".items[].labels[]",
                firstValue = "3l2",
                secondValue = "3l3",
                firstObject = mapOf("id" to 3.0, "labels" to REMOVED_STRING),
                secondObject = mapOf("id" to 3.0, "labels" to REMOVED_STRING)
        )
        ), compare(first, second).toSet())
    }

    @Test
    fun shouldMatchItemsWithinListByProperty() {
        val firstJson = """{ "items":
        [{
         "id": 2
         },
         {
         "id": 3
         }]
        }""".trimIndent()

        val secondJson = """{ "items":
        [{
         "id": 3
         },
         {
         "id": 2
         }]
        }""".trimIndent()
        val propertyBasedListCombiner = createPropertyBasedListCombiner("id")
        val listCombinerMapping = mapOf(".items[]" to propertyBasedListCombiner).withDefault { orderBasedListCombiner }
        Assert.assertEquals(emptyList<DiffResult>(), compare(firstJson, secondJson, listCombinerMapping))
    }

    @Test
    fun propertyBasedListCombinerShouldBeAppliedOnlyToParticularLevel() {
        val firstJson = """{ "items":
        [{
         "id": 2,
         "sub":[{
            "id":1
            },{
            "id":2
            }]
         }]
        }""".trimIndent()

        val secondJson = """{ "items":
        [{
         "id": 2,
         "sub":[{
            "id":2
            },{
            "id":1
            }]
         }]
        }""".trimIndent()
        val propertyBasedListCombiner = createPropertyBasedListCombiner("id")
        Assert.assertEquals(listOf(
                DiffResult.ValueDifference(".items[].sub[].id", 1.0, 2.0, mapOf("id" to 1.0), mapOf("id" to 2.0)),
                DiffResult.ValueDifference(".items[].sub[].id", 2.0, 1.0, mapOf("id" to 2.0), mapOf("id" to 1.0))),
                compare(firstJson, secondJson, mapOf(".items[]" to propertyBasedListCombiner).withDefault { orderBasedListCombiner }))
    }

    @Test
    fun shouldMatchItemsWithinListByPropertyAndHandleMisses() {
        val firstJson = """{ "items":
        [{
         "id": 3
         }]
        }""".trimIndent()

        val secondJson = """{ "items":
        [{
         "id": 3
         },
         {
         "id": 2
         }]
        }""".trimIndent()
        val propertyBasedListCombiner = createPropertyBasedListCombiner("id")
        val listCombinerMapping = mapOf(".items[]" to propertyBasedListCombiner).withDefault { orderBasedListCombiner }
        Assert.assertEquals(listOf(
                DiffResult.ValueDifference(
                        key = ".items[]",
                        firstValue = null,
                        secondValue = mapOf("id" to 2.0),
                        firstObject = mapOf("items" to REMOVED_STRING),
                        secondObject = mapOf("items" to REMOVED_STRING)
                )),
                compare(firstJson, secondJson, listCombinerMapping))
    }

    private fun createPropertyBasedListCombiner(property: String): (List<Any?>, List<Any?>) -> List<Pair<Any?, Any?>> {
        return { firstList, secondList ->
            (firstList.map { first -> first to secondList.find { second -> first?.asMap()?.get(property) == second?.asMap()?.get(property) } } +
                    secondList.map { second -> firstList.find { first -> first?.asMap()?.get(property) == second?.asMap()?.get(property) } to second })
                    .distinctBy { it.first?.asMap()?.get(property) ?: it.second?.asMap()?.get(property) }
        }
    }

    fun compare(
            first: String,
            second: String,
            listCombinerMapping: Map<String, ListCombiner> = mapOf<String, ListCombiner>().withDefault { orderBasedListCombiner }
    ): List<DiffResult> {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val type = Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
        val adapter = moshi.adapter<Map<String, Any>>(type)
        val firstJson = adapter.fromJson(first)!!
        val secondJson = adapter.fromJson(second)!!
        return computeObjectDiff(firstJson, secondJson, listCombinerMapping, "")
    }

    private fun computeObjectDiff(firstJson: Map<String, Any?>, secondJson: Map<String, Any?>, listCombinerMapping: Map<String, ListCombiner>, parentKey: String): List<DiffResult> {
        return (firstJson.keys + secondJson.keys).distinct().fold(emptyList()) { acc, key ->
            acc + dispatchByType("$parentKey.$key", firstJson[key], secondJson[key], firstJson, secondJson, listCombinerMapping)
        }
    }

    private fun computeListDiff(jsonPath: String, firstList: List<Any?>, secondList: List<Any?>, firstJson: Map<String, Any?>, secondJson: Map<String, Any?>, listCombinerMapping: Map<String, ListCombiner>): List<DiffResult> {
        return listCombinerMapping.getValue(jsonPath)(firstList, secondList)
                .fold(emptyList()) { acc, (firstItem, secondItem) ->
                    acc + dispatchByType(jsonPath, firstItem, secondItem, firstJson, secondJson, listCombinerMapping)
                }
    }

    private fun dispatchByType(jsonPath: String, firstItem: Any?, secondItem: Any?, firstJson: Map<String, Any?>, secondJson: Map<String, Any?>, listCombinerMapping: Map<String, ListCombiner>): List<DiffResult> {
        return when {
            typesNotNullButDifferent(firstItem, secondItem) -> computeTypesDifference(jsonPath, firstJson, secondJson, firstItem, secondItem)
            bothAreMaps(firstItem, secondItem) -> computeObjectDiff(firstItem!!.asMap(), secondItem!!.asMap(), listCombinerMapping, jsonPath)
            bothAreLists(firstItem, secondItem) -> computeListDiff("$jsonPath[]", firstItem!!.asList(), secondItem!!.asList(), firstJson, secondJson, listCombinerMapping)
            else -> computeValueDifference(jsonPath, firstItem, secondItem, firstJson, secondJson)
        }
    }

    private fun computeTypesDifference(jsonPath: String, firstJson: Map<String, Any?>, secondJson: Map<String, Any?>, firstItem: Any?, secondItem: Any?): List<DiffResult> =
            listOf(DiffResult.ValueDifference(jsonPath, firstValue = determineType(firstItem), secondValue = determineType(secondItem), firstObject = firstJson, secondObject = secondJson))

    private fun determineType(secondItem: Any?): String {
        if (secondItem == null) {
            return "null"
        }
        return when (secondItem) {
            is Map<*, *> -> "object"
            is List<*> -> "list"
            else -> "primitive"
        }
    }

    private fun typesNotNullButDifferent(firstObject: Any?, secondObject: Any?) =
            secondObject != null && firstObject != null && secondObject.javaClass != firstObject.javaClass

    private fun bothAreLists(firstObject: Any?, secondObject: Any?) = secondObject is List<*> && firstObject is List<*>

    private fun bothAreMaps(firstObject: Any?, secondObject: Any?) = secondObject is Map<*, *> && firstObject is Map<*, *>

    private val orderBasedListCombiner: ListCombiner = { firstList: List<Any?>, secondList: List<Any?> ->
        (0..Math.max(firstList.size, secondList.size)).map { firstList.getOrNull(it) to secondList.getOrNull(it) }
    }

    private fun computeValueDifference(key: String, firstValue: Any?, secondValue: Any?, firstJson: Map<String, Any?>, secondJson: Map<String, Any?>): List<DiffResult> {
        return if (secondValue != firstValue) {
            listOf(DiffResult.ValueDifference(key = key,
                    firstValue = firstValue,
                    secondValue = secondValue,
                    firstObject = removeLists(firstJson),
                    secondObject = removeLists(secondJson)))
        } else emptyList()
    }

    sealed class DiffResult {
        data class ValueDifference(
                val key: String,
                val firstValue: Any?,
                val secondValue: Any?,
                val firstObject: Map<String, Any?>,
                val secondObject: Map<String, Any?>
        ) : DiffResult()
    }

}

typealias ListCombiner = (List<Any?>, List<Any?>) -> List<Pair<Any?, Any?>>

private fun Any.asMap() = this as Map<String, Any?>

private fun Any.asList() = this as List<Any?>

fun removeLists(item: Map<String, Any?>): Map<String, Any?> {
    return mutableMapOf<String, Any?>().apply {
        val map = item.entries.map {
            when {
                it.value is Map<*, *> -> it.key to removeLists(it.value as Map<String, Any>)
                it.value is List<*> -> it.key to "**Removed**"
                else -> it.toPair()
            }
        }
        putAll(map)
    }
}
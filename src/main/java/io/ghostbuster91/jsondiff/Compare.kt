package io.ghostbuster91.jsondiff

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

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
        listOf(DiffResult(jsonPath, firstValue = determineType(firstItem), secondValue = determineType(secondItem), firstObject = firstJson, secondObject = secondJson))

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

val orderBasedListCombiner: ListCombiner = { firstList: List<Any?>, secondList: List<Any?> ->
    (0..Math.max(firstList.size, secondList.size)).map { firstList.getOrNull(it) to secondList.getOrNull(it) }
}

private fun computeValueDifference(key: String, firstValue: Any?, secondValue: Any?, firstJson: Map<String, Any?>, secondJson: Map<String, Any?>): List<DiffResult> {
    return if (secondValue != firstValue) {
        listOf(DiffResult(key = key,
                firstValue = firstValue,
                secondValue = secondValue,
                firstObject = removeLists(firstJson),
                secondObject = removeLists(secondJson)))
    } else emptyList()
}

data class DiffResult(
        val key: String,
        val firstValue: Any?,
        val secondValue: Any?,
        val firstObject: Map<String, Any?>,
        val secondObject: Map<String, Any?>
)

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

fun createPropertyBasedListCombiner(property: String): (List<Any?>, List<Any?>) -> List<Pair<Any?, Any?>> {
    return { firstList, secondList ->
        (firstList.map { first -> first to secondList.find { second -> first?.asMap()?.get(property) == second?.asMap()?.get(property) } } +
                secondList.map { second -> firstList.find { first -> first?.asMap()?.get(property) == second?.asMap()?.get(property) } to second })
                .distinctBy { it.first?.asMap()?.get(property) ?: it.second?.asMap()?.get(property) }
    }
}
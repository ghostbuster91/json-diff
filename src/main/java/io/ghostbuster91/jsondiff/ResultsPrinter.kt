package io.ghostbuster91.jsondiff

import com.google.gson.GsonBuilder

fun printResults(diffResults: List<DiffResult>, displayOptions: DisplayOptions) {
    println("Found ${diffResults.size} differences\n")
    val formatter = PrettyJsonFormatter(ValueFormatter(displayOptions))
    diffResults.forEachIndexed { id, item ->
        println("Diff number ${id + 1}:")
        printSingleResult(item, formatter)
    }
}

private fun printSingleResult(diffResult: DiffResult, formatter: Formatter) {
    diffResult.let {
        println("Affected key: ${it.key}")
        println("First value: ${formatter.format(it.firstValue)}")
        println("Second value: ${formatter.format(it.secondValue)}")
        println("Where first belongs to \n${formatter.format(it.firstObject)}")
        println("And second belongs to \n${formatter.format(it.secondObject)}")
        println("====================================================================================")
        print("\n\n")
    }
}

private interface Formatter {
    fun format(any: Any?): Any?
}

private class ValueFormatter(private val displayOptions: DisplayOptions) : Formatter {
    override fun format(any: Any?): Any? {
        return dispatchByType(any)
    }

    private fun dispatchByType(any: Any?, depth: Int = 0): Any? {
        return when (any) {
            is Map<*, *> -> formatObject(any, depth)
            is List<*> -> formatList(any)
            else -> any
        }
    }

    private fun formatList(list: List<*>): Any {
        return list.take(displayOptions.maxItems)
    }

    private fun formatObject(map: Map<*, *>, depth: Int): Any {
        return map.mapValues { (_, value) -> if (depth < displayOptions.maxDepth) dispatchByType(value, depth) else null }
    }
}

private class PrettyJsonFormatter(private val formatter: Formatter) : Formatter {
    private val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()

    override fun format(any: Any?): Any? {
        return if (any is Map<*, *>) {
            gson.toJson(formatter.format(any))
        } else {
            any
        }
    }
}
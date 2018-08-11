package io.ghostbuster91.jsondiff

import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy

fun printResults(diffResults: List<DiffResult>, displayOptions: DisplayOptions) {
    println("Found ${diffResults.size} differences\n")
    val printer = PrettyPrinter(ValuePrinter(displayOptions))
    diffResults.forEachIndexed { id, item ->
        println("Diff number ${id + 1}:")
        printSingleResult(item, printer)
    }
}

private fun printSingleResult(diffResult: DiffResult, printer: PrettyPrinter) {
    diffResult.let {
        println("Affected key: ${it.key}")
        println("First value: ${printer.apply(it.firstValue)}")
        println("Second value: ${printer.apply(it.secondValue)}")
        println("Where first belongs to \n${printer.apply(it.firstObject)}")
        println("And second belongs to \n${printer.apply(it.secondObject)}")
        println("====================================================================================")
        print("\n\n")
    }
}

private interface Printer {
    fun apply(any: Any?): Any?
}

private class ValuePrinter(private val displayOptions: DisplayOptions) : Printer {
    override fun apply(any: Any?): Any? {
        return applyRec(any)
    }

    private fun applyRec(any: Any?, depth: Int = 0): Any? {
        return when (any) {
            is Map<*, *> -> applyToMap(any, depth)
            is List<*> -> printList(any)
            else -> any
        }
    }

    private fun printList(list: List<*>): Any {
        return list.take(displayOptions.maxItems)
    }

    private fun applyToMap(map: Map<*, *>, depth: Int): Any {
        return map.mapValues { (_, value) -> if (depth < displayOptions.maxDepth) applyRec(value, depth) else null }
    }
}

private class PrettyPrinter(private val printer: Printer) : Printer {
    private val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()

    override fun apply(any: Any?): Any? {
        return if (any is Map<*, *>) {
            gson.toJson(printer.apply(any))
        } else {
            any
        }
    }
}
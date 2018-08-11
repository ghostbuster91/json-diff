package io.ghostbuster91.jsondiff

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.pair
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int

fun main(args: Array<String>) {
    JsonDiff().main(args)
}

private class JsonDiff : CliktCommand() {

    private val firstFile by argument(name = "firstFile").file(exists = true, fileOkay = true, readable = true)
    private val secondFile by argument(name = "secondFile").file(exists = true, fileOkay = true, readable = true)
    private val property by option("-m", "--matching").pair().multiple()
    private val displayMaxItems by option("-l", "--display-max-list-size").int().default(0)
    private val displayMaxDepth by option("-d", "--display-max-depth").int().default(Int.MAX_VALUE)

    override fun run() {
        val propertyMap = property.toMap().mapValues { (_, value) -> createPropertyBasedListCombiner(value) }
        printResults(compare(firstFile.readText(), secondFile.readText(), propertyMap), DisplayOptions(displayMaxItems, displayMaxDepth))
    }
}

data class DisplayOptions(val maxItems: Int, val maxDepth: Int)
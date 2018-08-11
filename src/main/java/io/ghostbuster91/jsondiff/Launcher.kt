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

private val MATCH_LIST_HELP =
        """Detailed description

           Option '-m'

           Defines how elements from lists should be matched against each other.

           By default they are matched by their order within corresponding list, so the first element from the first list
           will be compared to the first element from the second list and so on.

           Takes a pair of strings, where the first string represents unique path to the list calculated from the top of particular json
           and the second one refers to the key from items within that list. All not matched items will be treated as missing in the corresponding list.

           Can be specified one or multiple times.
        """.trimIndent()

private class JsonDiff : CliktCommand(epilog = MATCH_LIST_HELP) {

    private val firstFile by argument(name = "firstFile").file(exists = true, fileOkay = true, readable = true)
    private val secondFile by argument(name = "secondFile").file(exists = true, fileOkay = true, readable = true)
    private val matching by option("-m", "--matching", help = "Defines mapping between two particular lists", metavar = "jsonPath itemKey").pair().multiple()
    private val displayMaxItems by option("-l", "--display-max-list-size", help = "Max items in list to be displayed. Default 0").int().default(0)
    private val displayMaxDepth by option("-d", "--display-max-depth", help = "Max depth of json to be displayed. Default infinity").int().default(Int.MAX_VALUE)

    override fun run() {
        val propertyMap = matching.toMap().mapValues { (_, value) -> createPropertyBasedListCombiner(value) }
        printResults(compare(firstFile.readText(), secondFile.readText(), propertyMap), DisplayOptions(displayMaxItems, displayMaxDepth))
    }
}

data class DisplayOptions(val maxItems: Int, val maxDepth: Int)
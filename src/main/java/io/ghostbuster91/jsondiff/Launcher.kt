package io.ghostbuster91.jsondiff

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.validate
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    JsonDiff().main(args)
}

private class JsonDiff : CliktCommand() {

    private val firstFile by argument(name = "firstFile").validate { Files.exists(Paths.get(it)) }
    private val secondFile by argument(name = "secondFile").validate { Files.exists(Paths.get(it)) }

    override fun run() {
        compare(File(firstFile).readText(), File(secondFile).readText())
                .forEach { println(it) }
    }
}
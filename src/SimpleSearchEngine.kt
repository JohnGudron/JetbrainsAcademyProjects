import java.io.File

class SimpleSearchEngine {

    fun menu(map: MutableMap<String, MutableList<Int>>, text: List<String>) {
        println("=== Menu ===")
        println(
            "1. Find a person\n" +
                    "2. Print all people\n" +
                    "0. Exit"
        )
        when (readln().toInt()) {
            1 -> {
                findPerson(map, text)
                menu(map, text)
            }
            2 -> {
                println("=== List of people ===\n" + text.joinToString("\n"))
                menu(map, text)
            }
            0 -> println("\nBye!")
            else -> {
                println("Incorrect option! Try again.")
                menu(map, text)
            }
        }
    }

    fun findPerson(map: MutableMap<String, MutableList<Int>>, text: List<String>) {
        println("Select a matching strategy: ALL, ANY, NONE")
        val mode = readln()
        println("Enter a name or email to search all matching people.")
        val query = readln().lowercase().split(" ")
        val resultSet = mutableSetOf<Int>()
        when (mode) {
            // comparing search query word with keys and adding values if it is "true" (excluding duplicates, because of "set" can't store duplicates)
            "ANY", "NONE" -> query.forEach { s -> if (s in map.keys) map.getValue(s).forEach { resultSet.add(it) } }
            "ALL" -> if (query.joinToString { " " } in map.keys) resultSet.addAll(map.getValue(query.joinToString { " " }))
            else -> println("Invalid mode")
        }
        // output lines of input, using indexes from resultSet (in mode "NONE" output all lines except indexes from resultSet)
        if (resultSet.isNotEmpty() && mode == "NONE") {
            text.forEachIndexed { index, s -> if (index !in resultSet) println(s) }
        } else if (resultSet.isNotEmpty()) text.forEachIndexed { index, s -> if (index in resultSet) println(s) }
        else println("No matching people found.")
    }

    fun main(args: Array<String>) {
        val fileName = args[1]
        val text = File(fileName).readLines()
        val map = mutableMapOf<String, MutableList<Int>>()

        // Creating of a map of key(<String>): line/ each word in line to value(<List<Int>>): indexes of lines in which key is located
        text.forEachIndexed { ind, line ->
            map[line.lowercase()] = mutableListOf(ind)
            line.split(" ").forEach { i: String ->
                map[i.lowercase()] = mutableListOf()
                text.forEachIndexed { index, s ->
                    if (s.lowercase().contains(i.lowercase())) map.getValue(i.lowercase()).add(index)
                }
            }
        }
        menu(map, text)
    }
}
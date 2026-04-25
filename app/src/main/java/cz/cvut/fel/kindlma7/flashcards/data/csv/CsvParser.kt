package cz.cvut.fel.kindlma7.flashcards.data.csv

object CsvParser {

    data class Result(val pairs: List<Pair<String, String>>, val skippedRows: Int)

    fun parse(lines: List<String>): Result {
        val pairs = mutableListOf<Pair<String, String>>()
        var skipped = 0
        var  isFirst = true

        for (line in lines) {
            if (line.isBlank()) continue

            val fields = splitCsvLine(line)
            if (isFirst) {
                isFirst = false
                val first = fields.firstOrNull()?.trim()?.lowercase()
                if (first == "question" || first == "front") {
                    skipped++
                    continue
                }
            }

            if (fields.size < 2) {
                skipped++
                continue
            }

            val question = fields[0].trim()
            val answer = fields[1].trim()
            if (question.isEmpty() || answer.isEmpty()) {
                skipped++
                continue
            }

            pairs += question to answer
        }

        return Result(pairs, skipped)
    }

    private fun splitCsvLine(line: String): List<String> {
        val fields = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false

        var i = 0
        while (i < line.length) {
            val ch = line[i]
            when {
                ch == '"' && !inQuotes -> inQuotes = true
                ch == '"' && inQuotes && i + 1 < line.length && line[i + 1] == '"' -> {
                    // escaped quote inside quoted field
                    current.append('"')
                    i++
                }
                ch == '"' && inQuotes -> inQuotes = false
                ch == ',' && !inQuotes -> {
                    fields += current.toString()
                    current.clear()
                }
                else -> current.append(ch)
            }
            i++
        }
        fields += current.toString()
        return fields
    }
}

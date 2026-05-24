package com.rohitneel.todomaster.util

import com.rohitneel.todomaster.util.AppConstants.DEFAULT_ESCAPE_CHARACTER
import com.rohitneel.todomaster.util.AppConstants.DEFAULT_LINE_END
import com.rohitneel.todomaster.util.AppConstants.DEFAULT_QUOTE_CHARACTER
import com.rohitneel.todomaster.util.AppConstants.DEFAULT_SEPARATOR
import com.rohitneel.todomaster.util.AppConstants.NO_ESCAPE_CHARACTER
import com.rohitneel.todomaster.util.AppConstants.NO_QUOTE_CHARACTER

internal object CsvUtil {

    class Writer(
        private val writer: java.io.Writer?,
        private val separator: Char = DEFAULT_SEPARATOR,
        private val quoteChar: Char = DEFAULT_QUOTE_CHARACTER,
        private val escapeChar: Char = DEFAULT_ESCAPE_CHARACTER,
        private val lineEnd: String = DEFAULT_LINE_END,
    ) {
        init {
            writer?.write("sep=$separator\n")
        }

        fun writeNext(nextLine: Array<String>) {
            val builder = StringBuilder()
            for (i in nextLine.indices) {
                if (i != 0) {
                    builder.append(separator)
                }
                val nextElement = nextLine[i]

                if (quoteChar != NO_QUOTE_CHARACTER) {
                    builder.append(quoteChar)
                }

                for (element in nextElement) {
                    if (escapeChar == NO_ESCAPE_CHARACTER && (element == quoteChar || element == escapeChar)) {
                        builder.append(escapeChar)
                    }
                    builder.append(element)
                }

                if (quoteChar != NO_QUOTE_CHARACTER) {
                    builder.append(quoteChar)
                }
            }
            builder.append(lineEnd)
            writer?.write(builder.toString())
        }

        fun close() = writer?.runCatching {
            flush()
            close()
        }
    }

    class Reader(
        private val reader: java.io.Reader?,
        private val separator: Char = DEFAULT_SEPARATOR,
        private val quoteChar: Char = DEFAULT_QUOTE_CHARACTER,
        private val escapeChar: Char = DEFAULT_ESCAPE_CHARACTER,
        private val lineEnd: String = DEFAULT_LINE_END,
    ) {

        /**
         * Reads the entire file into a List with each element being a String[] of tokens.
         */
        fun rows(): List<Array<String>> {
            var lines = reader?.readLines()
            // remove the first line if it starts with "sep="
            lines = if (lines?.firstOrNull()?.startsWith("sep=") == true) {
                lines.drop(1)
            } else {
                lines
            }
            // split the lines into chunks and trim the chunks
            return lines?.map { line ->
                val tokens = line.split(separator)
                tokens.map { token ->
                    token.replace(quoteChar, ' ').replace(escapeChar, ' ').trim()
                }.toTypedArray()
            } ?: emptyList()
        }

        fun close() = reader?.runCatching {
            close()
        }
    }
}
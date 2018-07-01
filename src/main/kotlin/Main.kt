package com.github.sschuberth.stan

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter

import com.github.sschuberth.stan.exporters.Exporter
import com.github.sschuberth.stan.exporters.JsonExporter
import com.github.sschuberth.stan.exporters.OfxV1Exporter
import com.github.sschuberth.stan.model.Statement
import com.github.sschuberth.stan.parsers.PostbankPDFParser

import java.io.File
import java.io.IOException
import java.nio.file.Files.newDirectoryStream
import java.text.ParseException
import java.util.Collections

import kotlin.system.exitProcess

object Main {
    enum class ExportFormat(val exporter: Exporter) {
        JSON(JsonExporter()),
        OFX(OfxV1Exporter());

        override fun toString() = name.toLowerCase()
    }

    @Parameter
    private var statementFiles = mutableListOf<File>()

    @Parameter(description = "The data format used for dependency information.",
            names = arrayOf("--export-format", "-f"),
            order = 0)
    private var exportFormat: ExportFormat? = null

    @Parameter(description = "Display the command line help.",
            names = arrayOf("--help", "-h"),
            help = true,
            order = 100)
    private var help = false

    @JvmStatic
    fun main(args: Array<String>) {
        val jc = JCommander(this)
        jc.parse(*args)
        jc.programName = "stan"

        if (help) {
            jc.usage()
            exitProcess(1)
        }

        val statements = mutableListOf<Statement>()

        statementFiles.forEach { file ->
            try {
                newDirectoryStream(file.absoluteFile.parentFile.toPath(), file.name).use { stream ->
                    stream.forEach { filename ->
                        try {
                            val st = PostbankPDFParser.parse(filename.toFile())
                            println("Successfully parsed statement '" + filename + "' dated from " + st.fromDate + " to " + st.toDate + ".")
                            statements.add(st)
                        } catch (e: ParseException) {
                            System.err.println("Error parsing '$filename'.")
                            e.printStackTrace()
                        }
                    }
                    println("Parsed " + statements.size + " statement(s) in total.")
                }
            } catch (e: IOException) {
                System.err.println("Error opening '$file'.")
            }
        }

        Collections.sort(statements)

        var statementIterator = statements.iterator()
        if (!statementIterator.hasNext()) {
            System.err.println("No statements found.")
            System.exit(1)
        }

        var curr = statementIterator.next()
        var next: Statement
        while (statementIterator.hasNext()) {
            next = statementIterator.next()

            if (curr.toDate.plusDays(1) != next.fromDate) {
                System.err.println("Statements '" + curr.filename + "' and '" + next.filename + "' are not consecutive.")
                System.exit(1)
            }

            if (curr.balanceNew != next.balanceOld) {
                System.err.println("Balances of statements '" + curr.filename + "' and '" + next.filename + "' are not consistent.")
                System.exit(1)
            }

            curr = next
        }

        println("Consistency checks passed successfully.")

        exportFormat?.let {
            statementIterator = statements.iterator()
            while (statementIterator.hasNext()) {
                val statement = statementIterator.next()
                val exportName = statement.filename.substringBeforeLast(".") + "." + exportFormat.toString()

                println("Exporting\n    ${statement.filename}\nto\n    $exportName")
                it.exporter.write(statement, exportName)
                println("done.")
            }
        }
    }
}

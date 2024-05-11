package org.sayandev.stickynote.bukkit

import java.io.*;
import java.util.regex.*;


object TextFileConverter {
    @JvmStatic
    fun main(args: Array<String>) {
        // Input and output file paths
        val inputFile = "D:\\input.txt"
        val outputFile = "D:\\output.txt"

        // Regular expressions for matching and extracting information
        val varDeclarationRegex = "var\\s+(\\w+)\\s+=\\s+reqClass\\(\"([^\"]+)\"\\)"
        val methodRegex = "\\.reqMethod\\(\"(\\w+)\",?\\s*(Boolean)?\\)"
        val fieldRegex = "\\.reqField\\(\"(\\w+)\"\\)"
        val enumFieldRegex = "\\.reqEnumField\\(\"(\\w+)\"\\)"

        try {
            // Read input file
            val reader = BufferedReader(FileReader(inputFile))
            val outputBuilder = StringBuilder()

            // Iterate over each line
            var line: String?
            while ((reader.readLine().also { line = it }) != null) {
                val varMatcher: Matcher = Pattern.compile(varDeclarationRegex).matcher(line)
                if (varMatcher.find()) {
                    // Matched variable declaration, transform and append to output
                    val varName: String = varMatcher.group(1)
                    val className: String = varMatcher.group(2)
                    outputBuilder.append("var ").append(varName).append(" = \"").append(className).append("\"\n")
                    outputBuilder.append("mapClass(").append(varName).append(") {\n")
                    continue
                }

                val methodMatcher: Matcher = Pattern.compile(methodRegex).matcher(line)
                if (methodMatcher.find()) {
                    // Matched method, transform and append to output
                    val methodName: String = methodMatcher.group(1)
                    val returnType = if (methodMatcher.group(2) != null) "Boolean::class" else "\"1.20.4\""
                    outputBuilder.append("\tmethodInferred(\"").append(methodName).append("\", ").append(returnType)
                        .append(")\n")
                    continue
                }

                val fieldMatcher: Matcher = Pattern.compile(fieldRegex).matcher(line)
                if (fieldMatcher.find()) {
                    // Matched field, transform and append to output
                    val fieldName: String = fieldMatcher.group(1)
                    outputBuilder.append("\tfieldInferred(\"").append(fieldName).append("\", \"1.20.4\")\n")
                    continue
                }

                val enumFieldMatcher: Matcher = Pattern.compile(enumFieldRegex).matcher(line)
                if (enumFieldMatcher.find()) {
                    // Matched enum field, transform and append to output
                    val fieldName: String = enumFieldMatcher.group(1)
                    outputBuilder.append("\tenumConstant(\"").append(fieldName).append("\")\n")
                }
            }

            // Close the block for each class mapping
            outputBuilder.append("}")

            // Write output to file
            val writer = BufferedWriter(FileWriter(outputFile))
            writer.write(outputBuilder.toString())

            // Close readers and writers
            reader.close()
            writer.close()

            println("Conversion completed successfully. Output written to $outputFile")
        } catch (e: IOException) {
            System.err.println("Error occurred: " + e.message)
        }
    }
}
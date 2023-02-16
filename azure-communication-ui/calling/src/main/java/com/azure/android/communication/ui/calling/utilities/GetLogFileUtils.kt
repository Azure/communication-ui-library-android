package com.azure.android.communication.ui.calling.utilities

import java.io.File
import java.io.InputStream

internal class GetLogFileUtils {

    // under directory data/data/package_name/files stores all the blog file,
    // we need to read acs_sdk-0-**********.blog file which is the current blog file
    // if we want to read previous blog file, the file name starts with acs_sdk-1-

    fun readBlogFile() {
        val directory = """/data/data/com.azure.android.communication.ui.callingcompositedemoapp.debug/files"""
        val currentFileName = getCurrentBlogFileName(directory, ArrayList())
        if (currentFileName != null)
            // readBlogFileContent(directory + currentFileName)
            println("########################### $directory$currentFileName")
        println("**************End****************")
    }

    private fun listBlogFileNames(filePath: String, fileNames: MutableList<String>) : List<String?>? {
        val files = File(filePath).listFiles()
        if (files != null)
            for (k in files.indices) {
                fileNames.add(files[k].name)
            }
        return fileNames
    }

    private fun getCurrentBlogFileName(filePath: String, fileNames: MutableList<String>) : String? {
        listBlogFileNames(filePath, fileNames)
        for (fileName in fileNames) {
            if (fileName.startsWith("acs_sdk-0-")) {
                return fileName
            }
        }
        return null
    }

    private fun readBlogFileContent(filePath: String) {
        val inputStream: InputStream = File(filePath).inputStream()
        val inputString = inputStream.bufferedReader().use { it.readText() }
        println(inputString)
    }
}
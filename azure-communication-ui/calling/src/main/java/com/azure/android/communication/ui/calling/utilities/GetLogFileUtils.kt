package com.azure.android.communication.ui.calling.utilities

import android.util.Log
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

internal class GetLogFileUtils {

    // under directory data/data/package_name/files stores all the blog file,
    // we need to read acs_sdk-0-**********.blog file which is the current blog file
    // if we want to read previous blog file, the file name starts with acs_sdk-1-



    fun zipLogFiles() {
//        fun zipAll(directory: String, zipFile: String) {
        val directory = """/data/data/com.azure.android.communication.ui.callingcompositedemoapp.debug/files/"""
//        val sourceFile = File(directory)
        Log.d("################## ", "zipAll start")
        ZipOutputStream(BufferedOutputStream(FileOutputStream(directory + "zip.zip"))).use {
            it.use {
                zipFiles(it, directory, "")
            }
        }
    }

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

    private fun zipFiles(zipOut: ZipOutputStream, sourceFile: String, parentDirPath: String) {

        val data = ByteArray(2048)
        val fileNames = mutableListOf<String>()
        listBlogFileNames(sourceFile, fileNames)
        for (fileName in fileNames) {
            Log.d("################## ", "for file $fileName")
            val f = File(sourceFile + fileName)
            if (f.isDirectory || !fileName.startsWith("acs_sdk") || fileName.startsWith("acs_sdk-0")) {
                Log.d("#################  skp zip for ", fileName)
                continue
            }
            FileInputStream(f).use { fi ->
                BufferedInputStream(fi).use { origin ->
                    val path = parentDirPath + File.separator + f.name
                    Log.d("#################  zip ", "Adding file: $path")
                    val entry = ZipEntry(path)
                    entry.time = f.lastModified()
                    entry.isDirectory
                    entry.size = f.length()
                    zipOut.putNextEntry(entry)
                    while (true) {
                        val readBytes = origin.read(data)
                        if (readBytes == -1) {
                            break
                        }
                        zipOut.write(data, 0, readBytes)
                    }
                }
            }
        }
        zipOut.closeEntry()
        zipOut.close()
        Log.d("################## ", "zip files process end")
    }
}
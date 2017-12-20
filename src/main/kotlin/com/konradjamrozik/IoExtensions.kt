// Author: Konrad Jamrozik, github.com/konrad-jamrozik

package com.konradjamrozik

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import java.io.InputStream
import java.net.URL
import java.nio.file.Path
import java.io.IOException
import java.io.InputStreamReader
import java.io.BufferedReader

val InputStream.text: String
    get() {
        var br: BufferedReader? = null
        val sb = StringBuilder()

        var line: String?
        try {

            br = BufferedReader(InputStreamReader(this))
            line = br.readLine()
            while (line != null) {
                sb.append(line)
                line = br.readLine()
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (br != null) {
                try {
                    br.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

        return sb.toString()
    }

val URL.text: String
  get() = this.readText()

fun Map<String, String>.toInMemoryDir(): Path {
  
  val inMemoryFS = Jimfs.newFileSystem(Configuration.unix())
  val workingDir = inMemoryFS.getPath("/work")
  
  this.forEach { fileName, text ->
    val file = workingDir.resolve(fileName)
    file.createWithTextCreatingParentDirsIfNecessary(text)
  }
  
  return workingDir
}
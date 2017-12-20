// Author: Konrad Jamrozik, github.com/konrad-jamrozik

package com.konradjamrozik

import org.apache.commons.io.FilenameUtils
import java.nio.file.Files
import java.nio.file.Path

/**
 * Returns [Path] pointing to a regular file denoted by the [file], resolved against the receiver.
 *
 * Throws an [IllegalStateException] if any of the assumptions are violated.
 */
fun Path.resolveRegularFile(file: String): Path
{
  check(this.isDirectory, { "Failed check: receiver.isDirectory, where receiver is: $this" })
  
  checkNotNull(file)
  check(file.isNotEmpty(), { "Failed check: file.length > 0, where file is: '$file'" })
  
  val resolvedFile = this.resolve(file)

  check(resolvedFile.isRegularFile, { "Failed check: resolvedFile.isRegularFile, where resolvedFile is: $resolvedFile" })
  
  return resolvedFile
}

/**
 * Returns [Path] pointing to a dir denoted by the [dir], resolved against the receiver.
 *
 * Throws an [IllegalStateException] if any of the assumptions are violated.
 */
fun Path.resolveDir(dir: String): Path {

  check(this.isDirectory, { "Failed check: receiver.isDirectory, where receiver is: $this" })

  checkNotNull(dir)
  check(dir.isNotEmpty(), { "Failed check: dir.length > 0, where dir is: '$dir'" })

  val resolvedDir = this.resolve(dir)

  check(resolvedDir.isDirectory, { "Failed check: resolvedDir.isDirectory, where resolvedDir is: $resolvedDir" })

  return resolvedDir
}

/**
 * Calls [Files.isRegularFile] with the receiver.
 */
val Path.isRegularFile: Boolean
  get() = Files.isRegularFile(this)

/**
 * Calls [Files.isDirectory] with the receiver.
 */
val Path.isDirectory: Boolean
  get() = Files.isDirectory(this)

/**
 * Calls [Files.createDirectories] with the receiver's parent.
 */

fun Path.mkdirs(): Path? {
  check(!this.isDirectory)
  return Files.createDirectories(this.parent)
}

fun Path.createDirIfNotExists(): Boolean {
  if (Files.notExists(this)) {
    Files.createDirectory(this)
    check(this.isDirectory)
    return true
  }
  return false
}

fun Path.createFileAndParentDirsIfNecessary() {
  this.parent.createDirIfNotExists()
  Files.createFile(this)
  check(this.isRegularFile)
}

fun Path.writeText(text: String) {
  check(this.isRegularFile)
    Files.write(this, text.toByteArray())
}

fun Path.createWithTextCreatingParentDirsIfNecessary(text: String) {
  this.createFileAndParentDirsIfNecessary()
  this.writeText(text)
}

val Path.files: List<Path>
  get() {
    check(this.isDirectory)
    return Files.list(this).toList().filter(Path::isRegularFile)
  }

fun Path.replaceTextInAllFiles(sourceText: String, replacementText: String) {
  check(this.isDirectory)
  this.files.forEach { it.replaceText(sourceText, replacementText) }
}

fun Path.replaceText(sourceText: String, replacementText: String) {
  check(this.isRegularFile)
  this.writeText(this.text.replace(sourceText, replacementText))
}

val Path.text: String
  get() {
    check(this.isRegularFile)
    return Files.readAllLines(this).joinToString("\n")
  }

val Path.allFilesTexts: Iterable<String>
  get() {
    check(this.isDirectory)
    return this.files.map(Path::text)
  }

fun Path.printFileNames() {
  this.files.forEach { println(it.fileName) }
}


fun Path.getExtension(): String {
  return FilenameUtils.getExtension(this.fileName.toString())
}

fun Path.copyDirRecursivelyToDirInDifferentFileSystem(destDir: Path)
{
  FileSystemsOperations().copyDirRecursivelyToDirInDifferentFileSystem(this, destDir)
}

fun Path.copyDirContentsRecursivelyToDirInDifferentFileSystem(destDir: Path) {
  FileSystemsOperations().copyDirContentsRecursivelyToDirInDifferentFileSystem(this, destDir)
}

fun List<Path>.copyFilesToDirInDifferentFileSystem(destDir: Path) {
  FileSystemsOperations().copyFilesToDirInDifferentFileSystem(this, destDir)
}

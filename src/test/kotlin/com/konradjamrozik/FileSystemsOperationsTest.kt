// Author: Konrad Jamrozik, github.com/konrad-jamrozik

package com.konradjamrozik

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters
import java.io.IOException

import java.nio.file.Files
import java.nio.file.Path

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4::class)
class FileSystemsOperationsTest
{

  @Test
  fun `Copies directory to dir in a different file system`() {
      val dir = buildFixture()
      val dest = buildDestFs()

      // Act
      dir.copyDirRecursivelyToDirInDifferentFileSystem(dest)

      val expectedPaths = arrayListOf(
              "/work/dest/dir",
              "/work/dest/dir/data1.txt",
              "/work/dest/dir/subdir",
              "/work/dest/dir/subdir/data2.txt"
      )

      expectedPaths.forEach { assert(Files.exists(dest.fileSystem.getPath(it))) }
      assert(dest.resolve("dir/data1.txt").text == "123")
      assert(dest.resolve("dir/subdir/data2.txt").text == "abc")
      assert(dest.resolve("dir/subdir/data3.txt").text == "xyz")
  }

  @Test
  fun `Copies directory contents to dir in a different file system`()
  {
    val dir = buildFixture()
    val dest = buildDestFs()

    // Act
    dir.copyDirContentsRecursivelyToDirInDifferentFileSystem(dest)

    val expectedPaths = arrayListOf(
      "/work/dest/",
      "/work/dest/data1.txt",
      "/work/dest/subdir",
      "/work/dest/subdir/data2.txt",
      "/work/dest/subdir/data3.txt"
    )

    expectedPaths.forEach { assert(Files.exists(dest.fileSystem.getPath(it))) }
    assert(dest.resolve("data1.txt").text == "123")
    assert(dest.resolve("subdir/data2.txt").text == "abc")
    assert(dest.resolve("subdir/data3.txt").text == "xyz")
  }

  @Test
  fun `Copies files to dir in a different file system`()
  {
    val dir = buildFixture()
    val data1 = dir.resolve("data1.txt")
    val data2 = dir.resolve("subdir/data2.txt")
    val dest = buildDestFs()

    // Act
    arrayListOf(data1, data2).copyFilesToDirInDifferentFileSystem(dest)

    val expectedPaths = arrayListOf(
      "/work/dest/",
      "/work/dest/data1.txt",
      "/work/dest/data2.txt"
    )

    expectedPaths.forEach { assert(Files.exists(dest.fileSystem.getPath(it))) }
    assert(dest.resolve("data1.txt").text == "123")
    assert(dest.resolve("data2.txt").text == "abc")
    assert(Files.notExists(dest.resolve("data3.txt")))
    assert(Files.notExists(dest.resolve("subdir")))
  }

  private fun buildFixture(): Path
  {
    val sourceFs = Jimfs.newFileSystem(Configuration.unix())
    val dir = sourceFs.getPath("/work/dir")
    val subdir = dir.resolve("subdir")
    val data1 = dir.resolve("data1.txt")
    val data2 = subdir.resolve("data2.txt")
    val data3 = subdir.resolve("data3.txt")
    Files.createDirectories(subdir)
    Files.createFile(data1)
    data1.writeText("123")
    Files.createFile(data2)
    data2.writeText("abc")
    Files.createFile(data3)
    data3.writeText("xyz")

    // @formatter:off
    assert(dir     .toRealPath().toString() == "/work/dir")
    assert(data1   .toRealPath().toString() == "/work/dir/data1.txt")
    assert(subdir  .toRealPath().toString() == "/work/dir/subdir")
    assert(data2   .toRealPath().toString() == "/work/dir/subdir/data2.txt")
    assert(data3   .toRealPath().toString() == "/work/dir/subdir/data3.txt")
    // @formatter:on

    assert(data1.text == "123")
    assert(data2.text == "abc")
    assert(data3.text == "xyz")
    return dir
  }

  private fun buildDestFs(): Path
  {
    val targetFs = Jimfs.newFileSystem(Configuration.unix())
    val dest = targetFs.getPath("dest")
    Files.createDirectories(dest)
    assert(Files.isDirectory(dest))
    return dest
  }

  @Test
  @Throws(IOException::class)
  fun usageInJavaExample()
  {
    val dir =  Jimfs.newFileSystem().getPath("dirWithSomeContents")
    Files.createDirectory(dir)
    val dest = Jimfs.newFileSystem().getPath("destDir")
    Files.createDirectory(dest)

    FileSystemsOperations().copyDirContentsRecursivelyToDirInDifferentFileSystem(dir, dest)
  }
}
// Author: Konrad Jamrozik, github.com/konrad-jamrozik

package com.konradjamrozik

import java.nio.file.Path

interface IFileSystemsOperations {
    fun copyDirRecursivelyToDirInDifferentFileSystem(dir: Path, dest: Path)

    fun copyDirContentsRecursivelyToDirInDifferentFileSystem(dir: Path, dest: Path)

    fun copyFilesToDirInDifferentFileSystem(files: List<Path>, dest: Path)

    fun copyDirContentsRecursivelyToDirInSameFileSystem(dir: Path, dest: Path)
}
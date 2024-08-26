package de.solidblocks.infra.test.files

import de.solidblocks.infra.test.log
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.time.TimeSource


data class File(val file: Path)

class FileBuilder(
    private val path: Path,
    private val name: String,
    private val start: TimeSource.Monotonic.ValueTimeMark = TimeSource.Monotonic.markNow()
) {

    private var content = byteArrayOf()

    private var executable = false

    fun content(content: String) = content(content.toByteArray())

    fun content(content: ByteArray) = apply {
        this.content = content
    }

    fun create(): de.solidblocks.infra.test.files.File {
        val file = this.path.resolve(name)
        File(file.toFile().absolutePath).writeBytes(content)

        log(start, "created file '${file.toFile().absolutePath}' with size ${content.size}")

        val permissions = Files.getPosixFilePermissions(file)
        permissions.add(PosixFilePermission.OWNER_EXECUTE)
        Files.setPosixFilePermissions(file, permissions)

        return File(file)
    }

    fun executable() = apply {
        this.executable = true
    }
}

data class ZipFile(val file: Path)

class ZipFileBuilder(
    private val path: Path,
    private val name: String,
    private val start: TimeSource.Monotonic.ValueTimeMark = TimeSource.Monotonic.markNow()
) {

    private val entries = mutableMapOf<String, ByteArray>()

    fun entry(file: String, content: String) = entry(file, content.toByteArray())

    fun entry(file: String, content: ByteArray) = apply {
        entries[file] = content
    }

    fun create(): ZipFile {
        val zipFile = this.path.resolve(name)
        ZipOutputStream(FileOutputStream(zipFile.toFile())).use { zipOut ->
            entries.forEach { file ->
                val zipEntry = ZipEntry(file.key)
                zipOut.putNextEntry(zipEntry)
                zipOut.write(file.value)
                zipOut.closeEntry()
            }

            zipOut.close()
        }

        log(
            start,
            "created '${zipFile.toFile().absolutePath}' with ${entries.size} entries (${
                entries.map { it.key }.joinToString(", ")
            })"
        )

        return ZipFile(zipFile)
    }

}

fun DirectoryBuilder.createFile() = apply { }

fun DirectoryBuilder.zipFile(name: String) = ZipFileBuilder(this.path, name)

fun DirectoryBuilder.createFile(name: String) = FileBuilder(this.path, name)
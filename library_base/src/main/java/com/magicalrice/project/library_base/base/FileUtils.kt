package com.magicalrice.project.library_base.base

import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * @package com.magicalrice.project.library_base.base
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description TODO
 */

object FileUtils {
    private val DEFAULT_BUFFER_SIZE = 1024 * 4
    private val LINE_SEP = System.getProperty("line.separator")

    /**
     * 根据文件路径获取文件
     *
     * @param filePath The path of file.
     * @return the file
     */
    fun getFileByPath(filePath: String): File? {
        return if (isSpace(filePath)) null else File(filePath)
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath The path of file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFileExists(filePath: String): Boolean {
        return isFileExists(getFileByPath(filePath))
    }

    /**
     * 判断文件是否存在
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFileExists(file: File?): Boolean {
        return file != null && file.exists()
    }

    /**
     * 重命名文件
     *
     * @param filePath The path of file.
     * @param newName  The new name of file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun rename(filePath: String, newName: String): Boolean {
        return rename(getFileByPath(filePath), newName)
    }

    /**
     * 重命名文件
     *
     * @param file    The file.
     * @param newName The new name of file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun rename(file: File?, newName: String): Boolean {
        // file is null then return false
        if (file == null) return false
        // file doesn't exist then return false
        if (!file.exists()) return false
        // the new name is space then return false
        if (isSpace(newName)) return false
        // the new name equals old name then return true
        if (newName == file.name) return true
        val newFile = File(file.parent + File.separator + newName)
        // the new name of file exists then return false
        return !newFile.exists() && file.renameTo(newFile)
    }

    /**
     * 判断是否是目录
     *
     * @param dirPath The path of directory.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isDir(dirPath: String): Boolean {
        return isDir(getFileByPath(dirPath))
    }

    /**
     * 判断是否是目录
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isDir(file: File?): Boolean {
        return file != null && file.exists() && file.isDirectory
    }

    /**
     * 判断是否是文件
     *
     * @param filePath The path of file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFile(filePath: String): Boolean {
        return isFile(getFileByPath(filePath))
    }

    /**
     * 判断是否是文件
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFile(file: File?): Boolean {
        return file != null && file.exists() && file.isFile
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param dirPath The path of directory.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    fun createOrExistsDir(dirPath: String): Boolean {
        return createOrExistsDir(getFileByPath(dirPath))
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file The file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    fun createOrExistsDir(file: File?): Boolean {
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param filePath The path of file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    fun createOrExistsFile(filePath: String): Boolean {
        return createOrExistsFile(getFileByPath(filePath))
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param file The file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    fun createOrExistsFile(file: File?): Boolean {
        if (file == null) return false
        if (file.exists()) return file.isFile
        if (!createOrExistsDir(file.parentFile)) return false
        try {
            return file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

    }

    /**
     * 判断文件是否存在，存在则在创建之前删除
     *
     * @param filePath The path of file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun createFileByDeleteOldFile(filePath: String): Boolean {
        return createFileByDeleteOldFile(getFileByPath(filePath))
    }

    /**
     * 判断文件是否存在，存在则在创建之前删除
     *
     * @param file The file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun createFileByDeleteOldFile(file: File?): Boolean {
        if (file == null) return false
        // file exists and unsuccessfully delete then return false
        if (file.exists() && !file.delete()) return false
        if (!createOrExistsDir(file.parentFile)) return false
        try {
            return file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

    }

    /**
     * 复制目录
     *
     * @param srcDirPath  The path of source directory.
     * @param destDirPath The path of destination directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copyDir(
        srcDirPath: String,
        destDirPath: String
    ): Boolean {
        return copyDir(getFileByPath(srcDirPath), getFileByPath(destDirPath))
    }

    /**
     * 复制目录
     *
     * @param srcDirPath  The path of source directory.
     * @param destDirPath The path of destination directory.
     * @param listener    The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copyDir(
        srcDirPath: String,
        destDirPath: String,
        listener: OnReplaceListener
    ): Boolean {
        return copyDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener)
    }

    /**
     * 复制目录
     *
     * @param srcDir  The source directory.
     * @param destDir The destination directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copyDir(
        srcDir: File?,
        destDir: File?
    ): Boolean {
        return copyOrMoveDir(srcDir, destDir, false)
    }

    /**
     * 复制目录
     *
     * @param srcDir   The source directory.
     * @param destDir  The destination directory.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copyDir(
        srcDir: File?,
        destDir: File?,
        listener: OnReplaceListener
    ): Boolean {
        return copyOrMoveDir(srcDir, destDir, listener, false)
    }

    /**
     * 复制文件
     *
     * @param srcFilePath  The path of source file.
     * @param destFilePath The path of destination file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copyFile(
        srcFilePath: String,
        destFilePath: String
    ): Boolean {
        return copyFile(getFileByPath(srcFilePath), getFileByPath(destFilePath))
    }

    /**
     * 复制文件
     *
     * @param srcFilePath  The path of source file.
     * @param destFilePath The path of destination file.
     * @param listener     The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copyFile(
        srcFilePath: String,
        destFilePath: String,
        listener: OnReplaceListener
    ): Boolean {
        return copyFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), listener)
    }

    /**
     * 复制文件
     *
     * @param srcFile  The source file.
     * @param destFile The destination file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copyFile(
        srcFile: File?,
        destFile: File?
    ): Boolean {
        return copyOrMoveFile(srcFile, destFile, false)
    }

    /**
     * 复制文件
     *
     * @param srcFile  The source file.
     * @param destFile The destination file.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copyFile(
        srcFile: File?,
        destFile: File?,
        listener: OnReplaceListener
    ): Boolean {
        return copyOrMoveFile(srcFile, destFile, listener, false)
    }

    /**
     * 移动目录
     *
     * @param srcDirPath  The path of source directory.
     * @param destDirPath The path of destination directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun moveDir(
        srcDirPath: String,
        destDirPath: String
    ): Boolean {
        return moveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath))
    }

    /**
     * 移动目录
     *
     * @param srcDirPath  The path of source directory.
     * @param destDirPath The path of destination directory.
     * @param listener    The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun moveDir(
        srcDirPath: String,
        destDirPath: String,
        listener: OnReplaceListener
    ): Boolean {
        return moveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener)
    }

    /**
     * 移动目录
     *
     * @param srcDir  The source directory.
     * @param destDir The destination directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun moveDir(
        srcDir: File?,
        destDir: File?
    ): Boolean {
        return copyOrMoveDir(srcDir, destDir, true)
    }

    /**
     * 移动目录
     *
     * @param srcDir   The source directory.
     * @param destDir  The destination directory.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun moveDir(
        srcDir: File?,
        destDir: File?,
        listener: OnReplaceListener
    ): Boolean {
        return copyOrMoveDir(srcDir, destDir, listener, true)
    }

    /**
     * 移动文件
     *
     * @param srcFilePath  The path of source file.
     * @param destFilePath The path of destination file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun moveFile(
        srcFilePath: String,
        destFilePath: String
    ): Boolean {
        return moveFile(getFileByPath(srcFilePath), getFileByPath(destFilePath))
    }

    /**
     * 移动文件
     *
     * @param srcFilePath  The path of source file.
     * @param destFilePath The path of destination file.
     * @param listener     The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun moveFile(
        srcFilePath: String,
        destFilePath: String,
        listener: OnReplaceListener
    ): Boolean {
        return moveFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), listener)
    }

    /**
     * 移动文件
     *
     * @param srcFile  The source file.
     * @param destFile The destination file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun moveFile(
        srcFile: File?,
        destFile: File?
    ): Boolean {
        return copyOrMoveFile(srcFile, destFile, true)
    }

    /**
     * 移动文件
     *
     * @param srcFile  The source file.
     * @param destFile The destination file.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun moveFile(
        srcFile: File?,
        destFile: File?,
        listener: OnReplaceListener
    ): Boolean {
        return copyOrMoveFile(srcFile, destFile, listener, true)
    }

    private fun copyOrMoveDir(
        srcDir: File?,
        destDir: File?,
        isMove: Boolean
    ): Boolean {
        return copyOrMoveDir(srcDir, destDir, object : OnReplaceListener {
            override fun onReplace(): Boolean {
                return true
            }
        }, isMove)
    }

    private fun copyOrMoveDir(
        srcDir: File?,
        destDir: File?,
        listener: OnReplaceListener?,
        isMove: Boolean
    ): Boolean {
        if (srcDir == null || destDir == null) return false
        // destDir's path locate in srcDir's path then return false
        val srcPath = srcDir.path + File.separator
        val destPath = destDir.path + File.separator
        if (destPath.contains(srcPath)) return false
        if (!srcDir.exists() || !srcDir.isDirectory) return false
        if (destDir.exists()) {
            if (listener == null || listener.onReplace()) {// require delete the old directory
                if (!deleteAllInDir(destDir)) {// unsuccessfully delete then return false
                    return false
                }
            } else {
                return true
            }
        }
        if (!createOrExistsDir(destDir)) return false
        val files = srcDir.listFiles()
        for (file in files!!) {
            val oneDestFile = File(destPath + file.name)
            if (file.isFile) {
                if (!copyOrMoveFile(file, oneDestFile, listener, isMove)) return false
            } else if (file.isDirectory) {
                if (!copyOrMoveDir(file, oneDestFile, listener, isMove)) return false
            }
        }
        return !isMove || deleteDir(srcDir)
    }

    private fun copyOrMoveFile(
        srcFile: File?,
        destFile: File?,
        isMove: Boolean
    ): Boolean {
        return copyOrMoveFile(srcFile, destFile, object : OnReplaceListener {
            override fun onReplace(): Boolean {
                return true
            }
        }, isMove)
    }

    private fun copyOrMoveFile(
        srcFile: File?,
        destFile: File?,
        listener: OnReplaceListener?,
        isMove: Boolean
    ): Boolean {
        if (srcFile == null || destFile == null) return false
        // srcFile equals destFile then return false
        if (srcFile == destFile) return false
        // srcFile doesn't exist or isn't a file then return false
        if (!srcFile.exists() || !srcFile.isFile) return false
        if (destFile.exists()) {
            if (listener == null || listener.onReplace()) {// require delete the old file
                if (!destFile.delete()) {// unsuccessfully delete then return false
                    return false
                }
            } else {
                return true
            }
        }
        if (!createOrExistsDir(destFile.parentFile)) return false
        try {
            return writeFileFromIS(destFile, FileInputStream(srcFile)) && !(isMove && !deleteFile(
                srcFile
            ))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return false
        }

    }

    /**
     * 删除文件或目录
     *
     * @param filePath The path of file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun delete(filePath: String): Boolean {
        return delete(getFileByPath(filePath))
    }

    /**
     * 删除文件或目录
     *
     * @param file The file.
     */
    fun delete(file: File?): Boolean {
        if (file == null) return false
        return if (file.isDirectory) {
            deleteDir(file)
        } else deleteFile(file)
    }

    /**
     * 删除目录
     *
     * @param dirPath The path of directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteDir(dirPath: String): Boolean {
        return deleteDir(getFileByPath(dirPath))
    }

    /**
     * 删除目录
     *
     * @param dir The directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteDir(dir: File?): Boolean {
        if (dir == null) return false
        // dir doesn't exist then return true
        if (!dir.exists()) return true
        // dir isn't a directory then return false
        if (!dir.isDirectory) return false
        val files = dir.listFiles()
        if (files != null && files.size != 0) {
            for (file in files) {
                if (file.isFile) {
                    if (!file.delete()) return false
                } else if (file.isDirectory) {
                    if (!deleteDir(file)) return false
                }
            }
        }
        return dir.delete()
    }

    /**
     * 删除文件
     *
     * @param srcFilePath The path of source file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFile(srcFilePath: String): Boolean {
        return deleteFile(getFileByPath(srcFilePath))
    }

    /**
     * 删除文件
     *
     * @param file The file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFile(file: File?): Boolean {
        return file != null && (!file.exists() || file.isFile && file.delete())
    }

    /**
     * 删除目录下所有东西
     *
     * @param dirPath The path of directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteAllInDir(dirPath: String): Boolean {
        return deleteAllInDir(getFileByPath(dirPath))
    }

    /**
     * 删除目录下所有东西
     *
     * @param dir The directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteAllInDir(dir: File?): Boolean {
        return deleteFilesInDirWithFilter(dir, FileFilter { true })
    }

    /**
     * 删除目录下所有文件
     *
     * @param dirPath The path of directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDir(dirPath: String): Boolean {
        return deleteFilesInDir(getFileByPath(dirPath))
    }

    /**
     * 删除目录下所有文件
     *
     * @param dir The directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDir(dir: File?): Boolean {
        return deleteFilesInDirWithFilter(dir, FileFilter { pathname -> pathname.isFile })
    }

    /**
     * 删除目录下所有过滤的文件
     *
     * @param dirPath The path of directory.
     * @param filter  The filter.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDirWithFilter(
        dirPath: String,
        filter: FileFilter
    ): Boolean {
        return deleteFilesInDirWithFilter(getFileByPath(dirPath), filter)
    }

    /**
     * 删除目录下所有过滤的文件
     *
     * @param dir    The directory.
     * @param filter The filter.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDirWithFilter(dir: File?, filter: FileFilter): Boolean {
        if (dir == null) return false
        // dir doesn't exist then return true
        if (!dir.exists()) return true
        // dir isn't a directory then return false
        if (!dir.isDirectory) return false
        val files = dir.listFiles()
        if (files != null && files.size != 0) {
            for (file in files) {
                if (filter.accept(file)) {
                    if (file.isFile) {
                        if (!file.delete()) return false
                    } else if (file.isDirectory) {
                        if (!deleteDir(file)) return false
                    }
                }
            }
        }
        return true
    }

    /**
     * 获取目录下所有文件
     *
     * Doesn't traverse subdirectories
     *
     * @param dirPath The path of directory.
     * @return the files in directory
     */
    fun listFilesInDir(dirPath: String): List<File>? {
        return listFilesInDir(dirPath, false)
    }

    /**
     * 获取目录下所有文件
     *
     * Doesn't traverse subdirectories
     *
     * @param dir The directory.
     * @return the files in directory
     */
    fun listFilesInDir(dir: File): List<File>? {
        return listFilesInDir(dir, false)
    }

    /**
     * 获取目录下所有文件
     *
     * @param dirPath     The path of directory.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files in directory
     */
    fun listFilesInDir(dirPath: String, isRecursive: Boolean): List<File>? {
        return listFilesInDir(getFileByPath(dirPath), isRecursive)
    }

    /**
     * 获取目录下所有文件
     *
     * @param dir         The directory.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files in directory
     */
    fun listFilesInDir(dir: File?, isRecursive: Boolean): List<File>? {
        return listFilesInDirWithFilter(dir, FileFilter { true }, isRecursive)
    }

    /**
     * 获取目录下所有过滤的文件
     *
     * Doesn't traverse subdirectories
     *
     * @param dirPath The path of directory.
     * @param filter  The filter.
     * @return the files that satisfy the filter in directory
     */
    fun listFilesInDirWithFilter(
        dirPath: String,
        filter: FileFilter
    ): List<File>? {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, false)
    }

    /**
     * 获取目录下所有过滤的文件
     *
     * Doesn't traverse subdirectories
     *
     * @param dir    The directory.
     * @param filter The filter.
     * @return the files that satisfy the filter in directory
     */
    fun listFilesInDirWithFilter(
        dir: File,
        filter: FileFilter
    ): List<File>? {
        return listFilesInDirWithFilter(dir, filter, false)
    }

    /**
     * 获取目录下所有过滤的文件
     *
     * @param dirPath     The path of directory.
     * @param filter      The filter.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files that satisfy the filter in directory
     */
    fun listFilesInDirWithFilter(
        dirPath: String,
        filter: FileFilter,
        isRecursive: Boolean
    ): List<File>? {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive)
    }

    /**
     * 获取目录下所有过滤的文件
     *
     * @param dir         The directory.
     * @param filter      The filter.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files that satisfy the filter in directory
     */
    fun listFilesInDirWithFilter(
        dir: File?,
        filter: FileFilter,
        isRecursive: Boolean
    ): List<File>? {
        if (!isDir(dir)) return null
        val list = ArrayList<File>()
        val files = dir!!.listFiles()
        if (files != null && files.size != 0) {
            for (file in files) {
                if (filter.accept(file)) {
                    list.add(file)
                }
                if (isRecursive && file.isDirectory) {

                    list.addAll(listFilesInDirWithFilter(file, filter, true)!!)
                }
            }
        }
        return list
    }

    /**
     * 获取文件最后修改的毫秒时间戳
     *
     * @param filePath The path of file.
     * @return the time that the file was last modified
     */

    fun getFileLastModified(filePath: String): Long {
        return getFileLastModified(getFileByPath(filePath))
    }

    /**
     * 获取文件最后修改的毫秒时间戳
     *
     * @param file The file.
     * @return the time that the file was last modified
     */
    fun getFileLastModified(file: File?): Long {
        return file?.lastModified() ?: -1
    }

    /**
     * 简单获取文件编码格式
     *
     * @param filePath The path of file.
     * @return the charset of file simply
     */
    fun getFileCharsetSimple(filePath: String): String {
        return getFileCharsetSimple(getFileByPath(filePath))
    }

    /**
     * 简单获取文件编码格式
     *
     * @param file The file.
     * @return the charset of file simply
     */
    fun getFileCharsetSimple(file: File?): String {
        var p = 0
        var `is`: InputStream? = null
        try {
            `is` = BufferedInputStream(FileInputStream(file!!))
            p = (`is`.read() shl 8) + `is`.read()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        when (p) {
            0xefbb -> return "UTF-8"
            0xfffe -> return "Unicode"
            0xfeff -> return "UTF-16BE"
            else -> return "GBK"
        }
    }

    /**
     * 获取文件行数
     *
     * @param filePath The path of file.
     * @return the number of lines of file
     */
    fun getFileLines(filePath: String): Int {
        return getFileLines(getFileByPath(filePath))
    }

    /**
     * 获取文件行数
     *
     * @param file The file.
     * @return the number of lines of file
     */
    fun getFileLines(file: File?): Int {
        var count = 1
        var input: InputStream? = null
        try {
            input = BufferedInputStream(FileInputStream(file!!))
            val buffer = ByteArray(1024)
            var readChars: Int
            if (LINE_SEP.endsWith("\n")) {
                readChars = input.read(buffer, 0, 1024)
                while (readChars != -1) {
                    for (i in 0 until readChars) {
                        if (buffer[i] == '\n'.toByte()) ++count
                    }
                    readChars = input.read(buffer, 0, 1024)
                }
            } else {
                readChars = input.read(buffer, 0, 1024)
                while (readChars != -1) {
                    for (i in 0 until readChars) {
                        if (buffer[i] == '\r'.toByte()) ++count
                    }
                    readChars = input.read(buffer, 0, 1024)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                input?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return count
    }

    /**
     * 获取目录大小
     *
     * @param dirPath The path of directory.
     * @return the size of directory
     */
    fun getDirSize(dirPath: String): String {
        return getDirSize(getFileByPath(dirPath))
    }

    /**
     * 获取目录大小
     *
     * @param dir The directory.
     * @return the size of directory
     */
    fun getDirSize(dir: File?): String {
        val len = getDirLength(dir)
        return if (len == -1L) "" else byte2FitMemorySize(len)
    }

    /**
     * 获取文件大小
     *
     * @param filePath The path of file.
     * @return the length of file
     */
    fun getFileSize(filePath: String): String {
        val len = getFileLength(filePath)
        return if (len == -1L) "" else byte2FitMemorySize(len)
    }

    /**
     * 获取文件大小
     *
     * @param file The file.
     * @return the length of file
     */
    fun getFileSize(file: File): String {
        val len = getFileLength(file)
        return if (len == -1L) "" else byte2FitMemorySize(len)
    }

    /**
     * 获取目录长度
     *
     * @param dirPath The path of directory.
     * @return the length of directory
     */
    fun getDirLength(dirPath: String): Long {
        return getDirLength(getFileByPath(dirPath))
    }

    /**
     * 获取目录长度
     *
     * @param dir The directory.
     * @return the length of directory
     */
    fun getDirLength(dir: File?): Long {
        if (!isDir(dir)) return -1
        var len: Long = 0
        val files = dir!!.listFiles()
        if (files != null && files.size != 0) {
            for (file in files) {
                if (file.isDirectory) {
                    len += getDirLength(file)
                } else {
                    len += file.length()
                }
            }
        }
        return len
    }

    /**
     * 获取文件长度
     *
     * @param filePath The path of file.
     * @return the length of file
     */
    fun getFileLength(filePath: String): Long {
        val isURL = filePath.matches("[a-zA-z]+://[^\\s]*".toRegex())
        if (isURL) {
            try {
                val conn = URL(filePath).openConnection() as HttpURLConnection
                conn.setRequestProperty("Accept-Encoding", "identity")
                conn.connect()
                return if (conn.responseCode == 200) {
                    conn.contentLength.toLong()
                } else -1
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return getFileLength(getFileByPath(filePath))
    }

    /**
     * 获取文件长度
     *
     * @param file The file.
     * @return the length of file
     */
    fun getFileLength(file: File?): Long {
        return if (!isFile(file)) -1 else file!!.length()
    }

    /**
     * 获取文件的 MD5 校验码
     *
     * @param filePath The path of file.
     * @return the md5 of file
     */
    fun getFileMD5ToString(filePath: String): String {
        val file = if (isSpace(filePath)) null else File(filePath)
        return getFileMD5ToString(file)
    }

    /**
     * 获取文件的 MD5 校验码
     *
     * @param file The file.
     * @return the md5 of file
     */
    fun getFileMD5ToString(file: File?): String {
        return bytes2HexString(getFileMD5(file))
    }

    /**
     * 获取文件的 MD5 校验码
     *
     * @param filePath The path of file.
     * @return the md5 of file
     */
    fun getFileMD5(filePath: String): ByteArray? {
        return getFileMD5(getFileByPath(filePath))
    }

    /**
     * 获取文件的 MD5 校验码
     *
     * @param file The file.
     * @return the md5 of file
     */
    fun getFileMD5(file: File?): ByteArray? {
        if (file == null) return null
        var dis: DigestInputStream? = null
        try {
            val fis = FileInputStream(file)
            var md = MessageDigest.getInstance("MD5")
            dis = DigestInputStream(fis, md)
            val buffer = ByteArray(1024 * 256)
            while (true) {
                if (dis.read(buffer) <= 0) break
            }
            md = dis.messageDigest
            return md.digest()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                dis?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return null
    }

    /**
     * 根据全路径获取最长目录
     *
     * @param file The file.
     * @return the file's path of directory
     */
    fun getDirName(file: File?): String {
        return if (file == null) "" else getDirName(file.absolutePath)
    }

    /**
     * 根据全路径获取最长目录
     *
     * @param filePath The path of file.
     * @return the file's path of directory
     */
    fun getDirName(filePath: String): String {
        if (isSpace(filePath)) return ""
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastSep == -1) "" else filePath.substring(0, lastSep + 1)
    }

    /**
     * 根据全路径获取文件名
     *
     * @param file The file.
     * @return the name of file
     */
    fun getFileName(file: File?): String {
        return if (file == null) "" else getFileName(file.absolutePath)
    }

    /**
     * 根据全路径获取文件名
     *
     * @param filePath The path of file.
     * @return the name of file
     */
    fun getFileName(filePath: String): String {
        if (isSpace(filePath)) return ""
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastSep == -1) filePath else filePath.substring(lastSep + 1)
    }

    /**
     * 根据全路径获取文件名不带拓展名
     *
     * @param file The file.
     * @return the name of file without extension
     */
    fun getFileNameNoExtension(file: File?): String {
        return if (file == null) "" else getFileNameNoExtension(file.path)
    }

    /**
     * 根据全路径获取文件名不带拓展名
     *
     * @param filePath The path of file.
     * @return the name of file without extension
     */
    fun getFileNameNoExtension(filePath: String): String {
        if (isSpace(filePath)) return ""
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        if (lastSep == -1) {
            return if (lastPoi == -1) filePath else filePath.substring(0, lastPoi)
        }
        return if (lastPoi == -1 || lastSep > lastPoi) {
            filePath.substring(lastSep + 1)
        } else filePath.substring(lastSep + 1, lastPoi)
    }

    /**
     * 根据全路径获取文件拓展名
     *
     * @param file The file.
     * @return the extension of file
     */
    fun getFileExtension(file: File?): String {
        return if (file == null) "" else getFileExtension(file.path)
    }

    /**
     * 根据全路径获取文件拓展名
     *
     * @param filePath The path of file.
     * @return the extension of file
     */
    fun getFileExtension(filePath: String): String {
        if (isSpace(filePath)) return ""
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastPoi == -1 || lastSep >= lastPoi) "" else filePath.substring(lastPoi + 1)
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////

    interface OnReplaceListener {
        fun onReplace(): Boolean
    }

    ///////////////////////////////////////////////////////////////////////////
    // other utils methods
    ///////////////////////////////////////////////////////////////////////////

    private val HEX_DIGITS =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

    private fun bytes2HexString(bytes: ByteArray?): String {
        if (bytes == null) return ""
        val len = bytes.size
        if (len <= 0) return ""
        val ret = CharArray(len shl 1)
        var i = 0
        var j = 0
        while (i < len) {
            ret[j++] = HEX_DIGITS[bytes[i].toInt() shr 4 and 0x0f]
            ret[j++] = HEX_DIGITS[bytes[i].toInt() and 0x0f]
            i++
        }
        return String(ret)
    }

    private fun byte2FitMemorySize(byteNum: Long): String {
        return if (byteNum < 0) {
            "shouldn't be less than zero!"
        } else if (byteNum < 1024) {
            String.format(Locale.getDefault(), "%.3fB", byteNum.toDouble())
        } else if (byteNum < 1048576) {
            String.format(Locale.getDefault(), "%.3fKB", byteNum.toDouble() / 1024)
        } else if (byteNum < 1073741824) {
            String.format(Locale.getDefault(), "%.3fMB", byteNum.toDouble() / 1048576)
        } else {
            String.format(Locale.getDefault(), "%.3fGB", byteNum.toDouble() / 1073741824)
        }
    }

    private fun isSpace(s: String?): Boolean {
        if (s == null) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }

    private fun writeFileFromIS(
        file: File,
        input: InputStream
    ): Boolean {
        var os: OutputStream? = null
        try {
            os = BufferedOutputStream(FileOutputStream(file))
            val data = ByteArray(8192)
            var len: Int
            len = input.read(data, 0, 8192)
            while (len != -1) {
                os.write(data, 0, len)
                len = input.read(data, 0, 8192)
            }
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } finally {
            try {
                input.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    /**
     * Reads the contents of a file into a String.
     * The file is always closed.
     *
     * @param file     the file to read, must not be `null`
     * @param encoding the encoding to use, `null` means platform default
     * @return the file contents, never `null`
     * @throws IOException in case of an I/O error
     * @since 2.3
     */
    @Throws(IOException::class)
    fun readFileToString(file: File, encoding: Charset): String {
        var `in`: InputStream? = null
        try {
            `in` = openInputStream(file)
            return toString(`in`, Charsets.toCharset(encoding))
        } finally {
            CloseUtils.closeIOQuietly(`in`!!)
        }
    }

    /**
     * Reads the contents of a file into a String. The file is always closed.
     *
     * @param file     the file to read, must not be `null`
     * @param encoding the encoding to use, `null` means platform default
     * @return the file contents, never `null`
     * @throws IOException                          in case of an I/O error
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of [UnsupportedEncodingException] in version 2.2 if the encoding is not
     * supported.
     * @since 2.3
     */
    @Throws(IOException::class)
    fun readFileToString(file: File, encoding: String): String {
        return readFileToString(file, Charsets.toCharset(encoding))
    }


    /**
     * Reads the contents of a file into a String using the default encoding for the VM.
     * The file is always closed.
     *
     * @param file the file to read, must not be `null`
     * @return the file contents, never `null`
     * @throws IOException in case of an I/O error
     * @since 1.3.1
     */
    @Throws(IOException::class)
    fun readFileToString(file: File): String {
        return readFileToString(file, Charset.defaultCharset())
    }

    /**
     * Opens a [FileInputStream] for the specified file, providing better
     * error messages than simply calling `new FileInputStream(file)`.
     *
     *
     * At the end of the method either the stream will be successfully opened,
     * or an exception will have been thrown.
     *
     *
     * An exception is thrown if the file does not exist.
     * An exception is thrown if the file object exists but is a directory.
     * An exception is thrown if the file exists but cannot be read.
     *
     * @param file the file to open for input, must not be `null`
     * @return a new [FileInputStream] for the specified file
     * @throws FileNotFoundException if the file does not exist
     * @throws IOException           if the file object is a directory
     * @throws IOException           if the file cannot be read
     * @since 1.3
     */
    @Throws(IOException::class)
    fun openInputStream(file: File): FileInputStream {
        if (file.exists()) {
            if (file.isDirectory) {
                throw IOException("File '$file' exists but is a directory")
            }
            if (!file.canRead()) {
                throw IOException("File '$file' cannot be read")
            }
        } else {
            throw FileNotFoundException("File '$file' does not exist")
        }
        return FileInputStream(file)
    }

    /**
     * Get the contents of an `InputStream` as a String
     * using the specified character encoding.
     *
     *
     * This method buffers the input internally, so there is no need to use a
     * `BufferedInputStream`.
     *
     *
     * @param input    the `InputStream` to read from
     * @param encoding the encoding to use, null means platform default
     * @return the requested String
     * @throws NullPointerException if the input is null
     * @throws IOException  if an I/O error occurs
     * @since 2.3
     */
    @Throws(IOException::class)
    fun toString(input: InputStream, encoding: Charset): String {
        val sw = StringBuilderWriter()
        copy(input, sw, encoding)
        return sw.toString()
    }

    /**
     * Copy bytes from an `InputStream` to chars on a
     * `Writer` using the specified character encoding.
     *
     *
     * This method buffers the input internally, so there is no need to use a
     * `BufferedInputStream`.
     *
     *
     * This method uses [InputStreamReader].
     *
     * @param input    the `InputStream` to read from
     * @param output   the `Writer` to write to
     * @param encoding the encoding to use, null means platform default
     * @throws NullPointerException if the input or output is null
     * @throws IOException  if an I/O error occurs
     * @since 2.3
     */
    @Throws(IOException::class)
    fun copy(input: InputStream, output: Writer, encoding: Charset) {
        val `in` = InputStreamReader(input, Charsets.toCharset(encoding))
        copy(`in`, output)
    }

    /**
     * Copy chars from a `Reader` to a `Writer`.
     *
     *
     * This method buffers the input internally, so there is no need to use a
     * `BufferedReader`.
     *
     *
     * Large streams (over 2GB) will return a chars copied value of
     * `-1` after the copy has completed since the correct
     * number of chars cannot be returned as an int. For large streams
     * use the `copyLarge(Reader, Writer)` method.
     *
     * @param input  the `Reader` to read from
     * @param output the `Writer` to write to
     * @return the number of characters copied, or -1 if &gt; Integer.MAX_VALUE
     * @throws NullPointerException if the input or output is null
     * @throws IOException  if an I/O error occurs
     * @since 1.1
     */
    @Throws(IOException::class)
    fun copy(input: Reader, output: Writer): Int {
        val count = copyLarge(input, output)
        return if (count > Integer.MAX_VALUE) {
            -1
        } else count.toInt()
    }

    /**
     * Copy chars from a large (over 2GB) `Reader` to a `Writer`.
     *
     *
     * This method buffers the input internally, so there is no need to use a
     * `BufferedReader`.
     *
     *
     * The buffer size is given by [.DEFAULT_BUFFER_SIZE].
     *
     * @param input  the `Reader` to read from
     * @param output the `Writer` to write to
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException  if an I/O error occurs
     * @since 1.3
     */
    @Throws(IOException::class)
    fun copyLarge(input: Reader, output: Writer): Long {
        return copyLarge(input, output, CharArray(kotlin.io.DEFAULT_BUFFER_SIZE))
    }

    /**
     * Copy chars from a large (over 2GB) `Reader` to a `Writer`.
     *
     *
     * This method uses the provided buffer, so there is no need to use a
     * `BufferedReader`.
     *
     *
     *
     * @param input  the `Reader` to read from
     * @param output the `Writer` to write to
     * @param buffer the buffer to be used for the copy
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException  if an I/O error occurs
     * @since 2.2
     */
    @Throws(IOException::class)
    fun copyLarge(input: Reader, output: Writer, buffer: CharArray): Long {
        var count: Long = 0
        var n = 0
        while (-1 != n) {
            n = input.read(buffer)
            output.write(buffer, 0, n)
            count += n.toLong()
        }
        return count
    }
}
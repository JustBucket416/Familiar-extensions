package justbucket.familiar.musicextension

import android.content.Context
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.io.FileOutputStream

class AlbumArtCache private constructor(context: Context) {

    private val cacheMap = HashMap<ByteArrayWrapper, String>()
    private val cacheDirPath = context.cacheDir.absolutePath
    private val dataDirPath = context.filesDir.absolutePath
    private val saveMutex = Mutex()
    private val cacheMutex = Mutex()

    suspend fun saveAlbumArtToCache(byteArray: ByteArray, name: String): String {
        val wrapper = ByteArrayWrapper(byteArray)
        return if (cacheMap.containsKey(wrapper)) {
            requireNotNull(cacheMap[wrapper])
        } else {
            val cacheFilePath = "$cacheDirPath/$name"
            val file = File(cacheFilePath)
            cacheMutex.withLock {
                if (file.createNewFile()) {
                    FileOutputStream(cacheFilePath).use {
                        it.write(byteArray)
                    }
                }
            }
            cacheFilePath.also { cacheMap[wrapper] = it }
        }
    }

    suspend fun saveAlbumArtFromCache(path: String): String {
        if (!path.contains(cacheDirPath)) {
            return path
        }
        val cachedArt = File(path)
        if (cachedArt.exists()) {
            val savedPath = "$dataDirPath/${path.substring(path.lastIndexOf('/') + 1)}"
            val savedArt = File(savedPath)
            saveMutex.withLock(this) {
                if (!savedArt.exists()) {
                    cachedArt.copyTo(savedArt)
                }
            }
            return savedPath
        } else {
            throw IllegalStateException("Source file does not exist!")
        }
    }

    private class ByteArrayWrapper(val array: ByteArray) {

        override fun toString(): String {
            return array.toString()
        }

        override fun equals(other: Any?): Boolean {
            if (other !is ByteArray) {
                return false
            }
            return array.contentEquals(other)
        }

        override fun hashCode(): Int {
            return array.contentHashCode()
        }
    }

    companion object : SingletonHolder<AlbumArtCache, Context>(::AlbumArtCache)
}
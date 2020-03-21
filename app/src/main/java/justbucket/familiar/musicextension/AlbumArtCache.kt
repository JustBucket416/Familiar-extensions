package justbucket.familiar.musicextension

import java.io.File
import java.io.FileOutputStream

class AlbumArtCache(private val cacheDirPath: String) {

    fun cacheAlbumArt(byteArray: ByteArray, name: String): String {
        val cacheFilePath = "$cacheDirPath/$name"
        val file = File(cacheFilePath)
        if (file.createNewFile()) {
            FileOutputStream(cacheFilePath).use {
                it.write(byteArray)
            }
        }
        return cacheFilePath
    }
}
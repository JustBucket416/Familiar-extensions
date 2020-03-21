package justbucket.familiar.musicextension

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import com.mpatric.mp3agic.ID3Wrapper
import com.mpatric.mp3agic.InvalidDataException
import com.mpatric.mp3agic.Mp3File
import justbucket.familiar.extension.ExtensionLocator
import justbucket.familiar.extension.model.DetailModel
import justbucket.familiar.extension.model.MasterModel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.coroutineContext

class MusicLocator : ExtensionLocator(EXTENSION_NAME) {

    override suspend fun getDetailsForSearch(masterModel: MasterModel): DetailModel {
        requestReadPermission()
        val mp3File = Mp3File(masterModel.detailViewLink)
        return DetailModel(
            EXTENSION_NAME,
            mp3File.getTitle(),
            masterModel.imageLink,
            getMp3Description(mp3File)
        )
    }

    override suspend fun getMasterForSearch(query: String): Set<MasterModel> {
        requestReadPermission()
        return scanDirs(query, getRootFile(), mutableListOf()).toSet()
    }

    private fun requestReadPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            with(context as Activity) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                }
            }
        }
    }

    private fun getRootFile(): File {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requireNotNull(context.getExternalFilesDir(null)?.parentFile?.parentFile?.parentFile?.parentFile)
        } else {
            Environment.getExternalStorageDirectory()
        }
    }

    private suspend fun scanDirs(
        query: String,
        rootFile: File,
        foundItems: MutableList<MasterModel>
    ): List<MasterModel> = withContext(coroutineContext) {
        val cache = AlbumArtCache(context.getExternalFilesDir(null)!!.absolutePath)
        rootFile.listFiles().forEach { file ->
                if (!file.isDirectory) {
                    if (file.extension == "mp3") {
                        try {
                            val mp3file = Mp3File(file)
                            if (file.absolutePath.contains(query) || mp3file.getTitle().contains(query)) {
                                MasterModel(
                                    file.hashCode().toLong(),
                                    EXTENSION_NAME,
                                    mp3file.id3v2Tag?.albumImage?.let { cache.cacheAlbumArt(it, mp3file.getTitle()) }
                                        .orEmpty(),
                                    mp3file.getTitle(),
                                    "Local mp3 file",
                                    file.absolutePath
                                ).let {
                                    foundItems.add(it)
                                }
                            }
                        } catch (e: InvalidDataException) {
                            Log.w("Familiar mp3", "${e.localizedMessage} in track ${file.name}", e)
                        }
                    }
                } else {
                    scanDirs(query, file, foundItems)
                }
        }
        foundItems
    }

    private fun Mp3File.getTitle(): String {
        return id3v1Tag?.title ?: id3v2Tag?.title ?: filename
    }

    private fun getMp3Description(mp3File: Mp3File): String {
        val wrapper = ID3Wrapper(mp3File.id3v1Tag, mp3File.id3v2Tag)
        return """
            Track: ${wrapper.track}
            Artist: ${wrapper.artist}
            Title: ${wrapper.title}
            Album: ${wrapper.album}
            Year: ${wrapper.year}
            Genre: ${wrapper.genreDescription}
            Comment: ${wrapper.comment}
        """.trimIndent()
    }
}
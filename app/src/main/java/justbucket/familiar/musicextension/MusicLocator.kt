package justbucket.familiar.musicextension

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import justbucket.familiar.extension.ExtensionLocator
import justbucket.familiar.extension.model.DetailModel
import justbucket.familiar.extension.model.MasterModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagTextField
import java.io.File
import java.util.concurrent.Executors
import kotlin.coroutines.coroutineContext

class MusicLocator : ExtensionLocator(EXTENSION_NAME) {

    private val mutex = Mutex()
    private val dispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

    override suspend fun getDetailsForSearch(masterModel: MasterModel): DetailModel {
        val audioFile = AudioFileIO.read(File(masterModel.detailViewLink))
        return DetailModel(
            EXTENSION_NAME,
            audioFile.getTitle(),
            masterModel.imageLink,
            getMp3Description(audioFile)
        )
    }

    override suspend fun getMasterForSearch(query: String): Set<MasterModel> {
        requestReadPermission()
        val modelSet = mutableSetOf<MasterModel>()
        withContext(coroutineContext) {
            scanDirs(query, getRootFile(), modelSet)
        }.join()
        return modelSet
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

    private fun CoroutineScope.scanDirs(
        query: String,
        rootFile: File,
        foundItems: MutableSet<MasterModel>
    ): Job = launch(dispatcher) {
        rootFile.listFiles().forEach { file ->
            if (!file.isDirectory) {
                try {
                    val audioFile = AudioFileIO.read(file)
                    if (file.absolutePath.contains(query, ignoreCase = true)
                        || audioFile.getTitle().contains(query, ignoreCase = true)
                    ) {
                        MasterModel(
                            file.hashCode().toLong(),
                            EXTENSION_NAME,
                            audioFile.getAlbumArtLink(file),
                            audioFile.getTitle(),
                            "Local mp3 file",
                            file.absolutePath
                        ).let {
                            mutex.withLock {
                                foundItems.add(it)
                            }
                        }
                    }
                } catch (throwable: Throwable) {
                    when (throwable) {
                        is CannotReadException -> {
                            Log.w("Familiar mp3", "${throwable.localizedMessage} in track ${file.name}")
                        }
                        is InvalidAudioFrameException -> {
                            Log.w("Familiar mp3", "${throwable.localizedMessage} in track ${file.name}")
                        }
                        else -> throw throwable
                    }
                }
            } else {
                scanDirs(query, file, foundItems)
            }
        }
    }

    private fun AudioFile.getTitle(): String {
        return (tag?.getFirstField(FieldKey.TITLE) as? TagTextField)?.content ?: file.nameWithoutExtension
    }

    private fun getMp3Description(audioFile: AudioFile): String {
        if (audioFile.tag == null) {
            return ""
        }
        return StringBuilder()
            .appendTag("Track ", FieldKey.TRACK, audioFile, false)
            .appendTag("Artist ", FieldKey.ARTIST, audioFile)
            .appendTag("Title ", FieldKey.TITLE, audioFile)
            .appendTag("Album ", FieldKey.ALBUM, audioFile)
            .appendTag("Year ", FieldKey.YEAR, audioFile)
            .appendTag("Genre ", FieldKey.GENRE, audioFile)
            .appendTag("Comment ", FieldKey.COMMENT, audioFile)
            .toString()
    }

    private fun StringBuilder.appendTag(
        prefix: String,
        key: FieldKey,
        audioFile: AudioFile,
        addNewLine: Boolean = true
    ): StringBuilder = apply {
        (audioFile.tag.getFirstField(key) as? TagTextField)?.let {
            if (addNewLine) {
                append('\n')
            }
            append("$prefix ${it.content}")
        }
    }

    private suspend fun AudioFile.getAlbumArtLink(file: File): String {
        val embeddedArt = tag?.firstArtwork
        val folderArt =
            file.parentFile?.listFiles()?.find { it.name.equals(COVER_FILE_NAME, ignoreCase = true) }?.absolutePath
        return folderArt ?: embeddedArt?.let {
            AlbumArtCache.getInstance(context).saveAlbumArtToCache(it.binaryData, file.hashCode().toString())
        } ?: ""
    }

    companion object {
        private const val COVER_FILE_NAME = "cover.jpg"
    }
}
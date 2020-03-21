package justbucket.familiar.extension.image

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import justbucket.familiar.extension.ExtensionLocator
import justbucket.familiar.extension.model.MasterModel
import java.io.File

/**
 * @author JustBucket on 09-Feb-2020
 */
class ImageLocator : ExtensionLocator(EXTENSION_NAME) {

    override suspend fun getMasterForSearch(query: String): Set<MasterModel> = with(context as Activity) {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            emptySet()
        } else {
            loadFiles(query)
        }
    }

    private suspend fun loadFiles(query: String): Set<MasterModel> {
        val filePaths = scanDirs(getRootFile(), query, mutableListOf())
        return filePaths.map {
            MasterModel(
                id = it.hashCode().toLong(),
                extensionName = extensionName,
                title = it.nameWithoutExtension,
                description = "Local image file",
                imageLink = it.absolutePath
            )
        }.toHashSet()
    }

    private fun getRootFile(): File {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return requireNotNull(context.getExternalFilesDir(null)?.parentFile?.parentFile?.parentFile?.parentFile)
        } else {
            Environment.getExternalStorageDirectory()
        }
    }
}
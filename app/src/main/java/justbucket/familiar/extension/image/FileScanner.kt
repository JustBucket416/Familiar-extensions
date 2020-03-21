package justbucket.familiar.extension.image

import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.coroutineContext

/**
 * @author JustBucket on 09-Feb-2020
 */
suspend fun scanDirs(
    rootDir: File,
    query: String,
    paths: MutableList<File> = mutableListOf()
): List<File> = withContext(coroutineContext) {
    rootDir.listFiles().forEach {
        if (!it.isDirectory) {
            if ((it.extension == "jpg" || it.extension == "png") && it.nameWithoutExtension.contains(
                    query,
                    ignoreCase = true
                )
            ) {
                paths.add(it)
            }
        } else {
            scanDirs(it, query, paths)
        }
    }
    paths
}
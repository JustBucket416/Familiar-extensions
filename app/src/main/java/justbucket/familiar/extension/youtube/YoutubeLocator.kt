package justbucket.familiar.extension.youtube

import com.google.gson.GsonBuilder
import justbucket.familiar.extension.ExtensionLocator
import justbucket.familiar.extension.model.DetailModel
import justbucket.familiar.extension.model.MasterModel
import justbucket.familiar.extension.youtube.retrofit.BASE_URL
import justbucket.familiar.extension.youtube.retrofit.YoutubeAPI
import justbucket.familiar.extension.youtube.retrofit.models.Item
import kotlinx.coroutines.CoroutineScope
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class YoutubeLocator : ExtensionLocator(EXTENSION_NAME) {

    private val youtubeAPI = createYoutubeApi()

    override suspend fun getDetailsForSearch(masterModel: MasterModel): DetailModel {
        return DetailModel(
            EXTENSION_NAME,
            masterModel.title,
            masterModel.detailViewLink.orEmpty(),
            "LUL"
        )
    }

    override suspend fun getMasterForSearch(query: String): Set<MasterModel> {
        return findVideos(query).toSet()
    }

    private fun findVideos(query: String): List<MasterModel> {
        val response = youtubeAPI.findVideos(query = query).execute()
        if (response.isSuccessful.not()) {
            throw IllegalArgumentException("failed to find videos by query $query")
        }
        val items = requireNotNull(response.body()).items
        return items.map { item ->
            val thumbPath: String = item.getThumbPath()
            val id = item.id.videoId
            val detailLink = "https://www.youtube.com/watch?v=$id"
            val title = item.snippet.title
            MasterModel(
                id.hashCode().toLong(),
                EXTENSION_NAME,
                thumbPath,
                title,
                "Youtube video",
                //detailLink
                thumbPath
            )
        }
    }

    private fun loadVideo(link: String): MasterModel {
        val response = youtubeAPI.getVideo(id = link).execute()
        if (response.isSuccessful.not()) {
            throw IllegalArgumentException("failed to load video with link $link")
        }
        val item = requireNotNull(response.body()).items[0]
        val thumbPath: String = item.getThumbPath()
        val detailLink = "https://www.youtube.com/watch?v=${item.id}"
        val title = item.snippet.title
        return MasterModel(
            item.id.hashCode().toLong(),
            EXTENSION_NAME,
            thumbPath,
            title,
            "YouTube video",
            detailLink
        )
    }

    private fun CoroutineScope.loadPlaylist(
        link: String,
        modelList: MutableList<MasterModel>,
        pageToken: String? = null
    ): List<MasterModel> {
        val response = youtubeAPI.getPlaylist(playlist = link, pageToken = pageToken).execute()
        if (response.isSuccessful.not()) {
            throw IllegalArgumentException("Could not load playlist with link $link")
        }
        requireNotNull(response.body()?.items).forEach { item ->
            val thumbPath: String = item.getThumbPath()
            val id = requireNotNull(item.snippet.resourceId).videoId
            val detailLink = "https://www.youtube.com/watch?v=$id"
            val title = item.snippet.title
            MasterModel(
                id.hashCode().toLong(),
                EXTENSION_NAME,
                thumbPath,
                title,
                "Youtube video",
                detailLink
            ).let { modelList.add(it) }
        }
        val nextPageToken = response.body()?.nextPageToken
        return if (nextPageToken != null) {
            loadPlaylist(link, modelList, nextPageToken)
        } else {
            modelList
        }
    }

    private fun createYoutubeApi(): YoutubeAPI {
        val gson = GsonBuilder()
            .setLenient()
            .serializeNulls()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(YoutubeAPI::class.java)
    }

    private fun Item.getThumbPath(): String {
        return snippet.thumbnails.maxres?.url
            ?: snippet.thumbnails.standard?.url
            ?: snippet.thumbnails.medium?.url
            ?: snippet.thumbnails.default?.url!!
    }
}
package justbucket.familiar.extension.youtube.retrofit

import justbucket.familiar.extension.youtube.TOKEN
import justbucket.familiar.extension.youtube.retrofit.models.Root
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * A [Retrofit] call interface
 */
interface YoutubeAPI {

    /**
     * Performs an API request to get playlist videos' details
     *
     * @param url - request url
     * @param part - specifies which part of videos' details we want
     * @param results - specifies how much videos we want to get from playlist
     * @param apiKey - an API key for the request
     *
     * @return a Gson skeleton parameterized [Call] instance
     */
    @GET
    fun getPlaylist(@Url url: String = PLAYLIST_URL,
                    @Query("part") part: String = PART,
                    @Query("maxResults") results: Int = MAX_RESULTS,
                    @Query("pageToken") pageToken: String?,
                    @Query("playlistId") playlist: String,
                    @Query("key") apiKey: String = TOKEN): Call<Root>

    /**
     * Performs an API request to get video details
     *
     * @param url - request url
     * @param part - specifies which part of videos' details we want
     * @param id - specifies video id
     * @param apiKey - an API key for the request
     *
     * @return a Gson skeleton parameterized [Call] instance
     */
    @GET
    fun getVideo(@Url url: String = VIDEO_URL,
                 @Query("part") part: String = PART,
                 @Query("id") id: String,
                 @Query("key") apiKey: String = TOKEN): Call<Root>

    @GET
    fun findVideos(@Url url: String = SEARCH_URL,
                 @Query("part") part: String = PART,
                 @Query("q") query: String,
                 @Query("maxResults") maxResults: Int = MAX_RESULTS,
                 @Query("order") order: String = ORDER,
                 @Query("type") type: String = VIDEO_TYPE,
                 @Query("safeSearch") safeSearch: String = SAFE_SEARCH_TYPE,
                 @Query("key") apiKey: String = TOKEN): Call<Root>
}
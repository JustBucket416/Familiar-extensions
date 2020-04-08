package justbucket.familiar.extension.youtube.retrofit.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Root(
    @SerializedName("nextPageToken")
    @Expose
    var nextPageToken: String? = null,
    @SerializedName("items")
    @Expose
    var items: List<Item>
)

data class Item(
    @SerializedName("snippet")
    @Expose
    var snippet: Snippet,
    @SerializedName("id")
    @Expose
    var id: Id
)

data class Id (
    @SerializedName("kind")
    @Expose
    var kind: String,
    @SerializedName("videoId")
    @Expose
    var videoId: String
)

data class ResourceId(
    @SerializedName("videoId")
    @Expose
    var videoId: String
)

data class Snippet(
    @SerializedName("title")
    @Expose
    var title: String,
    @SerializedName("thumbnails")
    @Expose
    var thumbnails: Thumbnails,
    @SerializedName("resourceId")
    @Expose
    var resourceId: ResourceId? = null
)

data class Thumbnails(
    @SerializedName("default")
    @Expose
    var default: Default? = null,
    @SerializedName("medium")
    @Expose
    var medium: Medium? = null,
    @SerializedName("high")
    @Expose
    var high: High? = null,
    @SerializedName("standard")
    @Expose
    var standard: Standard? = null,
    @SerializedName("maxres")
    @Expose
    var maxres: Maxres? = null
)

data class Default(
    @SerializedName("url")
    @Expose
    var url: String
)

data class Medium(
    @SerializedName("url")
    @Expose
    var url: String
)

data class High(
    @SerializedName("url")
    @Expose
    var url: String
)

data class Standard(
    @SerializedName("url")
    @Expose
    var url: String
)

data class Maxres(
    @SerializedName("url")
    @Expose
    var url: String
)


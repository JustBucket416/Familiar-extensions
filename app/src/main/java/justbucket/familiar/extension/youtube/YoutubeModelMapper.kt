package justbucket.familiar.extension.youtube

import justbucket.familiar.extension.ExtensionModelMapper
import justbucket.familiar.extension.model.DetailModel
import justbucket.familiar.extension.model.MasterModel
import org.json.JSONObject

class YoutubeModelMapper : ExtensionModelMapper(EXTENSION_NAME) {
    override suspend fun mapDetailToLocal(detailModel: DetailModel): String? {
        val jsonObject = JSONObject()
        jsonObject.put(TITLE_KEY, detailModel.title)
        jsonObject.put(EXTENSION_NAME, EXTENSION_NAME_KEY)
        jsonObject.put(IMAGE_KEY, detailModel.imageLink)
        jsonObject.put(DESCRIPTION_KEY, detailModel.description)
        return jsonObject.toString()
    }

    override suspend fun mapLocalToDetail(jsonString: String): DetailModel? {
        val jsonObject = JSONObject(jsonString)
        return DetailModel(
            EXTENSION_NAME,
            jsonObject.getString(TITLE_KEY),
            jsonObject.getString(IMAGE_KEY),
            jsonObject.getString(DESCRIPTION_KEY)
        )
    }

    override suspend fun mapLocalToMaster(id: Long, jsonString: String): MasterModel? {
        val jsonObject = JSONObject(jsonString)
        return MasterModel(
            id,
            EXTENSION_NAME,
            jsonObject.getString(IMAGE_KEY),
            jsonObject.getString(TITLE_KEY),
            jsonObject.getString(DESCRIPTION_KEY)
        )
    }

    override suspend fun mapMasterToLocal(masterModel: MasterModel): String? {
        val jsonObject = JSONObject()
        jsonObject.put(TITLE_KEY, masterModel.title)
        jsonObject.put(EXTENSION_NAME, EXTENSION_NAME_KEY)
        jsonObject.put(IMAGE_KEY, masterModel.detailViewLink)
        jsonObject.put(DESCRIPTION_KEY, masterModel.description)
        return jsonObject.toString()
    }

    companion object {
        private const val TITLE_KEY = "title_key"
        private const val EXTENSION_NAME_KEY = "extension_key"
        private const val IMAGE_KEY = "image_key"
        private const val DESCRIPTION_KEY = "description_key"
    }
}
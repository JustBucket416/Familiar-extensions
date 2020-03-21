package justbucket.familiar.extension.image

import justbucket.familiar.extension.ExtensionModelMapper
import justbucket.familiar.extension.model.MasterModel
import org.json.JSONObject

/**
 * @author JustBucket on 09-Feb-2020
 */
class ImageModelMapper : ExtensionModelMapper(EXTENSION_NAME) {

    override fun mapLocalToMaster(id: Long, jsonString: String): MasterModel? {
        return JSONObject(jsonString).let { jsonObject ->
            MasterModel(
                id = id,
                extensionName = jsonObject.getString(EXTENSION_NAME_KEY),
                title = jsonObject.getString(TITLE_KEY),
                imageLink = jsonObject.getString(IMAGE_LINK_KEY),
                description = jsonObject.getString(DESCRIPTION_KEY)
            )
        }
    }

    override fun mapMasterToLocal(masterModel: MasterModel): String? {
        return JSONObject().apply {
            put(ID_KEY, masterModel.id)
            put(EXTENSION_NAME_KEY, masterModel.extensionName)
            put(IMAGE_LINK_KEY, masterModel.imageLink)
            put(TITLE_KEY, masterModel.title)
            put(DESCRIPTION_KEY, masterModel.description)
        }.toString(4)
    }

    private companion object {
        private const val ID_KEY = "id_key"
        private const val EXTENSION_NAME_KEY = "extension_name_key"
        private const val TITLE_KEY = "title_key"
        private const val IMAGE_LINK_KEY = "image_link_key"
        private const val DESCRIPTION_KEY = "description_key"
    }
}


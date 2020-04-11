package justbucket.familiar.extension.youtube

import androidx.fragment.app.Fragment
import justbucket.familiar.extension.ExtensionConfigurator
import justbucket.familiar.extension.model.MasterModel

class YoutubeConfigurator : ExtensionConfigurator(EXTENSION_NAME) {

    override fun configureDetailModel(masterModel: MasterModel): Fragment? {
        return YoutubeFragment.newInstance(masterModel, EXTENSION_NAME)
    }
}
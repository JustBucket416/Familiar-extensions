package justbucket.familiar.musicextension

import justbucket.familiar.extension.ExtensionConfigurator
import justbucket.familiar.extension.fragment.DetailFragment
import justbucket.familiar.extension.model.MasterModel

class MusicConfigurator : ExtensionConfigurator(EXTENSION_NAME) {

    override fun configureDetailModel(masterModel: MasterModel): DetailFragment? {
        return MusicFragment.newInstance(masterModel, EXTENSION_NAME)
    }
}
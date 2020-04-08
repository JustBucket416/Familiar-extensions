package justbucket.familiar.musicextension

import androidx.fragment.app.Fragment
import justbucket.familiar.extension.ExtensionConfigurator
import justbucket.familiar.extension.model.MasterModel

class MusicConfigurator : ExtensionConfigurator(EXTENSION_NAME) {

    override fun configureDetailModel(masterModel: MasterModel): Fragment? {
        return MusicFragment.newInstance(masterModel, EXTENSION_NAME)
    }
}
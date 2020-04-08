package justbucket.familiar.musicextension

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import justbucket.familiar.extension.fragment.DetailFragment
import justbucket.familiar.extension.model.DetailModel
import justbucket.familiar.extension.model.MasterModel
import justbucket.familiar.extension.resource.Resource

class MusicFragment : DetailFragment() {

    private lateinit var masterModel: MasterModel
    private lateinit var extensionName: String
    private lateinit var liveData: LiveData<Resource<DetailModel>>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        masterModel = requireNotNull(arguments?.getParcelable(MASTER_MODEL_KEY))
        extensionName = requireNotNull(arguments?.getString(EXTENSION_NAME_KEY))
        liveData = fragmentProvider.loadDetailModel(masterModel)
    }

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return fragmentProvider.getThemedLayoutInflater(extensionName)
            .inflate(R.layout.music_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        liveData.observe(this, Observer {
            if (it is Resource.Success) {
                val imageView = view.findViewById<ImageView>(R.id.image_view)
                Glide.with(requireActivity()).load(it.data.imageLink).into(imageView)
                view.findViewById<TextView>(R.id.title_view).text = it.data.title
                view.findViewById<TextView>(R.id.description_view).text = it.data.description
            }
        })
    }

    companion object {
        private const val MASTER_MODEL_KEY = "masterModelKey"
        private const val EXTENSION_NAME_KEY = "extensionNameKey"

        fun newInstance(masterModel: MasterModel, extensionName: String): MusicFragment {
            return MusicFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(MASTER_MODEL_KEY, masterModel)
                    putString(EXTENSION_NAME_KEY, extensionName)
                }
            }
        }
    }
}
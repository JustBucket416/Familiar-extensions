package justbucket.familiar.musicextension

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import justbucket.familiar.extension.ExtensionConfigurator
import justbucket.familiar.extension.model.DetailModel

class MusicConfigurator : ExtensionConfigurator(EXTENSION_NAME) {

    override fun configureDetailModel(): ((ViewGroup, DetailModel) -> Unit)? =
        { viewGroup: ViewGroup, model: DetailModel ->
            val resources =
                viewGroup.context.packageManager.getResourcesForApplication("justbucket.familiar.musicextension")
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(resources.getLayout(R.layout.music_layout), viewGroup, false)
            val imageView = view.findViewById<ImageView>(R.id.image_view)
            Glide.with(viewGroup).load(model.imageLink).into(imageView)
            view.findViewById<TextView>(R.id.title).text = model.title
            view.findViewById<TextView>(R.id.description).text = model.description
            viewGroup.addView(view)
        }
}
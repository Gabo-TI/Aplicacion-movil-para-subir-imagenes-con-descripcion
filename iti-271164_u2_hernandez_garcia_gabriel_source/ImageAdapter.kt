package upvictoria.pm_ene_abr_2024.iti_271164.pi1u2.hernandez_garcia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ImageAdapter(
    private val images: List<Image>,
    private val onItemClick: (Image) -> Unit,
    private val onDeleteClick: (Image) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
        holder.bind(image)
        holder.itemView.setOnClickListener { onItemClick(image) }
        holder.deleteButton.setOnClickListener { onDeleteClick(image) }
    }

    override fun getItemCount(): Int = images.size

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image_view)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.description_text_view)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

        fun bind(image: Image) {
            imageView.setImageURI(image.uri)
            descriptionTextView.text = image.description
        }
    }
}

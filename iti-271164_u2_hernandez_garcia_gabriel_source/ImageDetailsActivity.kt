package upvictoria.pm_ene_abr_2024.iti_271164.pi1u2.hernandez_garcia

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ImageDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_details)

        val imageView: ImageView = findViewById(R.id.imageView)
        val descriptionTextView: TextView = findViewById(R.id.descriptionTextView)

        val imageUri = intent.getStringExtra("imageUri")
        val description = intent.getStringExtra("description")

        imageView.setImageURI(Uri.parse(imageUri))
        descriptionTextView.text = description
    }
}

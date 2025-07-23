package upvictoria.pm_ene_abr_2024.iti_271164.pi1u2.hernandez_garcia

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var descriptionEditText: EditText
    private lateinit var saveImageButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private val images = mutableListOf<Image>()
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        saveImageButton = findViewById(R.id.saveImageButton)
        recyclerView = findViewById(R.id.recyclerView)

        // Configuración del RecyclerView con GridLayoutManager para mostrar en 3 columnas
        val layoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = layoutManager

        imageAdapter = ImageAdapter(images,
            onItemClick = { image -> showImageDetails(image) },
            onDeleteClick = { image -> deleteImage(image) }
        )
        recyclerView.adapter = imageAdapter

        // Cargar las imágenes y descripciones guardadas al iniciar la aplicación
        loadImagesFromStorage()

        val addImageButton: Button = findViewById(R.id.addImageButton)
        addImageButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        saveImageButton.setOnClickListener {
            saveImage()
        }
    }

    private val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickPhotoIntent ->
            pickPhotoIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                imageView.setImageURI(imageUri)
                currentImageUri = imageUri
            }
        }
    }

    private fun saveImage() {
        val description = descriptionEditText.text.toString()
        currentImageUri?.let { uri ->
            val image = Image(uri, description)
            images.add(image)
            saveImageToInternalStorage(uri, description) // Guardar imagen con su descripción
            imageAdapter.notifyDataSetChanged()
        }
    }

    private fun getImageUri(imageView: ImageView): Uri? {
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val uri = Uri.parse(MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Image", null))
            return uri
        }
        return null
    }

    private fun saveImageToInternalStorage(imageUri: Uri, description: String) {
        val inputStream = contentResolver.openInputStream(imageUri)
        val folder = getDir("images", Context.MODE_PRIVATE)
        val imageFile = File(folder, "${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(imageFile)
        inputStream?.copyTo(outputStream)
        outputStream.close()
        inputStream?.close()

        // Guardar descripción en un archivo de texto
        val descriptionFile = File(folder, "${imageFile.name}.txt")
        descriptionFile.writeText(description)
    }

    private fun loadImagesFromStorage() {
        val folder = getDir("images", Context.MODE_PRIVATE)
        folder.listFiles()?.forEach { imageFile ->
            val uri = Uri.fromFile(imageFile)
            val descriptionFile = File(folder, "${imageFile.name}.txt")
            val description = if (descriptionFile.exists() && descriptionFile.length() > 0) {
                descriptionFile.readText()
            } else {
                return@forEach // Omitir imágenes sin descripción válida
            }
            val image = Image(uri, description)
            images.add(image)
        }
        images.removeAll { image ->
            // Filtrar imágenes vacías
            image.uri == null || image.uri.toString().isEmpty()
        }
        imageAdapter.notifyDataSetChanged()
    }

    private fun showImageDetails(image: Image) {
        val intent = Intent(this, ImageDetailsActivity::class.java).apply {
            putExtra("imageUri", image.uri.toString())
            putExtra("description", image.description)
        }
        startActivity(intent)
    }

    private fun deleteImage(image: Image) {
        val folder = getDir("images", Context.MODE_PRIVATE)
        val imageFile = File(folder, "${image.uri.lastPathSegment}.jpg")
        val descriptionFile = File(folder, "${image.uri.lastPathSegment}.txt")

        if (imageFile.exists()) {
            imageFile.delete()
        }

        if (descriptionFile.exists()) {
            descriptionFile.delete()
        }

        images.remove(image)
        imageAdapter.notifyDataSetChanged()
    }
}

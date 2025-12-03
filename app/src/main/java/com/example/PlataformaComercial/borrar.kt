package com.example.trabajomarketplace

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.WriteMode
import com.dropbox.core.v2.sharing.CreateSharedLinkWithSettingsErrorException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

class borrar : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    // Constants
    // IMPORTANT: token removed for security. Replace at runtime via BuildConfig, gradle properties or env var.
    private val DROPBOX_ACCESS_TOKEN = "REDACTED_DROPBOX_TOKEN"

    // Initialize Dropbox client
    private val dbxClient = DbxClientV2(DbxRequestConfig("dropbox-sample"), DROPBOX_ACCESS_TOKEN)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_borrar)

        val btnSelectImage = findViewById<Button>(R.id.btnSelectImage)
        val btnUploadImage = findViewById<Button>(R.id.btnUploadImage)
        val listViewFiles = findViewById<ListView>(R.id.listViewFiles)
        val imageView = findViewById<ImageView>(R.id.imageView5
        )
        btnSelectImage.setOnClickListener {
            selectImageFromDevice()
        }

        listFilesAndGenerateLinks(listViewFiles)
        listViewFiles.setOnItemClickListener { _, _, position, _ ->
            val selectedLink = listViewFiles.adapter.getItem(position) as String
            loadImageFromLink(selectedLink, imageView)
        }

        btnUploadImage.setOnClickListener {
            selectedImageUri?.let {
                uploadImageToDropbox(it)
            } ?: run {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectImageFromDevice() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            Toast.makeText(this, "Image Selected: $selectedImageUri", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToDropbox(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val byteArrayOutputStream = ByteArrayOutputStream()
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArrayInputStream = ByteArrayInputStream(byteArrayOutputStream.toByteArray())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val metadata = dbxClient.files().uploadBuilder("/${System.currentTimeMillis()}.jpg")
                    .uploadAndFinish(byteArrayInputStream)
                runOnUiThread {
                    // Update UI after successful upload
                    Toast.makeText(this@borrar, "Upload successful: ${metadata.pathLower}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@borrar, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun listFilesAndGenerateLinks(listView: ListView) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = dbxClient.files().listFolder("")
                val files = result.entries.filterIsInstance<FileMetadata>()
                val fileLinks = mutableListOf<String>()

                files.forEach { file ->
                    try {
                        val sharedLinkMetadata = dbxClient.sharing().createSharedLinkWithSettings(file.pathLower)
                        val directLink = sharedLinkMetadata.url.replace("www.dropbox.com", "dl.dropboxusercontent.com").replace("?dl=0", "")
                        fileLinks.add(directLink)
                    } catch (e: CreateSharedLinkWithSettingsErrorException) {
                        // Handle case where shared link already exists
                        val sharedLinkMetadata = dbxClient.sharing().listSharedLinksBuilder()
                            .withPath(file.pathLower)
                            .withDirectOnly(true)
                            .start()
                            .links
                            .firstOrNull()

                        if (sharedLinkMetadata != null) {
                            val directLink = sharedLinkMetadata.url.replace("www.dropbox.com", "dl.dropboxusercontent.com").replace("?dl=0", "")
                            fileLinks.add(directLink)
                        }
                    }
                }

                runOnUiThread {
                    val adapter = ArrayAdapter(this@borrar, android.R.layout.simple_list_item_1, fileLinks)
                    listView.adapter = adapter
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@borrar, "Failed to list files: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun loadImageFromLink(url: String, imageView: ImageView) {
        Glide.with(this)
            .load(url)
            .into(imageView)
    }

}
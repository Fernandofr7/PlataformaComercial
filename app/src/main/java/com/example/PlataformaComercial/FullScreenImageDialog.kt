package com.example.trabajomarketplace

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide

class FullScreenImageDialog : DialogFragment() {

    companion object {
        private const val ARG_IMAGE_URI = "image_uri"

        fun newInstance(imageUri: String): FullScreenImageDialog {
            val args = Bundle()
            args.putString(ARG_IMAGE_URI, imageUri)
            val fragment = FullScreenImageDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        // Inflar el diseño del diálogo
        val contentView = LayoutInflater.from(requireContext()).inflate(R.layout.activity_full_screen_image_dialog, null)
        dialog.setContentView(contentView)

        // Encontrar la referencia al ImageView
        val imageView = contentView.findViewById<ImageView>(R.id.fullscreenImageView)

        // Obtener la URI de la imagen
        val imageUri = arguments?.getString(ARG_IMAGE_URI)

        // Cargar la imagen en el ImageView usando Glide
        if (!imageUri.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(imageUri)
                .placeholder(R.drawable.unnamed)  // Imagen por defecto
                .error(R.drawable.circulo)       // Imagen por defecto en caso de error
                .into(imageView)
        } else {
            // Si la URI de la imagen es nula o vacía, muestra la imagen por defecto
            imageView.setImageResource(R.drawable.userm)
        }

        return dialog
    }

}
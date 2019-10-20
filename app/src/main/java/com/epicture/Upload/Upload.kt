package com.epicture.Upload

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Half.toFloat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.epicture.Utils.AuthentificationImgur
import com.epicture.Utils.Utils
import com.example.epicture.R
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

/**
 * UploadFragment is the fragment which is rendered in the main activity
 * when clicking on the search button in the bottom bar
 * @param PersonnalApplication Our Application
 * @param PersonnalPackageManagerkey Our PackageManager
 * @param authentificationData class which contains the clientId and clientSecret
 */
class UploadFragment(private val PersonnalApplication: Application,
                     private val PersonnalPackageManagerkey: PackageManager,
                     private val authentificationData: AuthentificationImgur
) : Fragment() {

    private var imageData: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_upload, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titleEdit = view.findViewById<EditText>(R.id.Edit_Title)
        val descriptionEdit = view.findViewById<EditText>(R.id.Edit_Description)

        view.findViewById<ConstraintLayout>(R.id.pushButton).setOnClickListener {

            val animFadein = AnimationUtils.loadAnimation(it.context, R.anim.fade_in)

            it.startAnimation(animFadein)

            val body = JSONObject()
            body.put("image", imageData)
            body.put("title", titleEdit.text.toString())
            body.put("description", descriptionEdit.text.toString())
            body.put("type", "base64")

            val paramsPictures = HashMap<String, String>()
            paramsPictures["Authorization"] = "Bearer ${authentificationData.access_token}"
            paramsPictures["content-type"] = "application/json"

            Utils().getRequest(view.context, paramsPictures, "https://api.imgur.com/3/upload", {}, Request.Method.POST, body.toString(), "Image Uploaded!", "Image not Uploaded. Probably cause the size!")
        }


        view.findViewById<ConstraintLayout>(R.id.upload_Button).setOnClickListener {

            val animFadein = AnimationUtils.loadAnimation(it.context, R.anim.fade_in)

            it.startAnimation(animFadein)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(view.context, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED) {

                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_CODE)
                }
                else{
                    pickImageFromGallery()
                }
            }
            else{
                pickImageFromGallery()
            }
        }
    }
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery()
                } else {
                    Toast.makeText(view!!.context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    @Throws(IOException::class)
    fun modifyOrientation(bitmap: Bitmap, image_absolute_path: String?): Bitmap {
        val ei = ExifInterface(image_absolute_path)
        val orientation =
            ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> return rotate(bitmap, 90f)

            ExifInterface.ORIENTATION_ROTATE_180 -> return rotate(bitmap, 180f)

            ExifInterface.ORIENTATION_ROTATE_270 -> return rotate(bitmap, 270f)

            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> return flip(bitmap, true, false)

            ExifInterface.ORIENTATION_FLIP_VERTICAL -> return flip(bitmap, false, true)

            else -> return bitmap
        }
    }

    fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    @SuppressLint("HalfFloat")
    fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.preScale(if (horizontal) toFloat(-1) else toFloat(1), if (vertical) toFloat(-1) else toFloat(1))
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            val selectedfile: Uri? = data?.data

            var bm: Bitmap = MediaStore.Images.Media.getBitmap(view!!.context.contentResolver, selectedfile)
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = activity!!.contentResolver.query(selectedfile, null, null, null);
            cursor!!.moveToFirst()

            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
            val picturePath: String  = cursor.getString(columnIndex)
            cursor.close()

            val pictureFile = File(picturePath)

            bm = modifyOrientation(bm, pictureFile.absolutePath)
            val baos: ByteArrayOutputStream  = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            view!!.findViewById<ImageView>(R.id.imagePreview).apply {
                setImageBitmap(bm)
                visibility = View.VISIBLE
            }
            view!!.findViewById<ConstraintLayout>(R.id.pushButton).visibility = View.VISIBLE

            val b: ByteArray = baos.toByteArray()
            imageData = Base64.encodeToString(b, Base64.DEFAULT)
            Toast.makeText(view!!.context, "Image Saved.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private val IMAGE_PICK_CODE = 1000
        private val TAKE_PHOTO_REQUEST = 1000
        private val PERMISSION_CODE = 1001
        fun newInstance(PersonnalApplication: Application, PersonnalPackageManagerkey: PackageManager, authentificationData: AuthentificationImgur): UploadFragment = UploadFragment(PersonnalApplication, PersonnalPackageManagerkey, authentificationData)
    }
}
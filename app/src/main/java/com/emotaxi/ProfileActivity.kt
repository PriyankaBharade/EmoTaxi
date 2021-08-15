package com.emotaxi

import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.emotaxi.model.GetProfileModel
import com.emotaxi.model.TokenModel
import com.emotaxi.retrofit.BackEndApi
import com.emotaxi.retrofit.Constant
import com.emotaxi.retrofit.WebServiceClient
import com.emotaxi.widget.CustomDialogProgress
import com.emotaxi.widget.DataManager
import com.emotaxi.widget.FileUtils
import com.emotaxi.widget.SessionManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    var tv_title: TextView? = null
    var imageback: ImageView? = null
    var CAMER_PERMISSION = 11111
    var GALLERY_PERMISSION = 22222
    private val OPERATION_CAPTURE_PHOTO = 1
    private val OPERATION_CHOOSE_PHOTO = 2
    var Path = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        imageback = findViewById(R.id.imageback)
        tv_title = findViewById(R.id.tv_title)

        tv_title?.text = getString(R.string.profile)
        imageback?.setOnClickListener {
            finish()
        }
        profileImage?.setOnClickListener {
            getImageFromSelection()
        }
        btn_update.setOnClickListener {
            updateProfile()
        }

        if (DataManager.dataManager.getSignUpModel(this) != null) {
            getProfile()
        }
    }

    fun getImageFromSelection() {
        var permissions = arrayListOf<String>(android.Manifest.permission.CAMERA)
        val options =
            arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        var alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle("Choose your profile picture")
        alertDialog.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    capturePhoto()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.CAMERA),
                        CAMER_PERMISSION
                    )
                }
            } else if (options[item] == "Choose from Gallery") {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    openGallery()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        GALLERY_PERMISSION
                    )
                }
            } else {
                dialog.dismiss()
            }
        }
        alertDialog.show()
    }

    private fun capturePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, OPERATION_CAPTURE_PHOTO)
    }

    private fun openGallery() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, OPERATION_CHOOSE_PHOTO)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMER_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                capturePhoto()
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == GALLERY_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
                Toast.makeText(this, "Gallery Permission Granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            OPERATION_CAPTURE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    var photoPath = getPath(data)
                    Glide.with(this).load(getPath(data))
                        .into(profileImage)
                }
            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    handleImageOnKitkat(data)
                }
        }
    }

    private fun getPath(data: Intent?): String? {
        val bundle = data!!.extras
        val imagebitmap = bundle!!["data"] as Bitmap?
        val path =
            MediaStore.Images.Media.insertImage(contentResolver, imagebitmap, "Title", null)
        return FileUtils.getPath(this, Uri.parse(path))
    }

    @TargetApi(19)
    private fun handleImageOnKitkat(data: Intent?) {
        //   var imagePath: String? = null
        val uri = data!!.data
        //DocumentsContract defines the contract between a documents provider and the platform.
        if (DocumentsContract.isDocumentUri(this, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            if (uri != null) {
                if ("com.android.providers.media.documents" == uri.authority) {
                    val id = docId.split(":")[1]
                    val selsetion = MediaStore.Images.Media._ID + "=" + id
                    Path = getImagePath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        selsetion
                    )
                } else if ("com.android.providers.downloads.documents" == uri.authority) {
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse(
                            "content://downloads/public_downloads"
                        ), java.lang.Long.valueOf(docId)
                    )
                    Path = getImagePath(contentUri, null)
                }
            }
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            Path = getImagePath(uri, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            Path = uri.path!!
        }
        renderImage(Path)
    }

    private fun getImagePath(uri: Uri?, selection: String?): String {
        var path: String? = null
        val cursor = uri?.let { contentResolver.query(it, null, selection, null, null) }
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path!!
    }

    private fun renderImage(imagePath: String?) {
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            profileImage?.setImageBitmap(bitmap)
        } else {
            Toast.makeText(this, "ImagePath is null", Toast.LENGTH_SHORT)
        }
    }

    private fun getProfile() {
        var customDialogProgress = CustomDialogProgress(this)
        customDialogProgress?.show()
        var hashMap = HashMap<String, String>()
        hashMap.put("user_id", DataManager.dataManager.getSignUpModel(this).data[0].userId)
        var hashmapheader = HashMap<String, String>()
        hashmapheader["version"] = "1"
        hashmapheader["device_type"] = "android"
        WebServiceClient.client1.create(BackEndApi::class.java).getProfile(hashmapheader, hashMap)
            .enqueue(object : Callback<GetProfileModel> {
                override fun onFailure(call: Call<GetProfileModel>, t: Throwable) {
                    Log.e("Response", t.message.toString())
                    customDialogProgress?.dismiss()
                }

                override fun onResponse(call: Call<GetProfileModel>, response: Response<GetProfileModel>) {
                    Log.e("Response", response.message())
                    customDialogProgress?.dismiss()
                    var dataJson : String = Gson().toJson(response!!.body())
                    SessionManager.writeString(this@ProfileActivity,Constant.ProfileData,dataJson)
                    setDataOnViews(response.body())

                }

            })
    }

    private fun setDataOnViews(body: GetProfileModel?) {
        if(body!=null){
            Glide.with(this).load(body!!.data[0].profileImage).into(profileImage)
            input_name.setText(body!!.data[0].username)
            input_phone.setText(body!!.data[0].mobileNo)
            input_email.setText(body!!.data[0].email)
        }
    }

    fun updateProfile() {
        var customDialogProgress = CustomDialogProgress(this)
        customDialogProgress.show()
        var partimage: MultipartBody.Part =
            BackEndApi.prepareFilePart(getApplication(), "profile_image", Path);
        val mediaType = (MediaType.parse("text/plain"))
        var user_id_re: RequestBody = RequestBody.create(mediaType, DataManager.dataManager.getSignUpModel(this).data[0].userId)
        var full_name_re: RequestBody = RequestBody.create(mediaType, input_name.text.toString())
        var email_re: RequestBody = RequestBody.create(mediaType, input_email.text.toString())
        var version: RequestBody = RequestBody.create(mediaType, "1")
        var device_type: RequestBody = RequestBody.create(mediaType, "android")
        WebServiceClient?.client1?.create(BackEndApi::class.java)
            .profileUpdate(partimage, user_id_re!!, full_name_re, email_re, version,device_type)
            .enqueue(object : Callback<TokenModel> {
                override fun onFailure(call: Call<TokenModel>?, t: Throwable?) {
                    customDialogProgress.dismiss()
                    Toast.makeText(this@ProfileActivity!!, t?.message, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<TokenModel>?,
                    response: Response<TokenModel>?
                ) {
                    customDialogProgress.dismiss()
                    getProfile()

                }

            })
    }

}
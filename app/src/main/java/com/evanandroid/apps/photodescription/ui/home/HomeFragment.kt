package com.evanandroid.apps.photodescription.ui.home

import android.Manifest.permission
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Camera
import android.hardware.Camera.open
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.DragAndDropPermissions
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.evanandroid.apps.photodescription.R
import kotlinx.android.synthetic.main.fragment_home.*

import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import com.evanandroid.apps.photodescription.ui.gallery.ContentList
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.FileOutputStream
import java.nio.channels.AsynchronousFileChannel.open
import java.nio.channels.AsynchronousServerSocketChannel.open
import java.nio.channels.AsynchronousSocketChannel.open
import java.nio.channels.DatagramChannel.open
import java.nio.channels.Pipe.open
import java.nio.channels.ServerSocketChannel.open
import java.nio.channels.SocketChannel.open
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest
import com.evanandroid.apps.photodescription.Main as Main1

class HomeFragment : Fragment() {
    var storage : FirebaseStorage? = null
    val FLAG_REQ_STORAGE = 102
    val FLAG_REQ_CAMERA = 101
    val FLAG_PERM_STORAGE = 99
    val FLAG_PERM_CAMERA = 98
    val STORAGE_PERMISSION =
        arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE)
    val CAMERA_PERMISSION = arrayOf(permission.CAMERA)
    var cbut: Button? = null
    var gbut: Button? = null
    var photoUri : Uri? = null
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null

    private lateinit var homeViewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val cambutton = view.findViewById<Button>(R.id.buttonCammera)
        cbut = cambutton
        val readbutton = view.findViewById<Button>(R.id.buttonGallery)
        gbut = readbutton


        if (!checkPermission(STORAGE_PERMISSION, FLAG_PERM_STORAGE)) {
                cambutton.setOnClickListener {
                    openCammera()
                }
                readbutton.setOnClickListener {
                    openGallery()
                }
        }


        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val cam : Button = root.findViewById(R.id.buttonCammera)
        val gal : Button = root.findViewById(R.id.buttonGallery)
        val upb : Button = root. findViewById(R.id.uploadBut)
        cam.setOnClickListener {
            openCammera()

        }
        gal.setOnClickListener {
            openGallery()
        }

        upb.setOnClickListener {
            upLoad()

        }
        return root
    }

    fun upLoad(){
        //파일 이름의 중복 생성을 막는 부분
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFireName = "IMAGE" + timestamp + " .png"

        var storageRef = storage?.reference?.child("imagestory")?.child(imageFireName)

        storageRef?.putFile(photoUri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot>->
            return@continueWithTask  storageRef.downloadUrl
        }?.addOnSuccessListener { uri ->
            var content = ContentList()
            content.imageUrl = uri.toString()
            //현재 uid를 받는 코드
            content.uid = auth?.currentUser?.uid
            //userId를 받아오는 코드
            content.userId = auth?.currentUser?.email
            //설명을 넣어주는 코드
            content.explation = editDes.text.toString()
            //시간을 받아오는 코드
            content.time = System.currentTimeMillis()

            firestore?.collection("images")?.document()?.set(content)

            Activity().setResult(Activity.RESULT_OK)
            Activity().finish()
        }




        /*storageRef?.putFile(photoUri!!)?.addOnCanceledListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                //업로드 성공시 데이터를 넘겨 받는 코드
                var content = ContentList()
                content.imageUrl = uri.toString()
                //현재 uid를 받는 코드
                content.uid = auth?.currentUser?.uid
                //userId를 받아오는 코드
                content.userId = auth?.currentUser?.email
                //설명을 넣어주는 코드
                content.explation = editDes.text.toString()
                //시간을 받아오는 코드
                content.time = System.currentTimeMillis()

                firestore?.collection("images")?.document()?.set(content)

            }
        }*/
    }

    //권한 확인 함수
    fun checkPermission(permissions: Array<out String>, flag: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (context?.let {
                        ContextCompat.checkSelfPermission(
                            it,
                            permission
                        )
                    } != PackageManager.PERMISSION_GRANTED) {
                    activity?.let { ActivityCompat.requestPermissions(it, permissions, flag) }
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            FLAG_PERM_CAMERA -> {  //카메라 권한 승인 후에 처리되는 부분
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "카메라 권한을 승인해주세요.", Toast.LENGTH_LONG).show()
                        return
                    }
                }
                openCammera()
            }


            FLAG_PERM_STORAGE -> { //저장소 권한 승인 후에 처리되는 부분
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "저장소 권한을 승인해주세요.", Toast.LENGTH_LONG).show()
                        return
                    }
                }
                setViews()
            }

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FLAG_REQ_CAMERA -> {
                    if (data?.extras?.get("data") != null) {
                        val bitmap = data?.extras?.get("data") as Bitmap
                        //imagePreview.setImageBitmap(bitmap)
                        //val uri = saveImageFile(newFileName(), "image/jpg", bitmap)
                        photoUri = saveImageFile(newFileName(), "image/jpg", bitmap)
                        imagePreview.setImageURI(photoUri)
                    }
                }
                FLAG_REQ_STORAGE->{
                    //val uri = data?.data
                    photoUri = data?.data
                    imagePreview.setImageURI(photoUri)
                }
            }
        }
    }

    fun newFileName(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())
        return "$filename.jpg"
    }

    fun saveImageFile(filename: String, mimeType: String, bitmap: Bitmap): Uri? {
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val uri =
            context?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        try {
            if (uri != null) {
                var descriptor = context?.contentResolver?.openFileDescriptor(uri, "w")
                if (descriptor != null) {
                    val fos = FileOutputStream(descriptor.fileDescriptor)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.close()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        values.clear()
                        values.put(MediaStore.Images.Media.IS_PENDING, 0)
                        context?.contentResolver?.update(uri, values, null, null)
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("File", "error=${e.localizedMessage}")
        }
        return uri
    }


    fun setViews(){
        buttonCammera.setOnClickListener {
            openCammera()
        }
        buttonGallery.setOnClickListener {
            openGallery()
        }
    }

    fun openCammera() {
        if (checkPermission(CAMERA_PERMISSION, FLAG_PERM_CAMERA)) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            //startActivity(intent)
            startActivityForResult(intent,FLAG_REQ_CAMERA)
        }
    }

    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, FLAG_REQ_STORAGE)
    }

}
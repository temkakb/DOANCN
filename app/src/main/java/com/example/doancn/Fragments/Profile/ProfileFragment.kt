package com.example.doancn.Fragments.Profile

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.example.doancn.MainActivity
import com.example.doancn.Models.UserMe
import com.example.doancn.R
import com.example.doancn.R.id.*
import com.example.doancn.Retrofit.RetrofitManager
import com.example.doancn.Utilities.JwtManager
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.edit_profile.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream


class ProfileFragment : Fragment() {

    private var userme:UserMe? = null
    var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val main : MainActivity = activity as MainActivity
        userme = main.getMe()
        return inflater.inflate(R.layout.fragment_profile,container,false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(userme != null) {
            profile_name.text = userme!!.name
            profile_email.text = userme!!.account.email
            profile_dob.text = userme!!.dob
            profile_education_level.text = userme!!.educationLevel
            profile_curent_work_place.text = userme!!.currentWorkPlace
            profile_phone.text = userme!!.phoneNumber
            profile_adress.text = userme!!.address
            when (userme!!.gender.genderID) {
                1 -> {
                    profile_gender.text = "Khác"
                    if (userme!!.image == null)
                        profile_img.setImageResource(R.drawable.orther)
                }
                2 -> {
                    profile_gender.text = "Nữ"
                    if (userme!!.image == null)
                        profile_img.setImageResource(R.drawable.femal)
                }
                3 -> {
                    profile_gender.text = "Nam"
                    if (userme!!.image == null)
                        profile_img.setImageResource(R.drawable.man)
                }
            }
            JwtManager.apply {
                if (role == "TEACHER") {
                    tv_nlht.setText("Nơi làm việc hiện tại")
                    profile_role.text = "Giáo viên"
                } else if (role == "STUDENT") {
                    profile_role.text = "Học viên"
                }
            }
            if (userme!!.image != null) {
                val imgDecode: ByteArray = java.util.Base64.getDecoder().decode(userme!!.image)
                val bmp = BitmapFactory.decodeByteArray(imgDecode, 0, imgDecode.size)
                profile_img.setImageBitmap(bmp)
            }
            //Chọn hình để đổi hình ảnh
            profile_img.setOnClickListener {
                requestPermission()
            }

            progressDialog = ProgressDialog(context)
            progressDialog!!.setMessage("Loading...")
            progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)

            //Onclick Đổi mật khẩu
            cPass.setOnClickListener {
                val resetpasswordLayout: View = LayoutInflater.from(context)
                    .inflate(R.layout.change_pasword, null)
                val builder =
                    AlertDialog.Builder(requireContext())
                builder.setView(resetpasswordLayout)
                builder.setPositiveButton("Thay đổi")
                { dialogInterface: DialogInterface?, i1: Int -> progressDialog!!.show() }
                builder.setNegativeButton("cancel")
                { dialogInterface: DialogInterface, i1: Int -> dialogInterface.dismiss() }
                val dialog = builder.create()
                dialog.show()
            }

            //Onclick sửa thông tin cá nhân
            p_update.setOnClickListener {
                val editProfileLayout: View = LayoutInflater.from(context)
                    .inflate(R.layout.edit_profile, null)
                editProfileLayout.update_email.setText(userme!!!!.account.email)
                editProfileLayout.update_education_level.setText(userme!!!!.educationLevel)
                editProfileLayout.update_phone.setText(userme!!!!.phoneNumber)
                editProfileLayout.update_adress.setText(userme!!!!.address)
                editProfileLayout.update_curent_work_place.setText(userme!!!!.currentWorkPlace)
                val builder = AlertDialog.Builder(requireContext())
                builder.setView(editProfileLayout)
                builder.setPositiveButton("Thay đổi")
                { dialogInterface: DialogInterface?, i1: Int -> progressDialog!!.show() }
                builder.setNegativeButton("cancel")
                { dialogInterface: DialogInterface, i1: Int -> dialogInterface.dismiss() }
                val dialog = builder.create()
                dialog.show()
            }


        }
    }



    //Xin quyền truy cập máy ảnh và gallery
    private fun requestPermission() {
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                openImagePicker()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(
                    context,
                    "Đã từ chối cấp phép\n$deniedPermissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        TedPermission.create()
            .setPermissionListener(permissionlistener)
            .setDeniedMessage("Nếu bạn từ chối cấp phép thì bạn không thể sử dụng được dịch vụ\n\nHãy cấp phép bằng cách truy cập [Setting] > [Permission]")
            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()

    }

    //Chọn ảnh trong gallery
    private fun openImagePicker() {

        TedBottomPicker.with(context as FragmentActivity)
            .show {
                val sharedprefernces = this.context?.getSharedPreferences("tokenstorage", Context.MODE_PRIVATE)
                val token : String? = sharedprefernces?.getString("token",null)

                var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                    (context as FragmentActivity).contentResolver, it)
                profile_img.setImageBitmap(bitmap)

                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val image = stream.toByteArray()
                val imageBase64 : String = java.util.Base64.getEncoder().encodeToString(image)

                val map = HashMap<String,String>()
                map.put("imgBase64",imageBase64)
                Log.i("StringImg",imageBase64.length.toString())
                val call: Call<Unit> = RetrofitManager.updateuserapi.updateImgUser("Bearer $token",userme!!.userId,map)
                call.enqueue(object : Callback<Unit?> {
                    override fun onResponse(call: Call<Unit?>, response: Response<Unit?>) {
                        if (response.isSuccessful()) {
                            Toast.makeText(context, "Thay đổi ảnh thành công!", Toast.LENGTH_SHORT).show()
                            Log.i("PutApi","ok con dê")
                            val intent1 = Intent(context, MainActivity::class.java)
                            val intent = Intent(Intent.ACTION_PICK)

                            startActivity(intent1)

                        }
                    }

                    override fun onFailure(call: Call<Unit?>, t: Throwable) {
                        Log.e("ERROR: ", t.message!!)
                        Log.i("PutApi","ok ăn lol")
                    }
                })
            }

    }

    fun message(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    }
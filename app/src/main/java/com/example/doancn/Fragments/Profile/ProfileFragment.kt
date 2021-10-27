package com.example.doancn.Fragments.Profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.doancn.LoginRegisterActivity
import com.example.doancn.MainActivity
import com.example.doancn.Models.UserMe
import com.example.doancn.R
import com.example.doancn.Retrofit.RetrofitManager
import com.example.doancn.Utilities.JwtManager
import com.example.doancn.ViewModels.UserViewModel
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.change_pasword.view.*
import kotlinx.android.synthetic.main.edit_profile.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.nav_header.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap


class ProfileFragment : Fragment() {

    private var userme:UserMe? = null
    private lateinit var navController : NavController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController= requireParentFragment().findNavController()
        val main : MainActivity = activity as MainActivity
        val model : UserViewModel = ViewModelProvider(main)[UserViewModel::class.java]
        userme = model.user
        return inflater.inflate(R.layout.fragment_profile,container,false)
    }



    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val main : MainActivity = activity as MainActivity
        if(userme != null) {
            main.runOnUiThread {
                val header: View = main.nav_view.getHeaderView(0)
                header.user_name.text = userme!!.name
                header.user_email.text = userme!!.account.email
                if(userme!!.image != null){
                    val imgDecode: ByteArray = Base64.getDecoder().decode(userme!!.image)
                    val bmp = BitmapFactory.decodeByteArray(imgDecode, 0, imgDecode.size)
                    header.user_image.setImageBitmap(bmp)
                }
                else
                {
                    when(userme!!.gender.genderID)
                    {
                        1 -> { header.user_image.setImageResource(R.drawable.man) }
                        2 -> { header.user_image.setImageResource(R.drawable.femal) }
                        3 -> { header.user_image.setImageResource(R.drawable.orther) }
                    }
                }
            }
            profile_name.text = userme!!.name
            profile_email.text = userme!!.account.email
            profile_dob.text = userme!!.dob
            profile_education_level.text = userme!!.educationLevel
            profile_curent_work_place.text = userme!!.currentWorkPlace
            profile_phone.text = userme!!.phoneNumber
            profile_adress.text = userme!!.address
            when (userme!!.gender.genderID) {
                3 -> {
                    profile_gender.text = getString(R.string.Orther)
                    if (userme!!.image == null)
                        profile_img.setImageResource(R.drawable.orther)
                }
                2 -> {
                    profile_gender.text = getString(R.string.Female)
                    if (userme!!.image == null)
                        profile_img.setImageResource(R.drawable.femal)
                }
                1 -> {
                    profile_gender.text = getString(R.string.Male)
                    if (userme!!.image == null)
                        profile_img.setImageResource(R.drawable.man)
                }
            }
            JwtManager.apply {
                if (role == "TEACHER") {
                    tv_nlht.text = getString(R.string.currentWorkPlace)
                    profile_role.text = getString(R.string.teacher)
                    p_parent.visibility = View.GONE
                } else if (role == "STUDENT") {
                    profile_role.text = getString(R.string.student)
                }
            }
            if (userme!!.image != null) {
                val imgDecode: ByteArray = Base64.getDecoder().decode(userme!!.image)
                val bmp = BitmapFactory.decodeByteArray(imgDecode, 0, imgDecode.size)
                profile_img.setImageBitmap(bmp)
            }

            //Chọn hình để đổi hình ảnh
            profile_img.setOnClickListener {
                requestPermission()
            }

            //Onclick Đổi mật khẩu
            cPass.setOnClickListener {
                val resetpasswordLayout: View = LayoutInflater.from(context)
                    .inflate(R.layout.change_pasword, null)
                val builder =
                    AlertDialog.Builder(requireContext())
                builder.setView(resetpasswordLayout)
                builder.setPositiveButton("Thay đổi")
                { _: DialogInterface?, _: Int ->
                    val oldpass : String = resetpasswordLayout.cp_oldpass.text.toString().trim()
                    val newpass : String = resetpasswordLayout.cp_newpass.text.toString().trim()
                    val confirmpass : String = resetpasswordLayout.cp_confirmpass.text.toString()
                    val sharedprefernces = this.context?.getSharedPreferences("tokenstorage", Context.MODE_PRIVATE)
                    val token : String? = sharedprefernces?.getString("token",null)
                    val mapPass = HashMap<String,String>()
                    mapPass["oldpass"] = oldpass
                    mapPass["newpass"] = newpass
                    if(oldpass.isEmpty() || newpass.isEmpty() || confirmpass.isEmpty()){
                        message("Cập nhật thất bại do bỏ trống thông tin")
                    }else if(newpass.length<8){
                        message("Mật khẩu mới không được nhỏ hơn 8 ký tự")
                    }else if(newpass != confirmpass){
                        message("Mật khẩu không khớp")
                    }else{
                        val call: Call<ResponseBody> = RetrofitManager.userapi.updatePassUser("Bearer "+token,userme!!.userId,mapPass)
                        call.enqueue(object : Callback<ResponseBody?> {
                            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                                    val mesage : String = response.body()!!.string()
                                if(mesage == "success"){
                                    Toast.makeText(context, "Đổi mật khẩu thành công\nXin mời đăng nhập lại", Toast.LENGTH_LONG).show()
                                    val main : MainActivity = activity as MainActivity
                                    val intent = Intent(context, LoginRegisterActivity::class.java)
                                    startActivity(intent)
                                    main.finish()
                                } else{
                                    Toast.makeText(context, mesage, Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                                Log.e("ERROR: ", t.message!!)
                            }
                        })
                    }
                }
                builder.setNegativeButton("Hủy")
                { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
                val dialog = builder.create()
                dialog.show()
            }


            //Onclick sửa thông tin cá nhân
            p_update.setOnClickListener {

                val editProfileLayout: View = LayoutInflater.from(context)
                    .inflate(R.layout.edit_profile, null)
                val myAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    editProfileLayout.context,
                    android.R.layout.simple_list_item_1
                    ,resources.getStringArray(R.array.update_gender)
                )
                myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                editProfileLayout.update_gender.adapter = myAdapter
                editProfileLayout.update_gender.setSelection(
                    (editProfileLayout.update_gender.adapter as ArrayAdapter<String>).getPosition(
                        when(userme!!.gender.genderID){
                            1 -> "Nam"
                            2 -> "Nữ"
                            else -> "Khác"
                        }
                    ))
                editProfileLayout.update_name.setText(userme!!.name)
                editProfileLayout.update_dob.text = userme!!.dob
                editProfileLayout.update_education_level.setText(userme!!.educationLevel)
                editProfileLayout.update_phone.setText(userme!!.phoneNumber)
                editProfileLayout.update_adress.setText(userme!!.address)
                editProfileLayout.update_curent_work_place.setText(userme!!.currentWorkPlace)

                editProfileLayout.pickdob.setOnClickListener {
                    val c = Calendar.getInstance()
                    val year = c.get(Calendar.YEAR)
                    val month = c.get(Calendar.MONTH)
                    val day = c.get(Calendar.DAY_OF_MONTH)
                    val dpd = DatePickerDialog(
                        editProfileLayout.context,
                        { _, myear, monthOfYear, dayOfMonth ->
                            c.set(Calendar.YEAR,myear)
                            c.set(Calendar.MONTH,monthOfYear)
                            c.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                            val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
                            val date : String = dateFormatter.format(c.time)
                            editProfileLayout.update_dob.text = date
                        }, year, month, day
                    )
                    dpd.show()
                }

                val builder = AlertDialog.Builder(requireContext())
                builder.setView(editProfileLayout)

                builder.setPositiveButton("Thay đổi")
                { dialogInterface: DialogInterface?, i1: Int ->
                    val sharedprefernces = this.context?.getSharedPreferences("tokenstorage", Context.MODE_PRIVATE)
                    val token : String? = sharedprefernces?.getString("token",null)
                    val u_name : String = editProfileLayout.update_name.text.toString()
                    val u_dob : String = editProfileLayout.update_dob.text.toString()
                    val u_gender : String = editProfileLayout.update_gender.selectedItem.toString()
                    val u_education_level : String = editProfileLayout.update_education_level.text.toString()
                    val u_phone : String = editProfileLayout.update_phone.text.toString()
                    val u_adress : String = editProfileLayout.update_adress.text.toString()
                    val u_curent_work_place : String = editProfileLayout.update_curent_work_place.text.toString()
                    val validatePhone  = "^[+]?[0-9]{9,11}$"
                    val PATTERN: Pattern = Pattern.compile(validatePhone)
                    if (u_name.isEmpty() || u_dob.isEmpty() || u_gender.isEmpty()
                        ||u_education_level.isEmpty() || u_phone.isEmpty() || u_adress.isEmpty()
                        || u_curent_work_place.isEmpty()) {
                        message("Thay đổi thất bại do bỏ trống thông tin")
                    } else if(u_name.length > 26 ){
                        message("Thay đổi thất bại do tên quá 26 ký tự")
                    } else if(!PATTERN.matcher(u_phone).find()){
                        message("Thay đổi thất bại do không đúng định dạng số điện thoại")
                    }else{
                        userme!!.name = u_name
                        userme!!.dob = u_dob
                        userme!!.address = u_adress
                        userme!!.currentWorkPlace = u_curent_work_place
                        userme!!.phoneNumber = u_phone
                        userme!!.educationLevel = u_education_level
                        userme!!.address = u_adress
                        when (u_gender){
                            "Nam" -> {
                                userme!!.gender.genderID = 1
                                userme!!.gender.name = "MALE"
                            }
                            "Nữ" -> {
                                userme!!.gender.genderID = 2
                                userme!!.gender.name = "FEMALE"
                            }
                            "Khác" -> {
                                userme!!.gender.genderID = 3
                                userme!!.gender.name = "ORTHER"
                            }
                        }
                        val call: Call<Unit> = RetrofitManager.userapi.updateUser("Bearer $token"
                            ,userme!!.userId, userme!!)
                        call.enqueue(object : Callback<Unit?> {
                            override fun onResponse(call: Call<Unit?>, response: Response<Unit?>) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(context, "Thay đổi thành công!", Toast.LENGTH_SHORT).show()
                                    val main : MainActivity = activity as MainActivity
                                    val model : UserViewModel = ViewModelProvider(main)[UserViewModel::class.java]
                                    model.user = userme
                                    navController.popBackStack()
                                    navController.navigate(R.id.nav_profile)
                                }
                            }

                            override fun onFailure(call: Call<Unit?>, t: Throwable) {
                                Log.e("ERROR: ", t.message!!)
                            }
                        })
                    }
                }

                builder.setNegativeButton("cancel")
                { dialogInterface: DialogInterface, i1: Int -> dialogInterface.dismiss() }

                val dialog = builder.create()
                dialog.show()
            }

            p_parent.setOnClickListener {
                val main: MainActivity = activity as MainActivity
                val actionBar: ActionBar? = main.supportActionBar
                actionBar?.setDisplayShowHomeEnabled(true)
                actionBar?.setDisplayUseLogoEnabled(true)
                actionBar?.setLogo(R.drawable.ic_baseline_profile_ind_24)
                actionBar?.setTitle("Phụ huynh")
                navController.popBackStack()
                navController.navigate(R.id.nav_parentFragment)
            }
        }
    }



    //Xin quyền truy cập máy ảnh và gallery
    private fun requestPermission() {
        val permissionlistener: PermissionListener = object : PermissionListener {
            @RequiresApi(Build.VERSION_CODES.P)
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
    @RequiresApi(Build.VERSION_CODES.P)
    private fun openImagePicker() {

        TedBottomPicker.with(context as FragmentActivity)
            .show {
                val sharedprefernces = this.context?.getSharedPreferences("tokenstorage", Context.MODE_PRIVATE)
                val token : String? = sharedprefernces?.getString("token",null)

                val source = ImageDecoder.createSource((context as FragmentActivity).contentResolver,it)
                val bitmap = ImageDecoder.decodeBitmap(source)

                profile_img.setImageBitmap(bitmap)

                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val image = stream.toByteArray()
                val imageBase64 : String = Base64.getEncoder().encodeToString(image)

                val map = HashMap<String,String>()
                map["imgBase64"] = imageBase64
                val call: Call<Unit> = RetrofitManager.userapi.updateImgUser("Bearer $token",userme!!.userId,map)
                call.enqueue(object : Callback<Unit?> {
                    override fun onResponse(call: Call<Unit?>, response: Response<Unit?>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Thay đổi ảnh thành công!", Toast.LENGTH_SHORT).show()
                            val main : MainActivity = activity as MainActivity
                            val model : UserViewModel = ViewModelProvider(main)[UserViewModel::class.java]
                            model.user!!.image = imageBase64
                            navController.popBackStack()
                            navController.navigate(R.id.nav_profile)
                        }
                    }

                    override fun onFailure(call: Call<Unit?>, t: Throwable) {
                        Log.e("ERROR: ", t.message!!)
                    }
                })
            }

    }

    fun message(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}
package com.example.doancn.Fragments.Profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.doancn.DI.DataState
import com.example.doancn.LoginRegisterActivity
import com.example.doancn.MainActivity
import com.example.doancn.MainViewModel
import com.example.doancn.Models.UserMe
import com.example.doancn.R
import com.example.doancn.ViewModels.UserViewModel
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.change_pasword.view.*
import kotlinx.android.synthetic.main.edit_profile.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var userme: UserMe? = null
    private lateinit var navController: NavController

    private val profileViewModel: ProfileViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = requireParentFragment().findNavController()
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        getMyUser(mainViewModel.token.toString())

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    override fun onStart() {
        observeData()
        super.onStart()
    }


    @SuppressLint("SimpleDateFormat")
    private fun observeData() {
        val main : MainActivity = activity as MainActivity
        lifecycleScope.launchWhenCreated {
            profileViewModel.user.collect {
                when (it) {
                    is DataState.Loading -> {
                        requireView().process_profile.visibility = View.VISIBLE
                        requireView().profile_scollview.visibility = View.INVISIBLE
                    }
                    is DataState.Success -> {
                        requireView().process_profile.visibility = View.GONE
                        requireView().profile_scollview.visibility = View.VISIBLE
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
                            if(userme!!.educationLevel != null)
                            profile_education_level.text = userme!!.educationLevel
                            if(userme!!.currentWorkPlace != null)
                            profile_curent_work_place.text = userme!!.currentWorkPlace
                            if(userme!!.phoneNumber != null)
                            profile_phone.text = userme!!.phoneNumber
                            if(userme!!.address != null)
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
                            mainViewModel.role.apply {
                                if (this == "TEACHER") {
                                    tv_nlht.text = getString(R.string.currentWorkPlace)
                                    profile_role.text = getString(R.string.teacher)
                                    p_parent.visibility = View.GONE
                                } else if (this == "STUDENT") {
                                    profile_role.text = getString(R.string.student)
                                }
                            }
                            if (userme!!.image != null) {
                                val imgDecode: ByteArray = Base64.getDecoder().decode(userme!!.image)
                                val bmp = BitmapFactory.decodeByteArray(imgDecode, 0, imgDecode.size)

                                val ivWidth: Int = profile_img.width
                                val ivHeight: Int = profile_img.height

                                Log.i("ivWidth",ivWidth.toString())
                                Log.i("ivHeight",ivHeight.toString())

                                val newbitMap = Bitmap.createScaledBitmap(bmp, ivWidth, ivHeight, true)
                                profile_img.setImageBitmap(newbitMap)
                            }

                            //Ch???n h??nh ????? ?????i h??nh ???nh
                            profile_img.setOnClickListener {
                                requestPermission()
                            }

                            //Onclick ?????i m???t kh???u
                            cPass.setOnClickListener {
                                val resetpasswordLayout: View = LayoutInflater.from(context)
                                    .inflate(R.layout.change_pasword, null)
                                val builder =
                                    AlertDialog.Builder(requireContext())
                                builder.setView(resetpasswordLayout)
                                builder.setPositiveButton("Thay ?????i")
                                { _: DialogInterface?, _: Int ->
                                    val oldpass : String = resetpasswordLayout.cp_oldpass.text.toString().trim()
                                    val newpass : String = resetpasswordLayout.cp_newpass.text.toString().trim()
                                    val confirmpass : String = resetpasswordLayout.cp_confirmpass.text.toString()
                                    val mapPass = HashMap<String,String>()
                                    mapPass["oldpass"] = oldpass
                                    mapPass["newpass"] = newpass
                                    if(oldpass.isEmpty() || newpass.isEmpty() || confirmpass.isEmpty()){
                                        message("C???p nh???t th???t b???i do b??? tr???ng th??ng tin")
                                    }else if(newpass.length<8){
                                        message("M???t kh???u m???i kh??ng ???????c nh??? h??n 8 k?? t???")
                                    }else if(newpass != confirmpass){
                                        message("M???t kh???u kh??ng kh???p")
                                    }else{
                                        profileViewModel.updatePassUser(mainViewModel.token.toString(),mapPass)
                                    }
                                }
                                builder.setNegativeButton("H???y")
                                { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
                                val dialog = builder.create()
                                dialog.show()
                            }


                            //Onclick s???a th??ng tin c?? nh??n
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
                                            2 -> "N???"
                                            else -> "Kh??c"
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

                                builder.setPositiveButton("Thay ?????i")
                                { _: DialogInterface?, _: Int ->
                                    val uName : String = editProfileLayout.update_name.text.toString()
                                    val uDob : String = editProfileLayout.update_dob.text.toString()
                                    val uGender : String = editProfileLayout.update_gender.selectedItem.toString()
                                    val uEducationLevel : String = editProfileLayout.update_education_level.text.toString()
                                    val uPhone : String = editProfileLayout.update_phone.text.toString()
                                    val uAdress : String = editProfileLayout.update_adress.text.toString()
                                    val uCurentWorkPlace : String = editProfileLayout.update_curent_work_place.text.toString()
                                    val validatePhone  = "^[+]?[0-9]{9,11}$"
                                    val pattern: Pattern = Pattern.compile(validatePhone)
                                    if (uName.isEmpty() || uDob.isEmpty() || uGender.isEmpty()
                                        ||uEducationLevel.isEmpty() || uPhone.isEmpty() || uAdress.isEmpty()
                                        || uCurentWorkPlace.isEmpty()) {
                                        message("Thay ?????i th???t b???i do b??? tr???ng th??ng tin")
                                    } else if(uName.length > 26 ){
                                        message("Thay ?????i th???t b???i do t??n qu?? 26 k?? t???")
                                    } else if(!pattern.matcher(uPhone).find()){
                                        message("Thay ?????i th???t b???i do kh??ng ????ng ?????nh d???ng s??? ??i???n tho???i")
                                    }else{
                                        userme!!.name = uName
                                        userme!!.dob = uDob
                                        userme!!.address = uAdress
                                        userme!!.currentWorkPlace = uCurentWorkPlace
                                        userme!!.phoneNumber = uPhone
                                        userme!!.educationLevel = uEducationLevel
                                        when (uGender){
                                            "Nam" -> {
                                                userme!!.gender.genderID = 1
                                                userme!!.gender.name = "MALE"
                                            }
                                            "N???" -> {
                                                userme!!.gender.genderID = 2
                                                userme!!.gender.name = "FEMALE"
                                            }
                                            "Kh??c" -> {
                                                userme!!.gender.genderID = 3
                                                userme!!.gender.name = "ORTHER"
                                            }
                                        }
                                        val model : UserViewModel = ViewModelProvider(main)[UserViewModel::class.java]
                                        profileViewModel.updateUser(mainViewModel.token.toString(),userme!!,model)
                                    }
                                }

                                builder.setNegativeButton("cancel")
                                { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }

                                val dialog = builder.create()
                                dialog.show()
                            }

                            p_parent.setOnClickListener {
                                val actionBar: ActionBar? = main.supportActionBar
                                actionBar?.setDisplayShowHomeEnabled(true)
                                actionBar?.setDisplayUseLogoEnabled(true)
                                actionBar?.setLogo(R.drawable.ic_baseline_profile_ind_24)
                                actionBar?.title = "Ph??? huynh"
                                navController.popBackStack()
                                navController.navigate(R.id.nav_parentFragment)
                            }
                        }
                    }
                    is DataState.Error -> {
                        requireView().process_profile.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            profileViewModel.changing.collect {
                when (it) {
                    is DataState.Loading -> {
                        requireView().process_profile.visibility = View.VISIBLE
                        requireView().profile_scollview.visibility = View.INVISIBLE
                    }
                    is DataState.Success -> {
                        when (it.data) {
                            "Thay ?????i th??nh c??ng" -> {
                                requireView().process_profile.visibility = View.GONE
                                Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                                navController.navigate(R.id.nav_profile)
                            }
                            "success" -> {
                                Toast.makeText(context, "?????i m???t kh???u th??nh c??ng\nXin m???i ????ng nh???p l???i", Toast.LENGTH_LONG).show()
                                val intent = Intent(context, LoginRegisterActivity::class.java)
                                startActivity(intent)
                                main.finish()
                            }
                            else -> {
                                requireView().process_profile.visibility = View.INVISIBLE
                                requireView().profile_scollview.visibility = View.VISIBLE
                                Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    is DataState.Error -> {
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        lifecycleScope.launchWhenCreated {
            profileViewModel.changingstatus.collect {
                when (it) {
                    is DataState.Loading -> {
                        requireView().process_img.visibility = View.VISIBLE
                        requireView().profile_img.visibility = View.INVISIBLE
                    }
                    is DataState.Success -> {
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                        requireView().process_img.visibility = View.GONE
                        navController.popBackStack()
                        navController.navigate(R.id.nav_profile)
                    }
                    is DataState.Error -> {
                        requireView().process_img.visibility = View.GONE
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



    //Xin quy???n truy c???p m??y ???nh v?? gallery
    private fun requestPermission() {
        val permissionlistener: PermissionListener = object : PermissionListener {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onPermissionGranted() {
                openImagePicker()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(
                    context,
                    "???? t??? ch???i c???p ph??p\n$deniedPermissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        TedPermission.create()
            .setPermissionListener(permissionlistener)
            .setDeniedMessage("N???u b???n t??? ch???i c???p ph??p th?? b???n kh??ng th??? s??? d???ng ???????c d???ch v???\n\nH??y c???p ph??p b???ng c??ch truy c???p [Setting] > [Permission]")
            .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()

    }

    //Ch???n ???nh trong gallery
    @RequiresApi(Build.VERSION_CODES.P)
    private fun openImagePicker() {
        TedBottomPicker.with(this.activity)
            .show {
                val source = ImageDecoder.createSource((this.activity)!!.contentResolver,it)
                val bitmap = ImageDecoder.decodeBitmap(source)
                profile_img.setImageBitmap(bitmap)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val image = stream.toByteArray()
                val imageBase64 : String = Base64.getEncoder().encodeToString(image)

                val map = HashMap<String,String>()
                map["imgBase64"] = imageBase64

                val main : MainActivity = activity as MainActivity
                val model : UserViewModel = ViewModelProvider(main)[UserViewModel::class.java]
                profileViewModel.updateImgUser(mainViewModel.token.toString(),map,model)
            }

    }

    private fun getMyUser(token: String) {
        profileViewModel.getUserMe(token)
        Log.i("L???y h???c sinh","Yes")
        lifecycleScope.launchWhenCreated {
            profileViewModel.user.collect{
                if(it is DataState.Success){
                    userme = it.data
                }
            }
        }
    }

    private fun message(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}
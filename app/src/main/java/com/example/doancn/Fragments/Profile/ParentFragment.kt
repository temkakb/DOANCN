package com.example.doancn.Fragments.Profile

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doancn.Adapters.ParentAdapter
import com.example.doancn.MainActivity
import com.example.doancn.Models.Parent
import com.example.doancn.Models.UserMe
import com.example.doancn.R
import com.example.doancn.Retrofit.RetrofitManager
import com.example.doancn.ViewModels.UserViewModel
import kotlinx.android.synthetic.main.add_parent.view.*
import kotlinx.android.synthetic.main.edit_parent.view.*
import kotlinx.android.synthetic.main.fragment_parent.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern


class ParentFragment : Fragment(), ParentAdapter.OnItemClickListener {

    private var userme : UserMe? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_parent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val main : MainActivity = activity as MainActivity
        val model : UserViewModel = ViewModelProvider(main)[UserViewModel::class.java]
        userme = model.user
        val parentAdapter = ParentAdapter(this)
        rcv_parent.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        parentAdapter.setData(getListParent() as ArrayList<Parent>?)
        rcv_parent.adapter = parentAdapter
        add_parent.setOnClickListener {
            val addParentLayout: View = LayoutInflater.from(context)
                .inflate(R.layout.add_parent, null)
            val builder = AlertDialog.Builder(requireContext())
            builder.setView(addParentLayout)

            builder.setPositiveButton("Thêm")
            { dialogInterface: DialogInterface, i1: Int ->
                val sharedprefernces = this.context?.getSharedPreferences("tokenstorage", Context.MODE_PRIVATE)
                val token : String? = sharedprefernces?.getString("token",null)
                val a_p_name : String = addParentLayout.add_parent_name.text.toString()
                val a_p_phone : String = addParentLayout.add_parent_phone.text.toString()
                val a_p_address : String = addParentLayout.add_parent_adress.text.toString()
                val validatePhone  = "^[+]?[0-9]{9,11}$"
                val PATTERN: Pattern = Pattern.compile(validatePhone)
                if (a_p_name.isEmpty() || a_p_phone.isEmpty() || a_p_address.isEmpty()) {
                    message("Thay đổi thất bại do bỏ trống thông tin")
                } else if(a_p_name.length > 26 ){
                    message("Thay đổi thất bại do tên quá 26 ký tự")
                } else if(!PATTERN.matcher(a_p_phone).find()){
                    message("Thay đổi thất bại do không đúng định dạng số điện thoại")
                }else{
                    val mapNewParent = HashMap<String,String>()
                    mapNewParent["newParentName"] = a_p_name
                    mapNewParent["newParentPhone"] = a_p_phone
                    mapNewParent["newParentAddress"] = a_p_address
                    val call: Call<Unit> = RetrofitManager.parentapi.addParent("Bearer "+token
                        ,userme!!.userId,mapNewParent)
                    call.enqueue(object : Callback<Unit?> {
                        override fun onResponse(call: Call<Unit?>, response: Response<Unit?>) {
                            if(response.isSuccessful){
                                val main : MainActivity = activity as MainActivity
                                val model : UserViewModel = ViewModelProvider(main)[UserViewModel::class.java]
                                getParents(token!!,userme!!.userId,model)
                                main.runOnUiThread {
                                    val handler = Handler()
                                    handler.postDelayed({
                                        Toast.makeText(
                                            context,
                                            "Thêm phụ huynh thành công",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        val transaction: FragmentTransaction =
                                            main.supportFragmentManager.beginTransaction()
                                        transaction.replace(R.id.content_frame, ParentFragment())
                                        transaction.commit()
                                    }, 100)
                                }
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
    }

    //Thay đổi thông tin phụ huynh trong list
    override fun onItemClick(position: Int) {
        val updateParentLayout: View = LayoutInflater.from(context)
            .inflate(R.layout.edit_parent, null)
        updateParentLayout.update_parent_name.setText(userme!!.parents[position].name)
        updateParentLayout.update_parent_phone.setText(userme!!.parents[position].phoneNumber)
        updateParentLayout.update_parent_adress.setText(userme!!.parents[position].address)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(updateParentLayout)

        builder.setPositiveButton("Thay đổi")
        { dialogInterface: DialogInterface, i1: Int ->
            val sharedprefernces = this.context?.getSharedPreferences("tokenstorage", Context.MODE_PRIVATE)
            val token : String? = sharedprefernces?.getString("token",null)
            val u_p_name : String = updateParentLayout.update_parent_name.text.toString()
            val u_p_phone : String = updateParentLayout.update_parent_phone.text.toString()
            val u_p_address : String = updateParentLayout.update_parent_adress.text.toString()
            val validatePhone  = "^[+]?[0-9]{9,11}$"
            val PATTERN: Pattern = Pattern.compile(validatePhone)
            if (u_p_name.isEmpty() || u_p_phone.isEmpty() || u_p_address.isEmpty()) {
                message("Thay đổi thất bại do bỏ trống thông tin")
            } else if(u_p_name.length > 26 ){
                message("Thay đổi thất bại do tên quá 26 ký tự")
            } else if(!PATTERN.matcher(u_p_phone).find()){
                message("Thay đổi thất bại do không đúng định dạng số điện thoại")
            }else{
                userme!!.parents[position].name = u_p_name
                userme!!.parents[position].phoneNumber = u_p_phone
                userme!!.parents[position].address = u_p_address
                val mapNewParent = HashMap<String,String>()
                mapNewParent["updateParentName"] = u_p_name
                mapNewParent["updateParentPhone"] = u_p_phone
                mapNewParent["updateParentAddress"] = u_p_address
                val call: Call<Unit> = RetrofitManager.parentapi.updateParent("Bearer "+token
                    ,userme!!.parents[position].parentId,mapNewParent)
                call.enqueue(object : Callback<Unit?> {
                    override fun onResponse(call: Call<Unit?>, response: Response<Unit?>) {
                        if(response.isSuccessful){
                            Toast.makeText(context, "Thay đổi thông tin thành công", Toast.LENGTH_LONG).show()
                            val main : MainActivity = activity as MainActivity
                            val model : UserViewModel = ViewModelProvider(main)[UserViewModel::class.java]
                            model.user!!.parents[position] = userme!!.parents[position]
                            getParents(token!!,model.user!!.userId,model)
                            val transaction : FragmentTransaction = main.supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.content_frame,ParentFragment())
                            transaction.commit()
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

    //Xóa item trong list
    override fun onRemoveItemClick(position: Int) {
        val sharedprefernces = this.context?.getSharedPreferences("tokenstorage", Context.MODE_PRIVATE)
        val token : String? = sharedprefernces?.getString("token",null)
        val call: Call<Unit> = RetrofitManager.parentapi.deleteParent("Bearer "+token
            ,userme!!.userId,userme!!.parents[position].parentId)
        call.enqueue(object : Callback<Unit?> {
            override fun onResponse(call: Call<Unit?>, response: Response<Unit?>) {
                if(response.isSuccessful){
                    Toast.makeText(context,"Đã xóa phụ huynh  ${userme!!.parents[position].name}"
                        ,Toast.LENGTH_LONG).show()
                    val main : MainActivity = activity as MainActivity
                    val model : UserViewModel = ViewModelProvider(main)[UserViewModel::class.java]
                    model.user!!.parents.removeAt(position)
                    val transaction : FragmentTransaction = main.supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.content_frame,ParentFragment())
                    transaction.commit()
                }
            }
            override fun onFailure(call: Call<Unit?>, t: Throwable) {
                Log.e("ERROR: ", t.message!!)
            }
        })
    }


    private fun getListParent(): List<Parent>? {
        val list = ArrayList<Parent>()
        list.addAll(userme!!.parents)
        return list
    }

    //Lấy phụ huynh
    private fun getParents(token : String, id : Int , model: UserViewModel){

        val callSync : Call<List<Parent>> = RetrofitManager.parentapi.getUserParent("Bearer $token",id)
        callSync.enqueue(object : Callback<List<Parent>?> {
            override fun onResponse(call: Call<List<Parent>?>, response: Response<List<Parent>?>) {
                if(response.isSuccessful){
                    model.user!!.parents = response.body() as ArrayList<Parent>
                    userme!!.parents = response.body() as ArrayList<Parent>
                }
            }
            override fun onFailure(call: Call<List<Parent>?>, t: Throwable) {
                Log.e("ERROR: ", t.message!!)
            }
        })
    }

    fun message(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

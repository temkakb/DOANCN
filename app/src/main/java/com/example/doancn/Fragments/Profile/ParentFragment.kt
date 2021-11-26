package com.example.doancn.Fragments.Profile

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doancn.Adapters.ParentAdapter
import com.example.doancn.DI.DataState
import com.example.doancn.MainViewModel
import com.example.doancn.Models.Parent
import com.example.doancn.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_parent.view.*
import kotlinx.android.synthetic.main.change_pasword.view.*
import kotlinx.android.synthetic.main.edit_parent.view.*
import kotlinx.android.synthetic.main.edit_profile.view.*
import kotlinx.android.synthetic.main.fragment_joinclass.view.*
import kotlinx.android.synthetic.main.fragment_parent.*
import kotlinx.android.synthetic.main.fragment_parent.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.android.synthetic.main.people_fragment.*
import kotlinx.android.synthetic.main.people_fragment.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@ExperimentalCoroutinesApi
@AndroidEntryPoint

class ParentFragment : Fragment(), ParentAdapter.OnItemClickListener {

    private lateinit var navController: NavController
    private var parentAdapter: ParentAdapter? = null

    private val parentViewModel: ParentViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private var listParent : ArrayList<Parent>? = ArrayList()

    companion object {
        fun newInstance() = ParentFragment()
    }

    private lateinit var viewModel: ParentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getParents(mainViewModel.token.toString())
        return inflater.inflate(R.layout.fragment_parent, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ParentViewModel::class.java)
    }

    override fun onStart() {
        observeData()
        super.onStart()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController= requireParentFragment().findNavController()
        super.onViewCreated(view, savedInstanceState)

        add_parent.setOnClickListener {
            val addParentLayout: View = LayoutInflater.from(context)
                .inflate(R.layout.add_parent, null)
            val builder = AlertDialog.Builder(requireContext())
            builder.setView(addParentLayout)

            builder.setPositiveButton("Thêm")
            { _: DialogInterface, _: Int ->
                val addParentName : String = addParentLayout.add_parent_name.text.toString()
                val addParentPhone : String = addParentLayout.add_parent_phone.text.toString()
                val addParentAddress : String = addParentLayout.add_parent_adress.text.toString()
                val validatePhone  = "^[+]?[0-9]{9,11}$"
                val pattern: Pattern = Pattern.compile(validatePhone)
                if (addParentName.isEmpty() || addParentPhone.isEmpty() || addParentAddress.isEmpty()) {
                    message("Thay đổi thất bại do bỏ trống thông tin")
                } else if(addParentName.length > 26 ){
                    message("Thay đổi thất bại do tên quá 26 ký tự")
                } else if(!pattern.matcher(addParentPhone).find()){
                    message("Thay đổi thất bại do không đúng định dạng số điện thoại")
                }else{
                    val mapNewParent = HashMap<String,String>()
                    mapNewParent["newParentName"] = addParentName
                    mapNewParent["newParentPhone"] = addParentPhone
                    mapNewParent["newParentAddress"] = addParentAddress
                    parentViewModel.addUserParent(mainViewModel.token.toString(),mapNewParent)
                }
            }
            builder.setNegativeButton("cancel")
            { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
            val dialog = builder.create()
            dialog.show()
        }
    }

    //Thay đổi thông tin phụ huynh trong list
    override fun onItemClick(position: Int) {
        val updateParentLayout: View = LayoutInflater.from(context)
            .inflate(R.layout.edit_parent, null)
        updateParentLayout.update_parent_name.setText(listParent!![position].name)
        updateParentLayout.update_parent_phone.setText(listParent!![position].phoneNumber)
        updateParentLayout.update_parent_adress.setText(listParent!![position].address)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(updateParentLayout)

        builder.setPositiveButton("Thay đổi")
        { _: DialogInterface, _: Int ->
            val updateParentName : String = updateParentLayout.update_parent_name.text.toString()
            val updateParentPhone : String = updateParentLayout.update_parent_phone.text.toString()
            val updateParentAddress : String = updateParentLayout.update_parent_adress.text.toString()
            val validatePhone  = "^[+]?[0-9]{9,11}$"
            val pattern: Pattern = Pattern.compile(validatePhone)
            if (updateParentName.isEmpty() || updateParentPhone.isEmpty() || updateParentAddress.isEmpty()) {
                message("Thay đổi thất bại do bỏ trống thông tin")
            } else if(updateParentName.length > 26 ){
                message("Thay đổi thất bại do tên quá 26 ký tự")
            } else if(!pattern.matcher(updateParentPhone).find()){
                message("Thay đổi thất bại do không đúng định dạng số điện thoại")
            }else{
                listParent!![position].name = updateParentName
                listParent!![position].phoneNumber = updateParentPhone
                listParent!![position].address = updateParentAddress
                val mapNewParent = HashMap<String,String>()
                mapNewParent["updateParentName"] = updateParentName
                mapNewParent["updateParentPhone"] = updateParentPhone
                mapNewParent["updateParentAddress"] = updateParentAddress
                parentViewModel.updateUserParent(mainViewModel.token.toString()
                    ,listParent!![position].parentId, mapNewParent)
            }
        }
        builder.setNegativeButton("cancel")
        { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    //Xóa item trong list
    override fun onRemoveItemClick(position: Int) {
        parentViewModel.deleteUserParent(mainViewModel.token.toString(),listParent!![position])
    }

    private fun observeData() {

        lifecycleScope.launchWhenCreated {
            parentViewModel.parents.collect {
                when (it) {
                    is DataState.Loading -> {
                        requireView().noParents.visibility = View.GONE
                        requireView().process_parent.visibility = View.VISIBLE
                    }
                    is DataState.Success -> {
                        requireView().process_parent.visibility = View.GONE
                        if (it.data.isNullOrEmpty() || it.data.isEmpty()) {
                            requireView().noParents.visibility = View.VISIBLE
                            requireView().rcv_parent.adapter = null
                            parentAdapter = null
                        } else {
                            requireView().noParents.visibility = View.GONE
                            if (parentAdapter == null) {
                                listParent = it.data as ArrayList<Parent>?
                                requireView().rcv_parent.layoutManager = LinearLayoutManager(context
                                    ,RecyclerView.VERTICAL,false)
                                parentAdapter = ParentAdapter(this@ParentFragment)
                                parentAdapter!!.setData(listParent)
                                requireView().rcv_parent.adapter = parentAdapter
                            } else {
                                listParent = it.data as ArrayList<Parent>?
                                parentAdapter!!.setData(listParent)
                            }
                        }
                    }
                    is DataState.Error -> {
                        requireView().process_parent.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            parentViewModel.changing.collect {
                when (it) {
                    is DataState.Success -> {
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                    }
                    is DataState.Error -> {
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



    //Lấy phụ huynh
    private fun getParents(token : String){
        parentViewModel.getUserParents(token)
    }

    private fun message(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

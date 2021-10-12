package com.example.doancn.Fragments.JoinClass

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doancn.Adapters.SubjectsAdapter
import com.example.doancn.Models.Classroom
import com.example.doancn.Models.Subject
import com.example.doancn.R
import com.example.doancn.Repository.SubjectRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_joinclass.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.*


class JoinClassFragment : Fragment() {
    private lateinit var repository: SubjectRepository
    private var listsubjects: List<Subject>?=null
    private lateinit var subject : RecyclerView
    private lateinit var layoutmanager : LinearLayoutManager
    private lateinit var  fusedLocation : FusedLocationProviderClient
    private var classrooms :List<Classroom>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        repository= SubjectRepository()
        val view = inflater.inflate(R.layout.fragment_joinclass, container, false)
        layoutmanager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        subject = view.RC_subjects
        getClassrooms()
        getSubjects()

//        view.joinclass_listview.adapter=EnrolmentArrayAdapter(requireContext(),listfakedata2)
        return view
    }





    private fun getSubjects (){
        GlobalScope.launch {
            try {
            listsubjects= repository.getSubjects()
            Log.d("gigidonemay", listsubjects!![0].name)
            withContext(Dispatchers.Main) {
                subject.adapter = SubjectsAdapter(listsubjects!!)
                subject.layoutManager = layoutmanager
            }
            }catch (e:HttpException){ // dolater when it have
            }
        }
    }
    private fun getClassrooms (){
        fusedLocation=LocationServices.getFusedLocationProviderClient(requireActivity())
        // check permission
        if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
           fusedLocation.lastLocation.addOnCompleteListener {
               task->
               val location : Location = task.getResult()
               if (location!=null){
                   val geocoder = Geocoder(requireContext(),Locale.getDefault())
                    val listaddress : List<Address> =geocoder.getFromLocation(location.latitude,location.longitude,1)
                   Log.d("gigido",listaddress[0].locality)

               }
           }
        }else{
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),44)
        }
    }



}

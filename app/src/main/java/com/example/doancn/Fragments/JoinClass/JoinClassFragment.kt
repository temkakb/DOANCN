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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doancn.Adapters.EnrolmentArrayAdapter
import com.example.doancn.Adapters.SubjectsAdapter
import com.example.doancn.DI.DataState
import com.example.doancn.R
import com.example.doancn.Repository.SubjectRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.class_items.view.*
import kotlinx.android.synthetic.main.fragment_joinclass.view.*
import kotlinx.android.synthetic.main.fragment_joinclass.view.process
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class JoinClassFragment : Fragment() {
    private val joinClassViewModel: JoinClassViewModel by viewModels()
    private lateinit var repository: SubjectRepository
    private lateinit var listoptionname: Array<String>
    private lateinit var listsubjectname: Array<String>
    private lateinit var listsubjectname2: Array<String>
    private lateinit var subject: RecyclerView
    private lateinit var layoutmanager: LinearLayoutManager
    private lateinit var fusedLocation: FusedLocationProviderClient
    private lateinit var noclassroom: TextView
    private lateinit var noclassroomimageview: ImageView
    private var enrolmentArrayAdapter: EnrolmentArrayAdapter? = null
    private lateinit var txtbeforeLoading: CharSequence


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        repository = SubjectRepository()
        val view = inflater.inflate(R.layout.fragment_joinclass, container, false)
        layoutmanager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        noclassroom = view.noclassroom
        noclassroomimageview = view.noclassroom_image
        listoptionname = resources.getStringArray(R.array.option)
        subject = view.RC_subjects
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireActivity())
        observeData()
        getClassrooms(null)
        getSubjects()
        return view
    }


    private fun getSubjects() {
        listsubjectname = resources.getStringArray(R.array.enrollment_subjects)
        listsubjectname2 = resources.getStringArray(R.array.Subjects)
        subject.adapter = SubjectsAdapter(listsubjectname, this@JoinClassFragment)
        subject.layoutManager = layoutmanager

    }

    fun getClassrooms(subjectId: Long?) {
        Log.d("position", subjectId.toString())
        // check permission
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocation.lastLocation.addOnCompleteListener { task ->
                val location: Location = task.result
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val listaddress: List<Address> =
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                requireView().city.text = listaddress[0].locality
                joinClassViewModel.getClassRoomToEnroll(listaddress[0].locality, subjectId)
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 44
            )
        }
    }

    private fun observeData() {
        lifecycleScope.launchWhenCreated {
            joinClassViewModel.classrooms.collect {
                when (it) {
                    is DataState.Loading -> {
                        requireView().process.visibility = View.VISIBLE
                    }
                    is DataState.Success -> {
                        requireView().process.visibility = View.GONE
                        if (it.data.isNullOrEmpty()) {
                            noclassroom.visibility = View.VISIBLE
                            requireView().joinclass_listview.adapter = null
                            enrolmentArrayAdapter = null
                        } else {
                            noclassroom.visibility = View.INVISIBLE
                            if (enrolmentArrayAdapter == null) {
                                enrolmentArrayAdapter = EnrolmentArrayAdapter(
                                    requireContext(),
                                    it.data,
                                    listsubjectname2,
                                    listoptionname,
                                    joinClassViewModel
                                )
                                requireView().joinclass_listview.adapter = enrolmentArrayAdapter

                            } else enrolmentArrayAdapter!!.swapDataSet(it.data)

                        }
                    }
                    is DataState.Error -> {
                        requireView().process.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_LONG).show()
                    }
                }
            }

        }

        lifecycleScope.launchWhenCreated {
            joinClassViewModel.enrollstatus.collect { it ->

                when (it) {
                    is DataState.Loading -> {
                        txtbeforeLoading = joinClassViewModel.view!!.btn_enroll.text
                        joinClassViewModel.view!!.process.visibility = View.VISIBLE
                        if (joinClassViewModel.btnview == null)
                            joinClassViewModel.view!!.btn_enroll.text = null
                        else
                            joinClassViewModel.view!!.btn_enroll.text = "Đang tiến hành"

                    }
                    is DataState.Success -> {
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                        val btn = joinClassViewModel.view!!.btn_enroll
                        joinClassViewModel.view!!.process.visibility = View.GONE
                        if (it.data == "Đăng ký thành công") {
                            btn.text = resources.getString(R.string.enrolled)
                            btn.background = (ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.bg_button_enrolled
                            ))
                            joinClassViewModel.btnview?.let { it ->
                                it.text = resources.getString(R.string.enrolled)
                                it.background = (ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.bg_button_enrolled
                                ))
                            }
                        } else if (it.data == "Hủy đăng ký thành công") {
                            btn.text = resources.getString(R.string.sigup)
                            btn.background = (ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.bg_button_enroll
                            ))
                            joinClassViewModel.btnview?.let { it ->
                                it.text = resources.getString(R.string.sigup)
                                it.background = (ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.bg_button_enroll
                                ))
                            }
                        }
                    }
                    is DataState.Error -> {
                        joinClassViewModel.view!!.btn_enroll.text = txtbeforeLoading
                        joinClassViewModel.view!!.process.visibility = View.GONE
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}

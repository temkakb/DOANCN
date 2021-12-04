package com.example.doancn.Fragments.MyClass.home

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.bumptech.glide.Glide
import com.example.doancn.Adapters.AnnouncementAdapter
import com.example.doancn.ClassViewModel
import com.example.doancn.Models.Announcement
import com.example.doancn.Models.Classroom
import com.example.doancn.R
import com.example.doancn.databinding.BannerInfoBinding
import com.example.doancn.databinding.ClassHomeFragmentBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import android.content.Intent
import android.net.Uri
import androidx.navigation.NavController
import com.example.doancn.Utilities.StringUtils
import com.google.android.material.chip.Chip
import com.google.firebase.database.FirebaseDatabase
import com.loopj.android.http.AsyncHttpResponseHandler

import com.loopj.android.http.AsyncHttpClient
import cz.msebera.android.httpclient.Header


@ExperimentalCoroutinesApi
class ClassHomeFragment : Fragment() {

    private var _binding: ClassHomeFragmentBinding? = null
    private val classViewModel: ClassViewModel by activityViewModels()
    private val binding get() = _binding!!
    private lateinit var vp2: ViewPager2
    private lateinit var navController: NavController
    companion object {
        fun newInstance() = ClassHomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ClassHomeFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //setupAnnouncementTable()
        if (arguments?.containsKey("classRoomHome") == true) {
            val classroom =
                arguments?.getSerializable("classRoomHome") as Classroom
            Log.d("classroom", classroom.toString())
            classViewModel.selectItem(classroom)
        }
        setupBanner()
        return root
    }

    private fun setupBanner() {
        binding.className.text = classViewModel.classroom.value!!.name
        binding.teacherName.text = classViewModel.classroom.value!!.teacher.name
        binding.shortDescription.text = classViewModel.classroom.value!!.shortDescription
        val members = classViewModel.classroom.value!!.currentAttendanceNumber
        binding.members.text = "$members học viên"
        binding.banner.setOnClickListener {
            val classroom = classViewModel.classroom.value
            val dialog = MaterialDialog(requireContext()).apply {
                customView(R.layout.banner_info, scrollable = true)
                val viewBinding: BannerInfoBinding = BannerInfoBinding.bind(this.getCustomView())
                val circularProgressDrawable = CircularProgressDrawable(requireContext())
                circularProgressDrawable.strokeWidth = 5f
                circularProgressDrawable.centerRadius = 30f
                circularProgressDrawable.start()
                val bmp = classroom!!.teacher.image?.let {
                    val imgDecode: ByteArray =
                        Base64.getDecoder().decode(it)
                    BitmapFactory.decodeByteArray(imgDecode, 0, imgDecode.size)
                } ?: ""

                Log.d("bitmap",bmp.toString())

                Glide.with(requireContext())
                    .asBitmap()
                    .load(bmp)
                    .centerCrop()
                    .placeholder(circularProgressDrawable)
                    .error(R.drawable.orther)
                    .into(viewBinding.profileImage)

                viewBinding.teacherName.text = "Gv: ${classroom.teacher.name}"
                viewBinding.teacherPhoneNumber.text = "Phone :${classroom.teacher.phoneNumber}"
                viewBinding.teacherPhoneNumber.setOnClickListener{
                    val phone = classroom.teacher.phoneNumber
                    val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
                    startActivity(intent)
                }
                viewBinding.className.setText(classroom.name)
                viewBinding.address.setText(classroom.location.address)
                viewBinding.paymentOption.setText(resources.getStringArray(R.array.option)[classroom.option.paymentOptionId.toInt()-1])
                viewBinding.fee.setText(classroom.fee.toString())
                //setThumbnails()

                viewBinding.map.setOnClickListener{
                    val destinationLatitude = classroom.location.coordinateX
                    val destinationLongitude = classroom.location.coordinateY
                    val address = classroom.location.address
//                    Log.d("location",destinationLatitude.toString())
//                    Log.d("location",destinationLongitude.toString())
//                    val gmmIntentUri = Uri.parse("google.navigation:q=$destinationLatitude,$destinationLongitude&mode=d")
//                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//                    mapIntent.setPackage("com.google.android.apps.maps")
//                    startActivity(mapIntent)

                    val gmmIntentUri =
                        Uri.parse("geo:0,0?q=${destinationLatitude},${destinationLongitude}(${address})")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)

                }
                for(shift in classroom.shifts.reversed()){

                    val chip = layoutInflater.inflate(
                        R.layout.chip,
                        viewBinding.chipGroup,
                        false
                    ) as Chip
                    chip.text = StringUtils.dowFormatter(shift.dayOfWeek.dowName)
                    chip.setPadding(35,35,35,35)
                    chip.textSize = 18f
                    chip.layoutDirection = View.LAYOUT_DIRECTION_LOCALE
                    viewBinding.chipGroup.addView(chip)

                    val startAt = classroom.shifts.first().startAt
                    val hours = startAt / 3600000
                    val minute = (startAt % 3600000) / 60000
                    viewBinding.timeStart.setText(
                        String.format(
                            "%d giờ %dphút ",
                            hours.toInt(),
                            minute.toInt()
                        )
                    )
                }
            }
            dialog.show()
        }

    }

//    private fun setupAnnouncementTable() {
//        vp2 = binding.vp2Announcement
//        vp2.adapter = AnnouncementAdapter(requireContext(), Announcement.listOfAnnouncement())
//        vp2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
//
//    }


    fun setThumbnails() {
        val client = AsyncHttpClient()
        client["https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=14&size=600x300&key=AIzaSyCy-bRrRM94EOS0HxczPaxjoEOvJ3Z-UDQ", object : AsyncHttpResponseHandler() {
            override fun onStart() {
                // called before request is started
            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header?>?,
                response: ByteArray?
            ) {
                Log.d("hihi",response.toString())

            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header?>?,
                errorResponse: ByteArray?,
                e: Throwable?
            ) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d("onfaillure","failed")
            }

            override fun onRetry(retryNo: Int) {
                // called when request is retried
            }
        }]
    }

}

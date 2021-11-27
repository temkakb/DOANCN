package com.example.doancn.Fragments.Home

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.demotranghome.Adapters.CalendarWeekAdapter
import com.example.demotranghome.Interfaces.DateWatcher
import com.example.demotranghome.Interfaces.OnDateClickListener
import com.example.demotranghome.Models.CalendarDay
import com.example.doancn.Adapters.ShiftOfClassAdapter
import com.example.doancn.DI.DataState
import com.example.doancn.Fragments.MyClass.people.PeopleFragment
import com.example.doancn.HorizontalWeekCalendar
import com.example.doancn.MainActivity
import com.example.doancn.MainViewModel
import com.example.doancn.Models.*
import com.example.doancn.R
import com.example.doancn.ViewModels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_joinclass.view.*
import kotlinx.android.synthetic.main.people_fragment.*
import kotlinx.android.synthetic.main.people_fragment.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var selected: GregorianCalendar? = null
    private var calendarView: HorizontalWeekCalendar? = null

    private val homeViewModel: HomeViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private var userme:UserMe? = null
    companion object {
        fun newInstance() = PeopleFragment()
    }

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val main : MainActivity = activity as MainActivity
        val model : UserViewModel = ViewModelProvider(main)[UserViewModel::class.java]
        getMyUser(mainViewModel.token.toString(),model)
        return inflater.inflate(R.layout.fragment_home,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val calendar = Calendar.getInstance()
        selected = GregorianCalendar(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])

        calendarView = HorizontalWeekCalendar.Builder()
            .setView(R.id.horizontalCalendar)
            .init(this)

        calendarView!!.setSelectedBackground(R.drawable.grad)
        calendarView!!.setSelectedTextColor(Color.WHITE)

        calendarView!!.setOnDateClickListener(object : OnDateClickListener {
            override fun onCalenderDayClicked(year: Int, month: Int, day: Int) {
                val selectedDay = GregorianCalendar(year, month, day)
                if (selected!!.compareTo(selectedDay) != 0) {
                    selected = selectedDay
                    GlobalScope.launch {
                        withContext(Dispatchers.Main) {
                            Log.d("MainActivity", mainViewModel.role.toString())
                            if (mainViewModel.role == "STUDENT") {
                                Log.d("ROLE", "Học sinh")
                                if( userme?.enrollments == null) {
                                    return@withContext
                                }
                                else{
                                    setStudentEventAdpater()
                                }
                            } else if (mainViewModel.role == "TEACHER") {
                                Log.d("ROLE", "Giáo cmn viên")
                                if( userme?.classes == null) {
                                    return@withContext
                                }
                                else{
                                    setTeacherEventAdpater()
                                }
                            }
                        }
                    }
                    val dateformat = SimpleDateFormat("dd/MM/yyyy")
                    Log.i("Ngày",dateformat.format(selected!!.time))
                }
            }
        })

        calendarView!!.setDateWatcher(object : DateWatcher {
            override fun getStateForDate(year: Int, month: Int, day: Int, view: CalendarWeekAdapter.DayViewHolder?): Int {
                return if (selected!!.compareTo(
                        GregorianCalendar(year,
                            month,
                            day
                        )
                    ) == 0
                ) CalendarDay.SELECTED else CalendarDay.DEFAULT
            }
        })
        val dateformat = SimpleDateFormat("dd/MM/yyyy")
        Log.i("Ngày",dateformat.format(selected!!.time))
        Log.i("Ngày trong tuần",selected?.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG, Locale.getDefault())!!.uppercase())
        observeData()
    }



    override fun onResume() {
        super.onResume()
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                Log.d("MainActivity", mainViewModel.role.toString())
                if (mainViewModel.role == "STUDENT") {
                    if( userme?.enrollments == null) {
                        return@withContext
                    }
                    else{
                        setStudentEventAdpater()
                    }
                } else if (mainViewModel.role == "TEACHER") {
                    if( userme?.classes == null) {
                        return@withContext
                    }
                    else{
                        setTeacherEventAdpater()
                    }
                }
            }
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            homeViewModel.user.collect {
                when (it) {
                    is DataState.Loading -> {
                        requireView().process_home.visibility = View.VISIBLE
                    }
                    is DataState.Success -> {
                        requireView().process_home.visibility = View.GONE
                        Log.d("MainActivity", mainViewModel.role.toString())
                        if (mainViewModel.role == "STUDENT") {
                            Log.d("ROLE", "Học sinh")
                                    if( userme?.enrollments == null) {
                                        return@collect
                                    }
                                    else{
                                        setStudentEventAdpater()
                                    }
                                } else if (mainViewModel.role == "TEACHER") {
                                    Log.d("ROLE", "Giáo cmn viên")
                                    if( userme?.classes == null) {
                                        return@collect
                                    }
                                    else{
                                        setTeacherEventAdpater()
                                    }
                                }

                    }
                    is DataState.Error -> {
                        requireView().process_home.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }


    @SuppressLint("SimpleDateFormat")
    private fun setStudentEventAdpater() {
        val main : MainActivity = activity as MainActivity
        val todayClass = TodayClass()
        val selectedDayOfWeek : String = selected?.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG,Locale.getDefault())!!.uppercase()
        val listsubjectname: Array<String> = resources.getStringArray(R.array.enrollment_subjects)
        val dateformat = SimpleDateFormat("dd/MM/yyyy")
        val selectDay = dateformat.format(selected!!.time).toString()
        for (i in userme!!.enrollments!!) {
            if(i.accepted)
            todayClass.classrooms.add(i.classroom)
        }
        val dailyClass: ArrayList<Classroom> =
            todayClass.ClassroomsForDate(selectedDayOfWeek,selectDay)

        main.runOnUiThread {
            val eventAdapter = ShiftOfClassAdapter(requireContext(),dailyClass
                ,selectedDayOfWeek,listsubjectname)
            today_class_listview.adapter = eventAdapter
            if (dailyClass.count() == 0)
            {
                noclassroom.visibility = View.VISIBLE
            }
            else{
                noclassroom.visibility = View.GONE
            }
        }
        today_class_listview.setOnItemClickListener { parent, view, position, id ->
            main.homeToClass(dailyClass[position])
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun setTeacherEventAdpater() {
        val main : MainActivity = activity as MainActivity
        val todayClass = TodayClass()
        val selectedDayOfWeek : String = selected?.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG,Locale.getDefault())!!.uppercase()
        val dateformat = SimpleDateFormat("dd/MM/yyyy")
        val selectDay = dateformat.format(selected!!.time).toString()
        val listsubjectname: Array<String> = resources.getStringArray(R.array.enrollment_subjects)
        for (i in userme!!.classes!!) {
            todayClass.classrooms.add(i)
        }
        val dailyClass : ArrayList<Classroom> =
            todayClass.TeacherClassroomsForDate(selectedDayOfWeek,selectDay)


        main.runOnUiThread {
            val eventAdapter = ShiftOfClassAdapter(requireContext(),dailyClass
                ,selectedDayOfWeek,listsubjectname)
            today_class_listview.adapter = eventAdapter
            if (dailyClass.count() == 0)
            {
                noclassroom.visibility = View.VISIBLE
            }
            else{
                noclassroom.visibility = View.GONE
            }
        }
        today_class_listview.setOnItemClickListener { parent, view, position, id ->
            main.homeToClass(dailyClass[position])
        }

    }

    private fun getMyUser(token: String, model: UserViewModel) {
        homeViewModel.getUserMe(token)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            homeViewModel.user.collect {
                if (it is DataState.Success) {
                    userme = it.data
                    model.user = it.data

                }
            }
        }
    }

}
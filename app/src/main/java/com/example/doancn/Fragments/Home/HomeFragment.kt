package com.example.doancn.Fragments.Home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.demotranghome.Adapters.CalendarWeekAdapter
import com.example.demotranghome.Interfaces.DateWatcher
import com.example.demotranghome.Interfaces.OnDateClickListener
import com.example.demotranghome.Models.CalendarDay
import com.example.doancn.Adapters.ShiftOfClassAdapter
import com.example.doancn.HorizontalWeekCalendar
import com.example.doancn.MainActivity
import com.example.doancn.MainViewModel
import com.example.doancn.Models.*
import com.example.doancn.R
import com.example.doancn.Retrofit.RetrofitManager
import com.example.doancn.ViewModels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var selected: GregorianCalendar? = null
    private var calendarView: HorizontalWeekCalendar? = null

    private val mainViewModel: MainViewModel by viewModels()

    private var userme:UserMe? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val main : MainActivity = activity as MainActivity
        val model : UserViewModel = ViewModelProvider(main)[UserViewModel::class.java]
        val sharedprefernces = main.getSharedPreferences("tokenstorage", Context.MODE_PRIVATE)
        val token: String? = sharedprefernces.getString("token", null)
        val thread = Thread {
            try {
                getMyUser(token!!,model)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        thread.start()
        thread.join()
        return inflater.inflate(R.layout.fragment_home,container,false)
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
    }



    override fun onResume() {
        super.onResume()
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
    }


    private fun setStudentEventAdpater() {
        val main : MainActivity = activity as MainActivity
        val todayClass = TodayClass()
        val selectedDayOfWeek : String = selected?.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG,Locale.getDefault())!!.uppercase()
        val listsubjectname: Array<String> = resources.getStringArray(R.array.Subjects)
        val dailyClass: ArrayList<Classroom>
        for (i in userme!!.enrollments!!) {
            //if(i.accepted)
            todayClass.classrooms.add(i.classroom)
        }
        dailyClass = todayClass.ClassroomsForDate(selectedDayOfWeek)

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

    private fun setTeacherEventAdpater() {
        val main : MainActivity = activity as MainActivity
        val todayClass = TodayClass()
        val selectedDayOfWeek : String = selected?.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG,Locale.getDefault())!!.uppercase()
        val listsubjectname: Array<String> = resources.getStringArray(R.array.Subjects)
        val dailyClass : ArrayList<Classroom>
        for (i in userme!!.classes!!) {
            todayClass.classrooms.add(i)
        }
        dailyClass = todayClass.ClassroomsForDate(selectedDayOfWeek)


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

        val callSync: Call<UserMe> = RetrofitManager.userapi.getme("Bearer $token")
        try {
            val response: Response<UserMe> = callSync.execute()
            model.user = response.body()
            userme = model.user
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

}
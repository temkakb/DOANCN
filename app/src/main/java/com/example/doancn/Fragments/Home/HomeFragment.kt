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
import androidx.lifecycle.ViewModelProvider
import com.example.doancn.HorizontalWeekCalendar
import com.example.demotranghome.Adapters.CalendarWeekAdapter
import com.example.demotranghome.Interfaces.DateWatcher
import com.example.demotranghome.Interfaces.OnDateClickListener
import com.example.demotranghome.Models.CalendarDay
import com.example.doancn.Adapters.ShiftOfClassAdapter
import com.example.doancn.MainActivity
import com.example.doancn.R
import com.example.doancn.Retrofit.RetrofitManager
import com.example.doancn.ViewModels.UserViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.example.doancn.Models.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.class_items.view.classname
import kotlinx.android.synthetic.main.detail_classroom_shift_dialog.view.*


class HomeFragment : Fragment() {
    private var selected: GregorianCalendar? = null
    private var calendarView: HorizontalWeekCalendar? = null

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
                    if( userme?.enrollments == null) {
                        return
                    }
                    else{
                        setEventAdpater()
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

        if( userme?.enrollments == null) {
            return
        }
        else{
            setEventAdpater()
        }
    }



    override fun onResume() {
        super.onResume()
        if( userme?.enrollments == null) {
            return
        }
        else{
            setEventAdpater()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setEventAdpater() {
        val todayClass = TodayClass()
        val selectedDayOfWeek : String = selected?.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG,Locale.getDefault())!!.uppercase()
        val listsubjectname: Array<String> = resources.getStringArray(R.array.Subjects)
        val dailyClass: ArrayList<Classroom>

        for(i in userme!!.enrollments!!) {
            todayClass.classrooms.add(i.classroom)
            }

        dailyClass = todayClass.ClassroomsForDate(selectedDayOfWeek)

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

        today_class_listview.setOnItemClickListener { parent, view, position, id ->
            val o = 0
            val olong : Long = o.toLong()
            val dateformat = SimpleDateFormat("dd-MM-yyyy")
            val i = LayoutInflater.from(context)
                .inflate(R.layout.detail_classroom_shift_dialog,null)
            i.shift_of_class_subject.text = listsubjectname[dailyClass[position].subject.subjectId.toInt()].uppercase()
            i.shift_of_class_teacher.text= dailyClass[position].teacher.name
            i.shift_of_class_address.text = dailyClass[position].location.address
            i.shift_of_class_about.text = dailyClass[position].about
            i.shift_of_class_shortdescription.text = dailyClass[position].shortDescription
            i.classname.text= dailyClass[position].name
            i.shift_of_class_studyday.text = dateformat.format(selected!!.time).toString()
            var durationtime : String
            for ( shift in dailyClass[position].shifts){
                if(shift.dayOfWeek.dowName == selectedDayOfWeek){
                    if(shift.duration.div(60000).toInt() <60 ){
                        durationtime = shift.duration.div(60000).toString()+" phút"
                    }else{
                        if (shift.duration.minus((shift.duration.div(3600000))
                                .times(3600000)) != olong)
                            durationtime = shift.duration.div(3600000).toString() +
                                " tiếng " + (shift.duration.minus((shift.duration.div(3600000))
                                    .times(3600000))).div(60000).toString() + " phút"
                        else
                            durationtime = shift.duration.div(3600000).toString() +
                                    " tiếng: "
                    }
                    i.shift_of_class_time.text = durationtime
                }else
                {
                    continue
                }
            }
            val builder = AlertDialog.Builder(requireContext())
            builder.setView(i)
            val dialog = builder.create()
            val cancel : TextView = i.findViewById(R.id.cancel_button)
            cancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
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
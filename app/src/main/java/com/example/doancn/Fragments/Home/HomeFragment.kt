package com.example.doancn.Fragments.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.doancn.Adapters.CalendarAdapter
import com.example.doancn.DataObject
import com.example.doancn.R
import com.example.navigationdrawer.CustomAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private val lastDayInCalendar = Calendar.getInstance()
    private val sdf = SimpleDateFormat("MM - yyyy")
    private val cal = Calendar.getInstance()

    // current date
    private val currentDate = Calendar.getInstance()
    private val currentDay = currentDate[Calendar.DAY_OF_MONTH]
    private val currentMonth = currentDate[Calendar.MONTH]
    private val currentYear = currentDate[Calendar.YEAR]

    // selected date
    private var selectedDay: Int = currentDay
    private var selectedMonth: Int = currentMonth
    private var selectedYear: Int = currentYear

    // all days in month
    private val dates = ArrayList<Date>()

    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendar_recycler_view.apply {
            //Calendar week
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(calendar_recycler_view)

            lastDayInCalendar.add(Calendar.MONTH, 6)

            setUpCalendar()

            calendar_prev_button!!.setOnClickListener {
                if (cal.after(currentDate)) {
                    cal.add(Calendar.MONTH, -1)
                    if (cal == currentDate)
                        setUpCalendar()
                    else
                        setUpCalendar(changeMonth = cal)
                }
            }

            calendar_next_button!!.setOnClickListener {
                if (cal.before(lastDayInCalendar)) {
                    cal.add(Calendar.MONTH, 1)
                    setUpCalendar(changeMonth = cal)
                }
            }
        }
        rvList.apply {
            //Recycle View Today Class
            val data : MutableList<DataObject> = ArrayList()
            for(i : Int in 1..10){
                data.add(DataObject("Lớp thứ $i"))
            }

            layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)

            val adapter = CustomAdapter(data)

            rvList.layoutManager = layoutManager
            rvList.setHasFixedSize(true)
            rvList.adapter =adapter
        }
    }

    private fun setUpCalendar(changeMonth: Calendar? = null) {
        txt_current_month!!.text = sdf.format(cal.time)
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)


        selectedDay =
            when {
                changeMonth != null -> changeMonth.getActualMinimum(Calendar.DAY_OF_MONTH)
                else -> currentDay
            }
        selectedMonth =
            when {
                changeMonth != null -> changeMonth[Calendar.MONTH]
                else -> currentMonth
            }
        selectedYear =
            when {
                changeMonth != null -> changeMonth[Calendar.YEAR]
                else -> currentYear
            }

        var currentPosition = 0
        dates.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)

        while (dates.size < maxDaysInMonth) {
            // get position of selected day
            if (monthCalendar[Calendar.DAY_OF_MONTH] == selectedDay)
                currentPosition = dates.size
            dates.add(monthCalendar.time)
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Assigning calendar view.
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        calendar_recycler_view!!.layoutManager = layoutManager
        val calendarAdapter = context?.let { CalendarAdapter(it, dates, currentDate, changeMonth) }
        calendar_recycler_view!!.adapter = calendarAdapter

        when {
            currentPosition > 2 -> calendar_recycler_view!!.scrollToPosition(currentPosition - 3)
            maxDaysInMonth - currentPosition < 2 -> calendar_recycler_view!!.scrollToPosition(currentPosition)
            else -> calendar_recycler_view!!.scrollToPosition(currentPosition)
        }

        calendarAdapter?.setOnItemClickListener(object : CalendarAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val clickCalendar = Calendar.getInstance()
                clickCalendar.time = dates[position]
                selectedDay = clickCalendar[Calendar.DAY_OF_MONTH]
            }
        })
    }
}
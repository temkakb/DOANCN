package com.example.demotranghome.Adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.demotranghome.Interfaces.DateClickCallback
import com.example.demotranghome.Interfaces.DateWatcher
import com.example.demotranghome.Interfaces.OnDateClickListener
import com.example.demotranghome.Interfaces.ResProvider
import com.example.demotranghome.Models.CalendarDay
import com.example.demotranghome.Models.CalendarDay.Companion.DEFAULT
import com.example.demotranghome.Models.CalendarDay.Companion.SELECTED
import com.example.doancn.R
import java.lang.String
import java.util.*
import kotlin.collections.ArrayList


class CalendarWeekAdapter(resProvider: ResProvider) :
    RecyclerView.Adapter<CalendarWeekAdapter.DayViewHolder>(), DateClickCallback {
    var days: MutableList<CalendarDay?> = ArrayList()
    private var recyclerView: RecyclerView? = null
    private var dateWatcher: DateWatcher? = null
    private var onDateClickListener: OnDateClickListener? = null
    private val resProvider: ResProvider
    private fun initCalendar() {
        val now = Calendar.getInstance()
        val createdDays: MutableList<CalendarDay?> = ArrayList()
        for (i in 0..15) {
            val today: Calendar = GregorianCalendar(
                now[Calendar.YEAR],
                now[Calendar.MONTH],
                now[Calendar.DAY_OF_MONTH]
            )
            today.add(Calendar.DAY_OF_MONTH, i * -1)
            val createdDay = CalendarDay(
                today[Calendar.YEAR],
                today[Calendar.MONTH],
                today[Calendar.DAY_OF_MONTH]
            )
            createdDays.add(createdDay)
        }
        Collections.reverse(createdDays)
        days.addAll(createdDays)
        for (i in 1..15) {
            val today: Calendar = GregorianCalendar(
                now[Calendar.YEAR],
                now[Calendar.MONTH],
                now[Calendar.DAY_OF_MONTH]
            )
            today.add(Calendar.DAY_OF_MONTH, i)
            val createdDay = CalendarDay(
                today[Calendar.YEAR],
                today[Calendar.MONTH],
                today[Calendar.DAY_OF_MONTH]
            )
            days.add(createdDay)
        }
    }

    fun addCalendarDays(loadAfter: Boolean) {
        val insertIdx = if (loadAfter) days.size - 1 else 0
        val insertionPoint: CalendarDay? = days[insertIdx]
        val createdDays: MutableList<CalendarDay?> = ArrayList<CalendarDay?>()
        for (i in 1..10) {
            val startDay: Calendar = GregorianCalendar(
                insertionPoint!!.year,
                insertionPoint.month,
                insertionPoint.day.get(Calendar.DAY_OF_MONTH)
            )
            val daysToAppendOrPrepend = if (loadAfter) i else i * -1
            startDay.add(Calendar.DAY_OF_MONTH, daysToAppendOrPrepend)
            val newDay = CalendarDay(
                startDay[Calendar.YEAR],
                startDay[Calendar.MONTH],
                startDay[Calendar.DAY_OF_MONTH]
            )
            createdDays.add(newDay)
        }
        if (!loadAfter) Collections.reverse(createdDays)
        days.addAll(if (loadAfter) insertIdx + 1 else 0, createdDays)
        notifyItemRangeInserted(if (loadAfter) insertIdx + 1 else 0, 10)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        return DayViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.weekcalendar_day, parent, false),
            resProvider,
            this
        )
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day: CalendarDay? = days[position]
        day!!.state= dateWatcher!!.getStateForDate(
                day.year, day.month, day.day.get(
                    Calendar.DAY_OF_MONTH
                ), holder
            )
        holder.display(day)
    }

    override fun getItemCount(): Int {
        return days.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    fun setOnDateClickListener(onDateClickListener: OnDateClickListener?) {
        this.onDateClickListener = onDateClickListener
    }

    fun setDateWatcher(dateWatcher: DateWatcher?) {
        this.dateWatcher = dateWatcher
    }

    override fun onCalenderDayClicked(year: Int, month: Int, day: Int) {
        if (onDateClickListener != null) onDateClickListener!!.onCalenderDayClicked(year, month, day)
    }

    inner class DayViewHolder internal constructor(
        itemView: View,
        resProvider: ResProvider,
        clickCallback: DateClickCallback
    ) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val intToMonth = arrayOf(
            "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5",
            "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
        )
        private val intToWeekday =
            arrayOf("Chủ Nhật", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7")
        private val resProvider: ResProvider
        private val clickCallback: DateClickCallback
        private var currentDay: CalendarDay? = null
        var dayView: LinearLayout
        var dayOfWeek: TextView
        var dayOfMonth: TextView
        var month: TextView
        fun display(day: CalendarDay?) {
            currentDay = day
            dayView.invalidate()
            setupData(day)
            setupStyles(day)
        }

        private fun setupStyles(day: CalendarDay?) {
            when (day!!.state) {
                DEFAULT -> {
                    dayView.background =
                        ContextCompat.getDrawable(dayView.context, resProvider.getDayBackground())
                    dayOfMonth.typeface = resProvider.getCustomFont()
                    dayOfWeek.typeface = resProvider.getCustomFont()
                    month.typeface = resProvider.getCustomFont()
                    dayOfMonth.setTextColor(resProvider.getDayTextColor())
                    dayOfWeek.setTextColor(resProvider.getWeekDayTextColor())
                    month.setTextColor(resProvider.getDayTextColor())
                }
                SELECTED -> {
                    dayView.background = ContextCompat.getDrawable(
                        dayView.context,
                        resProvider.getSelectedDayBackground()
                    )
                    dayOfMonth.typeface = resProvider.getCustomFont()
                    dayOfWeek.typeface = resProvider.getCustomFont()
                    month.typeface = resProvider.getCustomFont()
                    dayOfMonth.setTextColor(resProvider.getSelectedDayTextColor())
                    dayOfWeek.setTextColor(resProvider.getSelectedDayTextColor())
                    month.setTextColor(resProvider.getSelectedDayTextColor())
                }
            }
        }

        private fun setupData(day: CalendarDay?) {
            dayOfWeek.text = intToWeekday[day!!.day.get(Calendar.DAY_OF_WEEK)-1]
            dayOfMonth.text = String.valueOf(day.day.get(Calendar.DAY_OF_MONTH)).toString()
            month.text = intToMonth[day.day.get(Calendar.MONTH)]
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onClick(view: View) {
            val year: Int = currentDay!!.year
            val month: Int = currentDay!!.month
            val day: Int = currentDay!!.day.get(Calendar.DAY_OF_MONTH)
            clickCallback.onCalenderDayClicked(year, month, day)
            Log.i("Ngày tháng năm","${day} - ${month+1} - ${year}")
            notifyDataSetChanged()
        }




        init {
            this.resProvider = resProvider
            this.clickCallback = clickCallback
            dayView = itemView.findViewById(R.id.container)
            dayOfWeek = itemView.findViewById(R.id.dayOfWeekText)
            dayOfMonth = itemView.findViewById(R.id.dayOfMonthText)
            month = itemView.findViewById(R.id.MonthText)
            itemView.setOnClickListener(this)
        }
    }

    init {
        this.resProvider = resProvider
        initCalendar()
    }
}
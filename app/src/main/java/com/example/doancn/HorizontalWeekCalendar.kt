package com.example.doancn

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demotranghome.Adapters.CalendarWeekAdapter
import com.example.demotranghome.Interfaces.DateWatcher
import com.example.demotranghome.Interfaces.OnDateClickListener
import com.example.doancn.Fragments.Home.HomeFragment
import com.example.doancn.Interfaces.ResProvider
import kotlinx.android.synthetic.main.fragment_home.*


class HorizontalWeekCalendar : LinearLayoutCompat, ResProvider {
    private var contextthis: Context? = null
    private var customFont: String? = null
    private var defaultDayTextColor: Int = 0
    private var defaultWeekDayTextColor: Int = 0
    private var defaultBackground: Int = 0
    private var selectedTextColor: Int = 0
    private var selectedBackground: Int = 0

    private var adapter: CalendarWeekAdapter? = null


    constructor(context: Context?) : super(context!!) {
        this.contextthis = context
        initLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initLayout(context)
        loadStyle(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initLayout(context)
        loadStyle(context, attrs)
    }

    private fun init() {
        setupRecyclerView()
    }

    private fun initLayout(context: Context?) {
        this.contextthis = context
        orientation = HORIZONTAL
        inflate(context, R.layout.weekcalendar_week, this)
    }

    private fun loadStyle(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(
            attrs,
            R.styleable.WeekCalendar,
            R.attr.WeekCalendarStyleAttr,
            R.style.WeekCalendarStyle
        )
        customFont = ta.getString(R.styleable.WeekCalendar_customFont)
        defaultBackground = ta.getResourceId(R.styleable.WeekCalendar_dayBackground, 0)
        defaultDayTextColor = ta.getColor(R.styleable.WeekCalendar_dayTextColor, 0)
        defaultWeekDayTextColor = ta.getColor(R.styleable.WeekCalendar_weekDayTextColor, 0)
        selectedBackground = ta.getResourceId(R.styleable.WeekCalendar_selectedBackground, 0)
        selectedTextColor = ta.getColor(R.styleable.WeekCalendar_selectedDayTextColor, 0)
        ta.recycle()
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = getAdapter()
        recyclerView.scrollToPosition(15)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var mLastFirstVisibleItem = 0
            private var mIsScrollingUp = false
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val lm = recyclerView.layoutManager as LinearLayoutManager?
                val currentFirstVisibleItem = lm!!.findFirstVisibleItemPosition()

                if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                    mIsScrollingUp = false
                } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                    mIsScrollingUp = true
                }
                mLastFirstVisibleItem = currentFirstVisibleItem
                if (lm.findFirstVisibleItemPosition() < 10 && mIsScrollingUp) {
                    getAdapter()!!.addCalendarDays(false)
                    Log.i(
                        "onScrollChange",
                        "FirstVisibleItem: " + lm.findFirstVisibleItemPosition()
                    )
                    Log.i("onScrollChange", "new Size: " + getAdapter()!!.days.size)
                } else if (getAdapter()!!.days.size - 1 - lm.findLastVisibleItemPosition() < 10 && !mIsScrollingUp) {
                    getAdapter()!!.addCalendarDays(true)
                    Log.i("onScrollChange", "LastVisibleItem: " + lm.findLastVisibleItemPosition())
                    Log.i("onScrollChange", "new Size: " + getAdapter()!!.days.size)
                }
            }
        })
    }

    fun getAdapter(): CalendarWeekAdapter? {
        return if (adapter == null) createAdapter() else adapter
    }

    private fun createAdapter(): CalendarWeekAdapter? {
        adapter = CalendarWeekAdapter(this)
        return adapter
    }

    fun setOnDateClickListener(callback: OnDateClickListener?) {
        getAdapter()!!.setOnDateClickListener(callback)
    }

    fun setDateWatcher(dateWatcher: DateWatcher?) {
        getAdapter()!!.setDateWatcher(dateWatcher)
    }

    fun setCustomFont(customFont: String?) {
        this.customFont = customFont
    }

    fun setDefaultDayTextColor(defaultDayTextColor: Int) {
        this.defaultDayTextColor = defaultDayTextColor
    }

    fun setDefaultBackground(defaultBackground: Int) {
        this.defaultBackground = defaultBackground
    }

    fun setSelectedTextColor(selectedTextColor: Int) {
        this.selectedTextColor = selectedTextColor
    }

    fun setSelectedBackground(selectedBackground: Int) {
        this.selectedBackground = selectedBackground
    }

    override fun getSelectedDayBackground(): Int {
        return this.selectedBackground
    }

    override fun getSelectedDayTextColor(): Int {
        return selectedTextColor
    }

    override fun getDayTextColor(): Int {
        return defaultDayTextColor
    }

    override fun getWeekDayTextColor(): Int {
        return defaultWeekDayTextColor
    }

    override fun getDayBackground(): Int {
        return defaultBackground
    }

    override fun getCustomFont(): Typeface? {
        return if (customFont == null) {
            null
        } else try {
            ResourcesCompat.getFont(
                context!!, resources.getIdentifier(
                    customFont!!.split("\\.").toTypedArray()[0],
                    "font", context!!.packageName
                )
            )
        } catch (e: Exception) {
            null
        }
    }

    class Builder {
        private var view = 0
        fun setView(view: Int): Builder {
            this.view = view
            return this
        }

        fun init(frag: HomeFragment): HorizontalWeekCalendar {
            val calendar: HorizontalWeekCalendar = frag.horizontalCalendar
            calendar.init()
            return calendar
        }
    }
}
package com.example.doancn
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.navigationdrawer.CalendarAdapter
import com.example.navigationdrawer.CustomAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    //Calendar

    private val lastDayInCalendar = Calendar.getInstance(Locale.ENGLISH)
    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)

    private val currentDate = Calendar.getInstance(Locale.ENGLISH)
    private val currentDay = currentDate[Calendar.DAY_OF_MONTH]
    private val currentMonth = currentDate[Calendar.MONTH]
    private val currentYear = currentDate[Calendar.YEAR]

    private var selectedDay: Int = currentDay
    private var selectedMonth: Int = currentMonth
    private var selectedYear: Int = currentYear

    private val dates = ArrayList<Date>()

    //Navigation drawwer
    lateinit var toggle : ActionBarDrawerToggle




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    /*startActivity(Intent(this,LoginRegisterActivity::class.java))
        finish()*/

        // Custom Action Bar
        val actionBar : ActionBar? = getSupportActionBar()
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setLogo(R.drawable.ic_baseline_home_24)
        actionBar?.setDisplayUseLogoEnabled(true)

        //Calendar
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

        //Recycle View Today Class
        val data : MutableList<DataObject> = ArrayList()
        for(i : Int in 1..10){
            data.add(DataObject("Lớp thứ $i"))
        }

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)

        val adapter = CustomAdapter(data)

        rvList.layoutManager = layoutManager
        rvList.setHasFixedSize(true)
        rvList.adapter =adapter

        //Navigation drawer
        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mymenu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                onBackPressed()
                return true
            }
            else -> {
            }
        }
        //OnOption navigation drawwer
        if(toggle.onOptionsItemSelected(item))
        {
            return true
        }

        return super.onOptionsItemSelected(item)
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
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        calendar_recycler_view!!.layoutManager = layoutManager
        val calendarAdapter = CalendarAdapter(this, dates, currentDate, changeMonth)
        calendar_recycler_view!!.adapter = calendarAdapter


        when {
            currentPosition > 2 -> calendar_recycler_view!!.scrollToPosition(currentPosition - 3)
            maxDaysInMonth - currentPosition < 2 -> calendar_recycler_view!!.scrollToPosition(currentPosition)
            else -> calendar_recycler_view!!.scrollToPosition(currentPosition)
        }



        calendarAdapter.setOnItemClickListener(object : CalendarAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val clickCalendar = Calendar.getInstance()
                clickCalendar.time = dates[position]
                selectedDay = clickCalendar[Calendar.DAY_OF_MONTH]
            }
        })
    }
}
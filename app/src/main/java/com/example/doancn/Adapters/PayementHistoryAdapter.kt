package com.example.doancn.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.doancn.Models.PaymentHistory
import com.example.doancn.R
import kotlinx.android.synthetic.main.item_payment_history.view.*

class PayementHistoryAdapter(var context: Context, list : List<PaymentHistory>) : BaseAdapter() {

    private val _context = context
    private val listPayments = list

    override fun getCount(): Int {
        return listPayments.size
    }

    override fun getItem(p0: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getItemId(p0: Int): Long {
        return 0L
    }

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater : LayoutInflater = _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_payment_history,null)
        val payment = listPayments[p0]
        view.payment_history_date.text = payment.dateCreated
        view.payment_history_amount.text = payment.amount.toString()
        return view
    }
}
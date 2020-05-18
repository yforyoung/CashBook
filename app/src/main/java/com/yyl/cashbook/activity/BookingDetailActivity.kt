package com.yyl.cashbook.activity

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yyl.cashbook.R
import com.yyl.cashbook.adapter.TypeAdapter
import com.yyl.cashbook.`interface`.OnItemClickListener
import com.yyl.cashbook.database.Bill
import com.yyl.cashbook.model.CashbookModel
import com.yyl.cashbook.model.TypeModel
import com.yyl.cashbook.model.beans.BookingType
import com.yyl.cashbook.utils.getDateIntByDate
import kotlinx.android.synthetic.main.activity_booking_detail.*
import kotlinx.android.synthetic.main.layout_cacul.*
import java.text.DecimalFormat
import java.util.*


class BookingDetailActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var typeAdapter: TypeAdapter
    private val typeList = arrayListOf<BookingType>()
    private val typeModel = TypeModel()
    private val cashBookModel = CashbookModel()
    private lateinit var billData: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_detail)

        initView()
        initData();

    }

    private fun initData() {
        typeList.addAll(typeModel.getTypeList())
        typeAdapter.notifyDataSetChanged()
        val calendar: Calendar = Calendar.getInstance()
        tv_date.text = "${calendar.get(Calendar.MONTH) + 1}月${calendar.get(Calendar.DAY_OF_MONTH)}日"
        billData = calendar.time
    }

    private fun initView() {
        initType()
        initKeyboard()
        spinner_detail_type.adapter = ArrayAdapter<String>(
            this, R.layout.item_spinner,
            R.id.tv_item_spinner, resources.getStringArray(R.array.array_type)
        )

        iv_booking_back.setOnClickListener {
            finish()
        }
        tv_date.setOnClickListener {
            showDatePickDialog()
        }


    }

    private fun showDatePickDialog() {
        val calendar: Calendar = Calendar.getInstance()
        val datePickerDialog =
            DatePickerDialog(
                this, DatePickerDialog.THEME_HOLO_LIGHT,
                OnDateSetListener { view, year, month, dayOfMonth ->
                    if (year == calendar.get(Calendar.YEAR)) {
                        tv_date.text = "${month + 1}-${dayOfMonth}"
                    } else {
                        tv_date.text = "${year}-${month + 1}-${dayOfMonth}"
                    }
                    billData = Date(year - 1900, month, dayOfMonth)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        datePickerDialog.show()
    }

    private fun initType() {
        rv_booking_type.layoutManager = GridLayoutManager(this, 5, RecyclerView.VERTICAL, false)
        typeAdapter = TypeAdapter(typeList)
        rv_booking_type.adapter = typeAdapter
        typeAdapter.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(v: View, position: Int) {
                tv_booking_detail_type_name.text = typeList[position].name
            }

        }

    }


    private fun initKeyboard() {
        tv_0.setOnClickListener(this)
        tv_1.setOnClickListener(this)
        tv_2.setOnClickListener(this)
        tv_3.setOnClickListener(this)
        tv_4.setOnClickListener(this)
        tv_5.setOnClickListener(this)
        tv_6.setOnClickListener(this)
        tv_7.setOnClickListener(this)
        tv_8.setOnClickListener(this)
        tv_9.setOnClickListener(this)
        tv_delete.setOnClickListener(this)
        tv_decimal.setOnClickListener(this)
        tv_ok.setOnClickListener(this)
        tv_add.setOnClickListener(this)
        tv_minus.setOnClickListener(this)

        tv_booking_detail_equation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var result = 0.00
                if (s == null) {
                    tv_booking_detail_count.text = ""
                }
                if (s!!.isEmpty()) {
                    tv_booking_detail_count.text = ""
                    return
                }
                val sList = arrayListOf<StringBuilder>()
                for (c in s) {
                    when (c) {
                        '+' -> {
                            sList.add(StringBuilder(""))
                        }
                        '-' -> {
                            sList.add(StringBuilder("-"))
                        }
                        else -> {
                            if (sList.isEmpty()) {
                                sList.add(StringBuilder(""))
                            }
                            sList[sList.size - 1].append(c)
                        }
                    }
                }
                for (e in sList) {
                    if (e.isNotEmpty() && e.toString() != "-") {
                        result = result.plus(e.toString().toDouble())
                    }
                }
                tv_booking_detail_count.text = DecimalFormat("0.00").format(result)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_0, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_5, R.id.tv_6
                , R.id.tv_7, R.id.tv_8, R.id.tv_9 -> {
                tv_booking_detail_equation.append((v as TextView).text)
            }
            R.id.tv_decimal, R.id.tv_add, R.id.tv_minus -> {
                if (tv_booking_detail_equation.text.isNotEmpty()) {
                    val s = tv_booking_detail_equation.text[tv_booking_detail_equation.length() - 1]
                    if (s == '-' || s == '+' || s == '.') {
                        return
                    }
                    tv_booking_detail_equation.append((v as TextView).text)
                }
            }
            R.id.tv_delete -> {
                val s = tv_booking_detail_equation.text
                if (s.length > 1) {
                    tv_booking_detail_equation.text = s.subSequence(0, s.length - 1)
                } else if (s.length == 1) {
                    tv_booking_detail_equation.text = ""
                }
            }
            R.id.tv_ok -> {
                addCashbook()
            }
        }
    }

    //TODO 当前默认只给支出，账户默认支付宝… 🦅
    private fun addCashbook() {
        val s = tv_booking_detail_count.text.toString()
        if (s.isNotEmpty()) {
            val bill = Bill()
            bill.count = s.toDouble()
            bill.moneyOut = true /*spinner_detail_type.selectedItemPosition == 0*/
            bill.date = getDateIntByDate(billData)
            bill.type = tv_booking_detail_type_name.text.toString()
            bill.detail=et_remarks.text.toString()
            cashBookModel.addCashbook(bill)
            finish()
        }
    }

}

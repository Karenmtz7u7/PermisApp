package com.aplicacion.permisapp.activities

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.time.Month
import java.util.Calendar

class DatePiker (val listener: (day: Int, month:Int, year:Int)->Unit): DialogFragment(),
    DatePickerDialog.OnDateSetListener {
    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayofMonth: Int) {
        listener(dayofMonth, month+1, year)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c=Calendar.getInstance()
        val day=c.get(Calendar.DAY_OF_MONTH)
        val month=c.get(Calendar.MONTH)
        val year=c.get(Calendar.YEAR)

        val picker =DatePickerDialog(activity as Context,this, year, month, day)
        c.add(Calendar.DAY_OF_MONTH,-3)
        picker.datePicker.minDate = c.timeInMillis
        c.add(Calendar.MONTH,+6)
        c.add(Calendar.DAY_OF_MONTH,+3)
        picker.datePicker.maxDate = c.timeInMillis
        return picker
    }
}
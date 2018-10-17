package app.c2.android;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ernestnyumbu on 16/10/2018.
 */

public class DatePickerFragment extends DialogFragment {
    DatePickerDialog.OnDateSetListener ondateSet;
    private int year, month, day;

    public DatePickerFragment() {}

    public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
        ondateSet = ondate;
    }

    @SuppressLint("NewApi")
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        year = args.getInt("year");
        month = args.getInt("month");
        day = args.getInt("day");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar maxDate = Calendar.getInstance();
        maxDate.set(2012, 1, 1);
        maxDate.add(Calendar.YEAR, -16);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Dialog_MinWidth, ondateSet, year, month, day);
        dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        return dialog;
       }
}

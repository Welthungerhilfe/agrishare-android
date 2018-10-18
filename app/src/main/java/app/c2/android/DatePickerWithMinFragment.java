package app.c2.android;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;

/**
 * Created by ernestnyumbu on 16/10/2018.
 */

public class DatePickerWithMinFragment extends DialogFragment {
    DatePickerDialog.OnDateSetListener ondateSet;
    private int year, month, day;

    public DatePickerWithMinFragment() {}

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
        Calendar minDate = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Dialog_MinWidth, ondateSet, year, month, day);
        dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        return dialog;
       }
}

package com.example.expensemanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.Model.Data;
import com.example.expensemanager.Tool.DateTool;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class IncomeFragment extends Fragment {

    public IncomeFragment() {
        // Required empty public constructor
    }



    //Firebase database..

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;

    //Recyclerview..

    private RecyclerView recyclerView;

    //Text view

    private TextView incomeTotalSum;

    ///Update edit text.

    private EditText edtAmmount;
    private EditText edtType;
    private EditText edtNote;
    private TextView tvdate_choose;
    private TextView tvtime_choose;
    private DateTool dateTool = new DateTool();
    //button for update and delete;

    private Button btnUpdate;
    private Button btnDelete;

    //Dtaa item value

    private String type;
    private String note;
    private int amount;

    private String post_key;
    private String uidGet ="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview =  inflater.inflate(R.layout.fragment_income, container, false);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();
        uidGet = uid;
        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        incomeTotalSum=myview.findViewById(R.id.income_txt_result);
        recyclerView=myview.findViewById(R.id.recycler_id_income);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        mIncomeDatabase.orderByChild("date_stmp").limitToLast(100).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {

                int totlatvalue = 0;

                for (DataSnapshot mysanapshot:dataSnapshot.getChildren()){

                    Data data=mysanapshot.getValue(Data.class);
                    Log.d("CHKDATAS",data.getNote());
                    totlatvalue+=data.getAmmount();

                    String stTotalvale=String.valueOf(totlatvalue);

                    incomeTotalSum.setText(stTotalvale+".00");

                }

            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });


        return myview;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data,MyViewHolder>adapter=new FirebaseRecyclerAdapter<Data, MyViewHolder>
                (

                        Data.class,
                        R.layout.income_recycler_data,
                        MyViewHolder.class,
                        mIncomeDatabase

                ) {
            @Override
            protected void populateViewHolder(MyViewHolder ViewHolder, final Data model,final int i) {

                ViewHolder.setType(model.getType());
                ViewHolder.setNote(model.getNote());
                ViewHolder.setDate(model.getDate());
                ViewHolder.setAmmount(model.getAmmount());

                ViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key=getRef(i).getKey();

                        type=model.getType();
                        note=model.getNote();
                        amount=model.getAmmount();

                        updateDataItem();

                    }
                });


            }
        };

        recyclerView.setAdapter(adapter);


    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public MyViewHolder( View itemView) {
            super(itemView);
            mView=itemView;
        }

        private void setType(String type){
            TextView mType=mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }

        private void setNote(String note){
            TextView mNote=mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        private void setDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }

        private void setAmmount(int ammount){

            TextView mAmmount=mView.findViewById(R.id.ammount_txt_income);
            String stammount=String.valueOf(ammount);
            mAmmount.setText(stammount);
        }

    }


    private void updateDataItem() {

        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.update_data_item,null);
        mydialog.setView(myview);

        edtAmmount=myview.findViewById(R.id.ammount_edt);
        edtType=myview.findViewById(R.id.type_edt);
        edtNote=myview.findViewById(R.id.note_edt);
        tvdate_choose = myview.findViewById(R.id.tvdate_choose);
        tvtime_choose = myview.findViewById(R.id.tvtime_choose);

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        String getD = note;
        String subD = getD.substring(0, 10);
        String getT = note;
        String subT = getT.substring(11, 16);
        tvdate_choose.setText(subD);
        tvtime_choose.setText(subT);

        String newDateget = DateTool.formatdate(subD);

        edtAmmount.setText(String.valueOf(amount));
        edtAmmount.setSelection(String.valueOf(amount).length());

        btnUpdate=myview.findViewById(R.id.btn_upd_Update);
        btnDelete=myview.findViewById(R.id.btnuPD_Delete);
        final String[] dateStr = {""};
        final int[] day_hour = new int[1];
        final int[] day_min = new int[1];
        dateStr[0] = newDateget;
        String getH = note;
        String subH = getH.substring(11, 13);
        day_hour[0] = Integer.valueOf(subH);
        String getM = note;
        String subM = getM.substring(14, 16);
        day_min[0] = Integer.valueOf(subM);

        final AlertDialog dialog=mydialog.create();
        tvtime_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String getH = String.format("%02d", selectedHour);
                        String getM = String.format("%02d", selectedMinute);
                        tvtime_choose.setText(getH + ":" + getM);
                        day_hour[0] = selectedHour;
                        day_min[0] = selectedMinute;
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("เลือกเวลา");
                mTimePicker.show();
            }
        });
        tvdate_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        tvdate_choose.setText(String.format("%02d", dayOfMonth) + "/" + String.format("%02d", monthOfYear + 1) + "/" + year);
                        dateStr[0] = year+"/"+String.format("%02d", monthOfYear + 1)+"/"+String.format("%02d", dayOfMonth);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.setTitle("เลือกวันที่");
                datePickerDialog.show();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type=edtType.getText().toString().trim();
                note=edtNote.getText().toString().trim();

                String mdammount=String.valueOf(amount);

                mdammount=edtAmmount.getText().toString().trim();

                int myAmmount=Integer.parseInt(mdammount);

                String mDate= DateFormat.getDateInstance().format(new Date(dateStr[0]));
                String dateNew = tvdate_choose.getText().toString();
                String timeNew = tvtime_choose.getText().toString();
                Data data=new Data(myAmmount,type,dateNew+" "+timeNew+":00",post_key,mDate,dateTool.getTmFromDate(dateNew+" "+timeNew+":00"),day_hour[0],day_min[0],0);

                mIncomeDatabase.child(post_key).setValue(data);

                dialog.dismiss();

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mIncomeDatabase.child(post_key).removeValue();

                dialog.dismiss();

            }
        });
        dialog.show();
    }

}

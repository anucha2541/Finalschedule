package com.example.expensemanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


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


import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseFragment extends Fragment {

    public ExpenseFragment() {
        // Required empty public constructor
    }


    //Firebase database..

    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;

    //Recyclerview..

    private RecyclerView recyclerView;

    //Text view


    private TextView expenseSumResult;

    //Edt data item;

    private EditText edtAmmount;
    private EditText edtType;
    private EditText edtNote;

    //button for update and delete;

    private Button btnUpdate;
    private Button btnDelete;

    //Data veriable..

    private String type;
    private String note;
    private int ammount;
    private int link_type=0;

    private String post_key;
    private TextView tvdate_choose;
    private TextView tvtime_choose;
    private DateTool dateTool = new DateTool();
    private Spinner spin;
    private ArrayList<String> mSpin = new ArrayList<String>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_expense, container, false);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();
        Log.d("CHKERROR",uid);


        mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);
        expenseSumResult=myview.findViewById(R.id.expense_txt_result);
        recyclerView=myview.findViewById(R.id.recycler_id_expense);



        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());


        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.orderByChild("date_stmp").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("CHKERROR","ONDATE");
                int expenseSum = 0;

                for (DataSnapshot mysnapshot:dataSnapshot.getChildren()){

                    Data data=mysnapshot.getValue(Data.class);
                    expenseSum+=data.getAmmount();
                    String strExpensesum=String.valueOf(expenseSum);

                    expenseSumResult.setText(strExpensesum+".00");
                }

            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });




        return  myview;

    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d("CHKERROR","On Start");
        FirebaseRecyclerAdapter<Data,MyViewHolder>adapter=new FirebaseRecyclerAdapter<Data, MyViewHolder>
                (
                        Data.class,
                        R.layout.expense_recycler_data,
                        MyViewHolder.class,
                        mExpenseDatabase
                ) {
            @Override
            protected void populateViewHolder(MyViewHolder ViewHolder,final Data data,final int i) {

                ViewHolder.setType(data.getType());
                ViewHolder.setNote(data.getNote());
                ViewHolder.setDate(data.getDate());
                ViewHolder.setAmmount(data.getAmmount());

                ViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key=getRef(i).getKey();
                        type=data.getType();
                        note=data.getNote();
                        ammount=data.getAmmount();
                        link_type = data.getLink_type();
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

        private void setDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }

        private void setType(String type){
            TextView mType=mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }

        private void setNote(String note){
            TextView mNote=mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }

        private void setAmmount(int ammount){

            TextView mAmmount=mView.findViewById(R.id.ammount_txt_expense);
            String stammount=String.valueOf(ammount);
            mAmmount.setText(stammount);
        }


    }

    public void createSpinData(){
        mSpin.add("Personal leave/ลากิจ");
        mSpin.add("Vacation Leave/ลาพักผ่อน");
        mSpin.add("Sick leave/ลาป่วย");
        mSpin.add("Accident/อุบัติเหตุ");
        mSpin.add("Day Off/วันหยุดทั่วไป");
        mSpin.add("Maternity Leave/ลาคลอด");
    }

    private void updateDataItem(){

        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.update_data_item2,null);
        mydialog.setView(myview);

        edtAmmount=myview.findViewById(R.id.ammount_edt);
        edtNote=myview.findViewById(R.id.note_edt);
        edtType=myview.findViewById(R.id.type_edt);

        tvdate_choose = myview.findViewById(R.id.tvdate_choose);
        tvtime_choose = myview.findViewById(R.id.tvtime_choose);

        Spinner spin = myview.findViewById(R.id.spin);
        createSpinData();
        ArrayAdapter<String> adapterThai = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, mSpin);
        spin.setAdapter(adapterThai);
        //set data to edit text..
        spin.setSelection(link_type);
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

        edtAmmount.setText(String.valueOf(ammount));
        edtAmmount.setSelection(String.valueOf(ammount).length());

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
            public void onClick(View v) {

                type=edtType.getText().toString().trim();
                note=edtNote.getText().toString().trim();
                String stammount=String.valueOf(ammount);
                stammount=edtAmmount.getText().toString().trim();
                int intamount=Integer.parseInt(stammount);
                String mDate= DateFormat.getDateInstance().format(new Date(dateStr[0]));
                String dateNew = tvdate_choose.getText().toString();
                String timeNew = tvtime_choose.getText().toString();

                Data data=new Data(intamount,mSpin.get(spin.getSelectedItemPosition()),dateNew+" "+timeNew+":00",post_key,mDate,dateTool.getTmFromDate(dateNew+" "+timeNew+":00"),day_hour[0],day_min[0],spin.getSelectedItemPosition());
                mExpenseDatabase.child(post_key).setValue(data);

                dialog.dismiss();



            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mExpenseDatabase.child(post_key).removeValue();

                dialog.dismiss();

            }
        });

        dialog.show();





    }

}
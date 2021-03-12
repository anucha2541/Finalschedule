package com.example.expensemanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.expensemanager.Model.Data;
import com.example.expensemanager.Tool.DateTool;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
 * Use the {@link DashBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashBoardFragment extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DashBoardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashBoardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashBoardFragment newInstance(String param1, String param2) {
        DashBoardFragment fragment = new DashBoardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            
        }
    }


    //Floating button

    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //Floating button textview..

    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    //boolen

    private boolean isOpen=false;


    //Animation.

    private Animation FadOpen,FadeClose;

    //Dasbord income and expense result..

    private TextView totalIncomeResult;
    private TextView totalExpenseResult;

    //Firebase...

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    //Recycler view

    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;

    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private DateTool dateTool = new DateTool();
    private Spinner spin;
    private ArrayList<String> mSpin = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_dash_board, container, false);


        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);


        //Connect floating button to layout
        fab_main_btn=myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn=myview.findViewById(R.id.income_Ft_btn);
        fab_expense_btn=myview.findViewById(R.id.expense_ft_btn);

        //Connect floating text..

        fab_income_txt=myview.findViewById(R.id.income_ft_text);
        fab_expense_txt=myview.findViewById(R.id.expense_ft_text);

        //Total income and expense result set..

        totalIncomeResult=myview.findViewById(R.id.income_set_result);
        totalExpenseResult=myview.findViewById(R.id.expense_set_result);

        //Recycler

        mRecyclerIncome=myview.findViewById(R.id.recycler_income);
        mRecyclerExpense=myview.findViewById(R.id.recycler_expense);



        //Animation connect..

        FadOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadeClose= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);



        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();

                if (isOpen){

                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadeClose);
                    fab_expense_txt.startAnimation(FadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen=false;

                }else{
                    fab_income_btn.startAnimation(FadOpen);
                    fab_expense_btn.startAnimation(FadOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadOpen);
                    fab_expense_txt.startAnimation(FadOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen=true;

                }

            }
        });


        //Calculate total income..

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int totalsum = 0;

                for (DataSnapshot mysnap:dataSnapshot.getChildren()){

                    Data data=mysnap.getValue(Data.class);

                    totalsum+=data.getAmmount();

                    String stResult=String.valueOf(totalsum);

                    totalIncomeResult.setText(stResult+".00");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Calculate total expense..

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int totalsum = 0;

                for (DataSnapshot mysnapshot:dataSnapshot.getChildren()){

                    Data data=mysnapshot.getValue(Data.class);
                    totalsum+=data.getAmmount();

                    String strTotalSum=String.valueOf(totalsum);

                    totalExpenseResult.setText(strTotalSum+".00 ");

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Recycler

        LinearLayoutManager layoutManagerIncome=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);


        LinearLayoutManager layoutManagerExpense=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);



        return myview;
    }

    //Floating button animation

    private void ftAnimation(){

        if (isOpen){

            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadeClose);
            fab_expense_txt.startAnimation(FadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen=false;

        }else{
            fab_income_btn.startAnimation(FadOpen);
            fab_expense_btn.startAnimation(FadOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadOpen);
            fab_expense_txt.startAnimation(FadOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen=true;

        }


    }


    private void addData(){

        //Fab Botton income..

        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeDateInsert();

            }
        });

        //Fab Botton expense..

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert();

            }
        });

    }

    public void  incomeDateInsert(){

        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myviewm=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myviewm);
        AlertDialog dialog=mydialog.create();

        dialog.setCancelable(false);

        EditText edtAmmount=myviewm.findViewById(R.id.ammount_edt);
        EditText edtType=myviewm.findViewById(R.id.type_edt);
        EditText edtNote=myviewm.findViewById(R.id.note_edt);

        Button btnSave=myviewm.findViewById(R.id.btnSave);
        Button btnCancel=myviewm.findViewById(R.id.btnCancel);
        final String[] dateStr = {""};
        TextView tvdate_choose = myviewm.findViewById(R.id.tvdate_choose);
        TextView tvtime_choose = myviewm.findViewById(R.id.tvtime_choose);
        final int[] day_hour = new int[1];
        final int[] day_min = new int[1];
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
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Test",Toast.LENGTH_SHORT).show();
                String type=edtType.getText().toString().trim();
                String ammount=edtAmmount.getText().toString().trim();
                String note=edtNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)){
                    edtType.setError("Requrired Field..");
                    return;
                }

                if (TextUtils.isEmpty(ammount)){
                    edtAmmount.setError("Requrired Field..");
                    return;
                }

                int ourammontint=Integer.parseInt(ammount);

//                if (TextUtils.isEmpty(note)){
//                    edtNote.setError("Requrired Field..");
//                    return;
//                }

                String id=mIncomeDatabase.push().getKey();
                String dateNew = tvdate_choose.getText().toString();
                String timeNew = tvtime_choose.getText().toString();
                String mDate= DateFormat.getDateInstance().format(new Date(dateStr[0]+" "+timeNew+":00"));

                Data data=new Data(ourammontint,type,dateNew+" "+timeNew+":00",id,mDate,dateTool.getTmFromDate(dateNew+" "+timeNew+":00"),day_hour[0],day_min[0],0);

                mIncomeDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(),"Data ADDED",Toast.LENGTH_SHORT).show();


                ftAnimation();
                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void createSpinData(){
        mSpin.add("Personal leave/ลากิจ");
        mSpin.add("Vacation Leave/ลาพักผ่อน");
        mSpin.add("Sick leave/ลาป่วย");
        mSpin.add("Accident/อุบัติเหตุ");
        mSpin.add("Day Off/วันหยุดทั่วไป");
        mSpin.add("Maternity Leave/ลาคลอด");
    }

    public void expenseDataInsert(){

        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myviewm=inflater.inflate(R.layout.custom_layout_for_insertdata2,null);
        mydialog.setView(myviewm);

        final AlertDialog dialog= mydialog.create();

        dialog.setCancelable(false);


        EditText ammount=myviewm.findViewById(R.id.ammount_edt);
        EditText type=myviewm.findViewById(R.id.type_edt);
        EditText note=myviewm.findViewById(R.id.note_edt);

        Button btnSave=myviewm.findViewById(R.id.btnSave);
        Button btnCansel=myviewm.findViewById(R.id.btnCancel);

        TextView tvdate_choose = myviewm.findViewById(R.id.tvdate_choose);
        TextView tvtime_choose = myviewm.findViewById(R.id.tvtime_choose);
        Spinner spin = myviewm.findViewById(R.id.spin);
        createSpinData();
        ArrayAdapter<String> adapterThai = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, mSpin);
        spin.setAdapter(adapterThai);
        final String[] dateStr = {""};
        final int[] day_hour = new int[1];
        final int[] day_min = new int[1];

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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tmAmmount=ammount.getText().toString().trim();
                String tmtype=type.getText().toString().trim();
                String tmnote=note.getText().toString().trim();

                if (TextUtils.isEmpty(tmAmmount)){
                    ammount.setError("Required Field..");
                    return;
                }

                int inamount=Integer.parseInt(tmAmmount);

//                if (TextUtils.isEmpty(tmtype)){
//                    type.setError("Required Field..");
//                    return;
//                }

//                if (TextUtils.isEmpty(tmnote)){
//                    note.setError("Required Field..");
//                    return;
//                }

                String id=mExpenseDatabase.push().getKey();
                String dateNew = tvdate_choose.getText().toString();
                String timeNew = tvtime_choose.getText().toString();
                String mDate= DateFormat.getDateInstance().format(new Date(dateStr[0]+" "+timeNew+":00"));
                Data data=new Data(inamount,mSpin.get(spin.getSelectedItemPosition()),dateNew+" "+timeNew+":00",id,mDate,
                        dateTool.getTmFromDate(dateNew+" "+timeNew+":00"),day_hour[0],day_min[0],spin.getSelectedItemPosition());
                mExpenseDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(),"Data ADDED",Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();

            }
        });

        btnCansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data,IncomeViewHolder>incomeAdapter=new FirebaseRecyclerAdapter<Data, IncomeViewHolder>
                (
                        Data.class,
                        R.layout.dashboard_income,
                        DashBoardFragment.IncomeViewHolder.class,
                        mIncomeDatabase
                ) {
            @Override
            protected void populateViewHolder(IncomeViewHolder  ViewHolder, Data data, int i) {


                ViewHolder.setIncomeType(data.getType());
                ViewHolder.setIncomeAmmount(data.getAmmount());
                ViewHolder.setIncomeDate(data.getDate());


            }
        };
        mRecyclerIncome.setAdapter(incomeAdapter);

        FirebaseRecyclerAdapter<Data,ExpenseViewHolder>expenseAdapter=new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>
                (

                        Data.class,
                        R.layout.dashboart_expense,
                        DashBoardFragment.ExpenseViewHolder.class,
                        mExpenseDatabase

                ) {
            @Override
            protected void populateViewHolder(ExpenseViewHolder ViewHolder, Data data, int i) {

                ViewHolder.setExpenseType(data.getType());
                ViewHolder.setExpenseAmmount(data.getAmmount());
                ViewHolder.setmExpenseDate(data.getDate());

            }
        };


        mRecyclerExpense.setAdapter(expenseAdapter);


    }


    //For Income Data

    public static class IncomeViewHolder extends RecyclerView.ViewHolder{

        View mIncomeView;

        public IncomeViewHolder(View itemView) {
            super(itemView);
            mIncomeView=itemView;
        }

        public void setIncomeType(String type){

            TextView mtype=mIncomeView.findViewById(R.id.type_Income_ds);
            mtype.setText(type);
        }

        public void setIncomeAmmount(int ammount){

            TextView mAmmount=mIncomeView.findViewById(R.id.ammount_income_ds);

            String strAmmount=String.valueOf(ammount);

            mAmmount.setText(strAmmount);


        }

        public void setIncomeDate(String date){

            TextView mDate=mIncomeView.findViewById(R.id.date_income_ds);
            mDate.setText(date);

        }




    }

    //For expense data..

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{

        View mExpenseView;


        public ExpenseViewHolder(View itemView) {
            super(itemView);
            mExpenseView=itemView;
        }


        public void setExpenseType(String type) {
            TextView mtype=mExpenseView.findViewById(R.id.type_Expense_ds);
            mtype.setText(type);
        }

        public void setExpenseAmmount(int ammount){
            TextView mAmmount = mExpenseView.findViewById(R.id.ammount_Expense_ds);
            String strAmmount=String.valueOf(ammount);
            mAmmount.setText(strAmmount);

        }

        public void setmExpenseDate(String date){
            TextView mDate=mExpenseView.findViewById(R.id.date_expense_ds);
            mDate.setText(date);

        }

    }

}
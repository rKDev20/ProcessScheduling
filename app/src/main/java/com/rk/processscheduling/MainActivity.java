package com.rk.processscheduling;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.rk.processscheduling.algorithms.FCFS;
import com.rk.processscheduling.algorithms.PriorityBased;
import com.rk.processscheduling.algorithms.RoundRobin;
import com.rk.processscheduling.algorithms.SJF;
import com.rk.processscheduling.model.Input;
import com.rk.processscheduling.model.Output;
import com.rk.processscheduling.util.CpuQueueView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "processScheduling";
    String[] algoritms;
    Spinner algoClass;
    RadioGroup algoSubclass;
    RadioButton emptive;
    RadioButton nonEmptive;
    EditText quantumTime;
    TableLayout processTable;
    TableLayout summaryTable;
    TableLayout comparisionTable;
    CpuQueueView cpuQueueView;
    ConstraintLayout outputContainer;
    TextView avgTurnAround;
    TextView avgWaiting;
    TextView priorityInfo;
    EditText quantumComparision;
    Button add;
    Button remove;
    Button go;
    Button goComparision;
    ScrollView scrollView;

    List<TableRow> rows;
    Input[] input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        rows = new ArrayList<>();
        algoritms = new String[]{"Select algorithm", "FCFS", "SJF", "Priority", "Round robin"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_textview, algoritms);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        algoClass.setAdapter(adapter);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableRow newRow = (TableRow) LayoutInflater.from(MainActivity.this).inflate(R.layout.process_row, null);
                processTable.addView(newRow);
                rows.add(newRow);
                ((TextView) (newRow.findViewById(R.id.pid))).setText("p" + (processTable.getChildCount() - 1));
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int len = processTable.getChildCount();
                if (len > 2)
                    processTable.removeViewAt(len - 1);
                else
                    Toast.makeText(MainActivity.this, "At least one row required", Toast.LENGTH_LONG).show();
            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOuput();
            }
        });

        algoClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        setUpBlank();
                    case 1:
                        setUpFCFS();
                        break;
                    case 4:
                        setUpRoundRobin();
                        break;
                    case 3:
                        setUpPriority();
                        break;
                    case 2:
                        setUpSJF();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    void setUpFCFS() {
        algoSubclass.setVisibility(View.GONE);
        quantumTime.setVisibility(View.GONE);
        priorityInfo.setVisibility(VISIBLE);
    }

    void setUpPriority() {
        algoSubclass.setVisibility(VISIBLE);
        quantumTime.setVisibility(View.GONE);
        priorityInfo.setVisibility(View.GONE);
    }

    void setUpSJF() {
        algoSubclass.setVisibility(VISIBLE);
        quantumTime.setVisibility(View.GONE);
        priorityInfo.setVisibility(VISIBLE);
    }

    void setUpRoundRobin() {
        algoSubclass.setVisibility(View.GONE);
        priorityInfo.setVisibility(VISIBLE);
        quantumTime.setVisibility(VISIBLE);
    }

    void setUpBlank() {
        algoSubclass.setVisibility(View.GONE);
        quantumTime.setVisibility(View.GONE);
        priorityInfo.setVisibility(View.GONE);
    }

    private void initViews() {
        algoClass = findViewById(R.id.algo_class);
        algoSubclass = findViewById(R.id.algo_subclass);
        emptive = findViewById(R.id.emptive);
        nonEmptive = findViewById(R.id.non_emptive);
        quantumTime = findViewById(R.id.quantum);
        processTable = findViewById(R.id.processTable);
        summaryTable = findViewById(R.id.summaryTable);
        add = findViewById(R.id.add);
        remove = findViewById(R.id.remove);
        go = findViewById(R.id.go);
        cpuQueueView = findViewById(R.id.cpu_queue);
        avgTurnAround = findViewById(R.id.avgTurnAround);
        avgWaiting = findViewById(R.id.avgWaiting);
        comparisionTable = findViewById(R.id.comparisionTable);
        quantumComparision = findViewById(R.id.quantum2);
        goComparision = findViewById(R.id.go2);
        outputContainer = findViewById(R.id.outputContainer);
        priorityInfo = findViewById(R.id.textView6);
        scrollView = findViewById(R.id.scrollView2);
    }

    void getOuput() {
        int type = algoClass.getSelectedItemPosition();
        if (type == 0) {
            Toast.makeText(this, "Please select algorithm type", Toast.LENGTH_LONG).show();
        } else {
            int len = processTable.getChildCount();
            input = new Input[len - 1];
            for (int i = 1; i < len; i++) {
                TableRow row = (TableRow) processTable.getChildAt(i);
                Input in = new Input();
                String pname = ((EditText) row.findViewById(R.id.pid)).getText().toString();
                if (pname.compareTo("") == 0) {
                    Toast.makeText(this, "Please enter process name", Toast.LENGTH_LONG).show();
                    return;
                }
                in.setpName(pname);
                try {
                    in.setaTime(Integer.parseInt(((EditText) row.findViewById(R.id.atime)).getText().toString()));
                } catch (NumberFormatException e) {
                    final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
                    row.findViewById(R.id.atime).startAnimation(animShake);
                    row.findViewById(R.id.atime).requestFocus();
                    Toast.makeText(this, "Please enter an integer", Toast.LENGTH_LONG).show();
                    outputContainer.setVisibility(View.GONE);
                    return;
                }
                try {
                    in.setbTime(Integer.parseInt(((EditText) row.findViewById(R.id.btime)).getText().toString()));
                } catch (NumberFormatException e) {
                    final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
                    row.findViewById(R.id.btime).startAnimation(animShake);
                    row.findViewById(R.id.btime).requestFocus();
                    Toast.makeText(this, "Please enter an integer", Toast.LENGTH_LONG).show();
                    outputContainer.setVisibility(View.GONE);
                    return;
                }
                try {
                    in.setPriority(Integer.parseInt(((EditText) row.findViewById(R.id.priority)).getText().toString()));
                } catch (NumberFormatException e) {
                    if (type == 3) {
                        final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
                        row.findViewById(R.id.priority).startAnimation(animShake);
                        row.findViewById(R.id.priority).requestFocus();
                        Toast.makeText(this, "Please enter an integer", Toast.LENGTH_LONG).show();
                        outputContainer.setVisibility(View.GONE);
                        return;
                    }
                }
                input[i - 1] = in;
            }
            Output[] output = null;
            List<Integer> cpuQueue = null;
            int choice;
            switch (type) {
                case 1:
                    FCFS fcfs = new FCFS();
                    output = fcfs.getOutput(input);
                    cpuQueue = fcfs.getCpuQueue();
                    break;
                case 2:
                    SJF sjf = new SJF();
                    choice = algoSubclass.getCheckedRadioButtonId();
                    if (choice == R.id.non_emptive)
                        output = sjf.getNonPreemptive(input);
                    else
                        output = sjf.getPreemptive(input);
                    cpuQueue = sjf.getCpuQueue();
                    break;
                case 3:
                    PriorityBased priority = new PriorityBased();
                    choice = algoSubclass.getCheckedRadioButtonId();
                    if (choice == R.id.non_emptive)
                        output = priority.getNonPreemptive(input);
                    else
                        output = priority.getPreemptive(input);
                    cpuQueue = priority.getCpuQueue();
                    break;
                case 4:
                    RoundRobin roundRobin = new RoundRobin();
                    try {
                        output = roundRobin.getOutput(input, Integer.parseInt(quantumTime.getText().toString()));
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Please enter quantum time in integer", Toast.LENGTH_LONG).show();
                        return;
                    }
                    cpuQueue = roundRobin.getCpuQueue();
                    break;
            }
            outputContainer.setVisibility(VISIBLE);
            if (type == 3) {
                summaryTable.getChildAt(0).findViewById(R.id.priority).setVisibility(VISIBLE);
            } else
                summaryTable.getChildAt(0).findViewById(R.id.priority).setVisibility(View.GONE);
            for (int i = 0; i < len - 1; i++) {
                TableRow row = (TableRow) summaryTable.getChildAt(i + 1);
                if (row == null) {
                    row = (TableRow) LayoutInflater.from(this).inflate(R.layout.summary_row, null);
                    summaryTable.addView(row);
                }
                if (type == 3) {
                    row.findViewById(R.id.priority).setVisibility(VISIBLE);
                    ((TextView) (row.findViewById(R.id.priority))).setText(String.valueOf(input[i].getPriority()));
                } else
                    row.findViewById(R.id.priority).setVisibility(View.GONE);
                ((TextView) (row.findViewById(R.id.pid))).setText(input[i].getpName());
                ((TextView) (row.findViewById(R.id.atime))).setText(String.valueOf(input[i].getaTime()));
                ((TextView) (row.findViewById(R.id.btime))).setText(String.valueOf(input[i].getbTime()));
                ((TextView) (row.findViewById(R.id.turnaround))).setText(String.valueOf(output[i].getTurnAround()));
                ((TextView) (row.findViewById(R.id.waiting))).setText(String.valueOf(output[i].getWaiting()));
            }

            int len2 = summaryTable.getChildCount();
            if (len2 > len) {
                for (int i = len; i < len2; i++) {
                    summaryTable.removeViewAt(len);
                }
            }
            cpuQueueView.setUp(cpuQueue, input);
            avgWaiting.setText("Average waiting time : " + Output.getAverageWaitingTime(output));
            avgTurnAround.setText("Average turn around time : " + Output.getAverageTurnAround(output));

            TableRow row = (TableRow) comparisionTable.getChildAt(1);
            if (row == null) {
                row = (TableRow) LayoutInflater.from(this).inflate(R.layout.comparision_row, null);
                ((TextView) row.findViewById(R.id.type)).setText("FCFS");
                comparisionTable.addView(row);
            }
            FCFS fcfs = new FCFS();
            Output[] output1 = fcfs.getOutput(input);
            ((TextView) row.findViewById(R.id.waiting)).setText(String.valueOf(Output.getAverageWaitingTime(output1)));
            ((TextView) row.findViewById(R.id.turnaround)).setText(String.valueOf(Output.getAverageTurnAround(output1)));

            row = (TableRow) comparisionTable.getChildAt(2);
            if (row == null) {
                row = (TableRow) LayoutInflater.from(this).inflate(R.layout.comparision_row, null);
                ((TextView) row.findViewById(R.id.type)).setText("SJF (Preemptive)");
                comparisionTable.addView(row);
            }
            SJF sjf = new SJF();
            output1 = sjf.getPreemptive(input);
            ((TextView) row.findViewById(R.id.waiting)).setText(String.valueOf(Output.getAverageWaitingTime(output1)));
            ((TextView) row.findViewById(R.id.turnaround)).setText(String.valueOf(Output.getAverageTurnAround(output1)));

            row = (TableRow) comparisionTable.getChildAt(3);
            if (row == null) {
                row = (TableRow) LayoutInflater.from(this).inflate(R.layout.comparision_row, null);
                ((TextView) row.findViewById(R.id.type)).setText("SJF (Non-preemptive)");
                comparisionTable.addView(row);
            }
            output1 = sjf.getNonPreemptive(input);
            ((TextView) row.findViewById(R.id.waiting)).setText(String.valueOf(Output.getAverageWaitingTime(output1)));
            ((TextView) row.findViewById(R.id.turnaround)).setText(String.valueOf(Output.getAverageTurnAround(output1)));

            row = (TableRow) comparisionTable.getChildAt(4);
            if (row == null) {
                row = (TableRow) LayoutInflater.from(this).inflate(R.layout.comparision_row, null);
                ((TextView) row.findViewById(R.id.type)).setText("Priority (Preemptive)");
                comparisionTable.addView(row);
            }
            PriorityBased priority = new PriorityBased();
            output1 = priority.getPreemptive(input);
            ((TextView) row.findViewById(R.id.waiting)).setText(String.valueOf(Output.getAverageWaitingTime(output1)));
            ((TextView) row.findViewById(R.id.turnaround)).setText(String.valueOf(Output.getAverageTurnAround(output1)));

            row = (TableRow) comparisionTable.getChildAt(5);
            if (row == null) {
                row = (TableRow) LayoutInflater.from(this).inflate(R.layout.comparision_row, null);
                ((TextView) row.findViewById(R.id.type)).setText("Priority (Non-preemptive)");
                comparisionTable.addView(row);
            }
            output1 = priority.getNonPreemptive(input);
            ((TextView) row.findViewById(R.id.waiting)).setText(String.valueOf(Output.getAverageWaitingTime(output1)));
            ((TextView) row.findViewById(R.id.turnaround)).setText(String.valueOf(Output.getAverageTurnAround(output1)));

            row = (TableRow) comparisionTable.getChildAt(6);
            if (row == null) {
                row = (TableRow) LayoutInflater.from(this).inflate(R.layout.comparision_row, null);
                ((TextView) row.findViewById(R.id.type)).setText("Round robin");
                comparisionTable.addView(row);
            }
            RoundRobin robin = new RoundRobin();
            quantumComparision.setText(String.valueOf(3));
            output1 = robin.getOutput(input, 3);
            ((TextView) row.findViewById(R.id.waiting)).setText(String.valueOf(Output.getAverageWaitingTime(output1)));
            ((TextView) row.findViewById(R.id.turnaround)).setText(String.valueOf(Output.getAverageTurnAround(output1)));
            quantumComparision.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                        ((ScrollView) findViewById(R.id.scrollView2)).fullScroll(View.FOCUS_DOWN);
                }
            });
            goComparision.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setRoundRobinComparision();
                }
            });
            Toast.makeText(this,"Done",Toast.LENGTH_SHORT).show();
        scrollView.smoothScrollTo(0,summaryTable.getTop());
        }
    }

    void setRoundRobinComparision() {
        RoundRobin robin = new RoundRobin();
        TableRow row = (TableRow) comparisionTable.getChildAt(6);
        try {
            Output[] output1 = robin.getOutput(input, Integer.parseInt(quantumComparision.getText().toString()));
            ((TextView) row.findViewById(R.id.waiting)).setText(String.valueOf(Output.getAverageWaitingTime(output1)));
            ((TextView) row.findViewById(R.id.turnaround)).setText(String.valueOf(Output.getAverageTurnAround(output1)));
            Toast.makeText(MainActivity.this, "Comparision table updated", Toast.LENGTH_LONG).show();
        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity.this, "Please enter integer", Toast.LENGTH_LONG).show();
        } catch (Exception ignore) {
        }
    }
}
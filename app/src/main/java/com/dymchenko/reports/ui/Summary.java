package com.dymchenko.reports.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;

import com.dymchenko.reports.ActivityForPDF;
import com.dymchenko.reports.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Summary extends AppCompatActivity {

    Button create;
    EditText summary,
            project,
            component,
            version,
            author,
            assigned_to,
            steps_to_reproduce,
            result,
            expected_result;
    Spinner severity, priority, status;
    public String[] severities, priorities, statuses;
    String severity_text, priority_text, status_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        severities  = new String[]{ "S1 Блокирующая (Blocker)", "S2 Критическая (Critical)", "S3 Значительная (Major)", "S4 Незначительная (Minor)", "S5 Тривиальная (Trivial)"};
        priorities  = new String[]{ "P1 Высокий (High)", "P2 Средний (Medium)", "P3 Низкий (Low)"};
        statuses  = new String[]{ "New", "Incomplete", "Invalid", "Confirmed", "In Progress", "Fix Committed", "Fix Released"};

        summary = (EditText) findViewById(R.id.summary);
        project = findViewById(R.id.project);
        component = findViewById(R.id.component);
        version = findViewById(R.id.version);
        severity = findViewById(R.id.severity);
        priority = findViewById(R.id.priority);
        status = findViewById(R.id.status);
        author = findViewById(R.id.author);
        assigned_to = findViewById(R.id.assigned_to);
        steps_to_reproduce = findViewById(R.id.steps_to_reproduce);
        result = findViewById(R.id.result);
        expected_result = findViewById(R.id.expected_result);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, severities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        severity.setAdapter(adapter);
        severity.setSelection(1);
        severity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                severity_text = severities[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                severity_text = severities[1];

            }
        });
        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, statuses);
        status.setAdapter(adapter);
        status.setSelection(1);
        status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                status_text = statuses[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                status_text = statuses[1];

            }
        });

        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities);
        priority.setAdapter(adapter);
        priority.setSelection(1);
        priority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                priority_text = priorities[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                priority_text = priorities[1];

            }
        });
        create = findViewById(R.id.create_button);
        create.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Summary.this, ActivityForPDF.class);
                if (summary.getText().toString().matches("")) {
                        Toast.makeText(Summary.this, "Вы не ввели summary!", Toast.LENGTH_LONG).show();
                        summary.setError("Вы не ввели summary!");
                        return;
                }
                else if (project.getText().toString().matches("")){
                    Toast.makeText(Summary.this, "Вы не ввели project!", Toast.LENGTH_LONG).show();
                    project.setError("Вы не ввели project!");
                    return;
                }
                else if(component.getText().toString().matches("")){
                    Toast.makeText(Summary.this, "Вы не ввели component!", Toast.LENGTH_LONG).show();
                    component.setError("Вы не ввели component!");
                    return;
                }
                else if(version.getText().toString().matches("")){
                    Toast.makeText(Summary.this, "Вы не ввели version!", Toast.LENGTH_LONG).show();
                    version.setError("Вы не ввели verion!");
                    return;
                }
                else if(author.getText().toString().matches("")){
                    Toast.makeText(Summary.this, "Вы не ввели author!", Toast.LENGTH_LONG).show();
                    author.setError("Вы не ввели author!");
                    return;
                }
                else if(assigned_to.getText().toString().matches("")){
                    Toast.makeText(Summary.this, "Вы не ввели assigned to!", Toast.LENGTH_LONG).show();
                    assigned_to.setError("Вы не ввели assigned to!");
                    return;
                }
                else if(steps_to_reproduce.getText().toString().matches("")){
                    Toast.makeText(Summary.this, "Вы не ввели steps to reproduce!", Toast.LENGTH_LONG).show();
                    steps_to_reproduce.setError("Вы не ввели steps to reproduce!");
                    return;
                }
                else if(result.getText().toString().matches("")){
                    Toast.makeText(Summary.this, "Вы не ввели result!", Toast.LENGTH_LONG).show();
                    result.setError("Вы не ввели result!");
                    return;
                }
                else if(expected_result.getText().toString().matches("")){
                    Toast.makeText(Summary.this, "Вы не ввели expected result!", Toast.LENGTH_LONG).show();
                    expected_result.setError("Вы не ввели expected result!");
                    return;
                }
                else {
                intent.putExtra("summary", summary.getText());
                intent.putExtra("project", project.getText());
                intent.putExtra("component", component.getText());
                intent.putExtra("version", version.getText());
                intent.putExtra("severity", severity_text);
                intent.putExtra("priority", priority_text);
                intent.putExtra("status", status_text);
                intent.putExtra("author", author.getText());
                intent.putExtra("assigned_to", assigned_to.getText());
                intent.putExtra("steps_to_reproduce", steps_to_reproduce.getText());
                intent.putExtra("result", result.getText());
                intent.putExtra("expected_result", expected_result.getText());
                startActivity(intent);
                }
            }
        });
    }

}
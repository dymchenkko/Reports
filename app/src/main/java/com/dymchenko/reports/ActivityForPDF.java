package com.dymchenko.reports;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ActivityForPDF extends AppCompatActivity {

    TextView summary, project, component, version, severity, priority, status, author, assigned_to, steps_to_reproduce, result, expected_result;
    LinearLayout for_size;
    ScrollView pdf_layout;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_p_d_f);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        summary = findViewById(R.id.summary_create);
        project = findViewById(R.id.project_create);
        component = findViewById(R.id.component_create);
        version = findViewById(R.id.version_create);
        severity = findViewById(R.id.severity_create);
        priority = findViewById(R.id.priority_create);
        status = findViewById(R.id.status_create);
        author = findViewById(R.id.author_create);
        assigned_to = findViewById(R.id.assigned_to_create);
        steps_to_reproduce = findViewById(R.id.steps_to_reproduce_create);
        result = findViewById(R.id.result_create);
        expected_result = findViewById(R.id.expected_result_create);
        for_size = findViewById(R.id.ll_pdflayout);
        Bundle extras = getIntent().getExtras();
        generatePdf(extras.get("summary").toString(),
                extras.get("project").toString(),
                extras.get("component").toString(),
                extras.get("version").toString(),
                extras.get("severity").toString(),
                extras.get("priority").toString(),
                extras.get("status").toString(),
                extras.get("author").toString(),
                extras.get("assigned_to").toString(),
                extras.get("steps_to_reproduce").toString(),
                extras.get("result").toString(),
                extras.get("expected_result").toString());
        pdf_layout = findViewById(R.id.for_size);

    }
    public void onBackPressed() {
        Intent setIntent = new Intent(ActivityForPDF.this, MainActivity.class);
        startActivity(setIntent);
        return;
    }
    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void generatePdf(String _summary, String _project, String _component, String _version, String _severity, String _priority, String _status, String _author, String _assigned_to, String _steps_to_reproduce, String _result, String _expected_result) {
        summary.setText(_summary);
        project.setText(_project);
        component.setText(_component);
        version.setText(_version);
        severity.setText(_severity);
        priority.setText(_priority);
        status.setText(_status);
        author.setText(_author);
        assigned_to.setText(_assigned_to);
        steps_to_reproduce.setText(_steps_to_reproduce);
        result.setText(_result);
        expected_result.setText(_expected_result);
        PdfDocument mypdfdoc = new PdfDocument();
        PdfDocument.PageInfo mypageinfo1 = null;
        View contentQ1 = findViewById(R.id.ll_pdflayout);
        ViewGroup.LayoutParams params = for_size.getLayoutParams();
        contentQ1.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            mypageinfo1 = new PdfDocument.PageInfo.
                    Builder(getWindowManager().getDefaultDisplay().getWidth(), contentQ1.getMeasuredHeight(), 1).create();
        }

        PdfDocument.Page mypage1 = mypdfdoc.startPage(mypageinfo1);

        int measureWidth = View.MeasureSpec.makeMeasureSpec(mypage1.getCanvas().getWidth(), View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(mypage1.getCanvas().getHeight(), View.MeasureSpec.EXACTLY);

        contentQ1.measure(measureWidth, measuredHeight);
        contentQ1.layout(0, 0, measureWidth, measuredHeight);
        contentQ1.draw(mypage1.getCanvas());

        mypdfdoc.finishPage(mypage1);


        String path1 = this.getExternalFilesDir(null).getAbsolutePath();
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Reports/");

        File mFile = new File(folder, _summary+"_report.pdf");

        try {
            mypdfdoc.writeTo(new FileOutputStream(mFile));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }
        mypdfdoc.close();

    }

}
package com.dymchenko.reports;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class PdfActivity extends AppCompatActivity implements View.OnClickListener {
    private String path;
    private ImageView imgView;
    private Button btnPrevious, btnNext;
    private int currentPage = 0;
    private ImageButton btn_zoomin, btn_zoomout;

    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page curPage;
    private ParcelFileDescriptor descriptor;
    private float currentZoomLevel = 2;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        path = getIntent().getStringExtra("fileName");
        setTitle(getIntent().getStringExtra("keyName"));

        if (savedInstanceState != null) {
            currentPage = savedInstanceState.getInt(String.valueOf(currentPage), 0);
        }

        imgView = findViewById(R.id.imgView);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btn_zoomin = findViewById(R.id.zoomin);
        btn_zoomout = findViewById(R.id.zoomout);
        btnPrevious.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btn_zoomin.setOnClickListener(this);
        btn_zoomout.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override public void onStart() {
        super.onStart();
        try {
            openPdfRenderer();
            displayPage(currentPage);
        } catch (Exception e) {
            Toast.makeText(this, "PDF-файл защищен паролем.", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openPdfRenderer() {
        File file = new File(path);
        descriptor = null;
        pdfRenderer = null;
        try {
            descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(descriptor);
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка", Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void displayPage(int index) {
        if (pdfRenderer.getPageCount() <= index) return;
        if (curPage != null) curPage.close();
        curPage = pdfRenderer.openPage(index);
        int newWidth = (int) ( curPage.getWidth() );
        int newHeight =
                (int) ( curPage.getHeight()
                      );
        Bitmap bitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        Matrix matrix = new Matrix();
        float dpiAdjustedZoomLevel = currentZoomLevel * DisplayMetrics.DENSITY_MEDIUM
                / getResources().getDisplayMetrics().densityDpi;
        matrix.setScale(dpiAdjustedZoomLevel, dpiAdjustedZoomLevel);
        curPage.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        imgView.setImageBitmap(bitmap);
        int pageCount = pdfRenderer.getPageCount();
        btnPrevious.setEnabled(0 != index);
        btnNext.setEnabled(index + 1 < pageCount);
        btn_zoomout.setEnabled(currentZoomLevel != 2);
        btn_zoomin.setEnabled(currentZoomLevel != 12);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPrevious: {
                int index = curPage.getIndex() - 1;
                displayPage(index);
                break;
            }
            case R.id.btnNext: {
                int index = curPage.getIndex() + 1;
                displayPage(index);
                break;
            }
            case R.id.zoomout: {
                --currentZoomLevel;
                displayPage(curPage.getIndex());
                break;
            }
            case R.id.zoomin: {
                ++currentZoomLevel;
                displayPage(curPage.getIndex());
                break;
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (curPage != null) {
            outState.putInt(String.valueOf(currentPage), curPage.getIndex());
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override public void onStop() {
        try {
            closePdfRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void closePdfRenderer() throws IOException {
        if (curPage != null) curPage.close();
        if (pdfRenderer != null) pdfRenderer.close();
        if (descriptor != null) descriptor.close();
    }
}
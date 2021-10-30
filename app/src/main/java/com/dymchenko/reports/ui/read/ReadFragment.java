package com.dymchenko.reports.ui.read;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dymchenko.reports.PdfActivity;
import com.dymchenko.reports.R;
import com.dymchenko.reports.models.PdfFile;

import java.io.File;
import java.util.ArrayList;

public class ReadFragment extends Fragment {

    private ReadViewModel homeViewModel;
    private ListView listView;
    private static final int REQUEST_PERMISSION = 1;
    private ArrayList<PdfFile> list = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(ReadViewModel.class);
        View root = inflater.inflate(R.layout.fragment_read, container, false);
        listView = root.findViewById(R.id.listView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            initViews();
        }

        return root;
    }
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            initViews();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_PERMISSION);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initViews();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        finish();
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(
                                "Вы отказались предоставлять разрешение на чтение хранилища.\n\nЭто необходимо для работы приложения."
                                        + "\n\n"
                                        + "Нажмите \"Предоставить\", чтобы предоставить приложению разрешения.")
                                .setPositiveButton("Предоставить", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getActivity().getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("Отказаться", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                    }
                                });
                        builder.setCancelable(false);
                        builder.create().show();
                    }
                }
                break;
            }
        }
    }

    private void finish() {
    }

    private BaseAdapter adapter = new BaseAdapter() {
        @Override public int getCount() {
            return list.size();
        }

        @Override public PdfFile getItem(int i) {
            return (PdfFile) list.get(i);
        }

        @Override public long getItemId(int i) {
            return i;
        }

        @Override public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            if (v == null) {
                v = getLayoutInflater().inflate(R.layout.pdf_read_low, viewGroup, false);
            }

            PdfFile pdfFile = getItem(i);
            TextView name = v.findViewById(R.id.txtFileName);
            name.setText(pdfFile.getFileName());
            return v;
        }
    };
    private void initViews() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        initList(path);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), PdfActivity.class);
                intent.putExtra("keyName", list.get(i).getFileName());
                intent.putExtra("fileName", list.get(i).getFilePath());
                startActivity(intent);
            }
        });
    }

    private void initList(String path) {
        try {
            File file = new File(path);
            File[] fileList = file.listFiles();
            String fileName;
            for (File f : fileList) {
                if (f.isDirectory()) {
                    initList(f.getAbsolutePath());
                } else {
                    fileName = f.getName();
                    if (fileName.endsWith(".pdf")) {
                        list.add(new PdfFile(fileName, f.getAbsolutePath()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
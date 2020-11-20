package com.backpackvr.ppquest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class FileManager extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public FileManager() {
        // Required empty public constructor
    }

    public static FileManager newInstance(String param1, String param2) {
        FileManager fragment = new FileManager();
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

    static TextView debugTextFiles;
    TextView buttonGoBack;
    public static ArrayList<FileData> filesList = new ArrayList<FileData>();
    public static FileCellAdapter mAdapterFiles = new FileCellAdapter(filesList);

    public void BuildRecycleView(View view) {
        RecyclerView filesView = view.findViewById(R.id.filesView);
        filesView.setAdapter(mAdapterFiles);
        mAdapterFiles.setOnItemClickListener(new FileCellAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                Log.i("FRAGMENT", "pos: " + pos);
                OpenFile(filesList.get(pos).name);
            }
        });

    }
    public static String currentPath = Environment.getExternalStorageDirectory().toString();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_file_manager, container, false);
        debugTextFiles = view.findViewById(R.id.debugTextFiles);
        buttonGoBack= view.findViewById(R.id.buttonGoBack);

        BuildRecycleView(view);


        buttonGoBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GoUp();
            }
        });


        ListFiles();

        return view;
    }



    public void OpenFile(String fileName) {
        Log.i("TAG", "OpenFile(" + fileName + ")");
        String path = currentPath + "/" + fileName;
        File dir = new File(path);
        if (dir.isDirectory()) {
            currentPath = dir.toString();
            ListFiles();
        } else {
            String extension = fileName.substring(fileName.lastIndexOf("."));

            if (extension.equals(".apk")) {
                Log.i("TAG", "extension.equals(\".apk\")");
                APKInstaller.installAPK(dir.toString(),getActivity());
            }
        }
    }
    public void GoUp() {
        if (currentPath.equals(Environment.getExternalStorageDirectory().toString())) {
            return;
        }
        File dir = new File(currentPath);
        currentPath = dir.getParent().toString();
        ListFiles();
    }

    public void requestPermissions(Activity activity) {
        //Install Permission
        Log.e("Build.VERSION.SDK_INT", String.valueOf(Build.VERSION.SDK_INT));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!activity.getPackageManager().canRequestPackageInstalls()) {

                activity.startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", activity.getPackageName()))), 1234);
            }
        }

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        //Storage Permissions
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissions, 1);
        }
    }
    public void ListFiles() {
        requestPermissions(getActivity());
        Log.i("TAG", "ListFiles(" + currentPath + ")");
        String path = currentPath;
        debugTextFiles.setText(currentPath);

        File directory = new File(path);

        File[] files = directory.listFiles();
        filesList.clear();

        Log.v("Files",directory.exists()+"");
        Log.v("Files",directory.isDirectory()+"");
        Log.v("Files",directory.listFiles()+"");
        //Log.e("files", "files: " + files.toString());
        if (files != null) {

            for (int i = 0; i < files.length; i++)
            {

                FileData f = new FileData();
                f.name = files[i].getName();

                f.isFolder = files[i].isDirectory();
                filesList.add(f);
            }
            mAdapterFiles.notifyDataSetChanged();
        }
    }
}
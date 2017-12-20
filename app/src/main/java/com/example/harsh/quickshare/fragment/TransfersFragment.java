package com.example.harsh.quickshare.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.harsh.quickshare.R;
import com.example.harsh.quickshare.type.DeviceFile;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransfersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransfersFragment extends Fragment {

    private static final float TEXT_SIZE = 22f;

    private Map<DeviceFile, LinearLayout> fileViewMap = new HashMap<>();

    public TransfersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TransfersFragment.
     */
    public static TransfersFragment newInstance() {
        TransfersFragment fragment = new TransfersFragment();
        Bundle args = new Bundle();
        // Set the arguments here
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Get the arguments here
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transfers, container, false);
    }

    /**
     * Shows the commencement of file download.
     *
     * @param deviceFile File
     * @param parts No of parts of file to be downloaded concurrently
     */
    public void addDownloadView(final DeviceFile deviceFile, final Integer parts) {
        if (deviceFile == null || parts == null || parts <= 0) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout downloadsLayout = (LinearLayout) getActivity().findViewById(R.id.downloads_layout);

                LinearLayout fileDownloadLayout = generateFileDownloadLayout(deviceFile, parts);
                fileViewMap.put(deviceFile, fileDownloadLayout);
                downloadsLayout.addView(fileDownloadLayout);
            }
        });
    }

    // Generates layout for showing a file download
    private LinearLayout generateFileDownloadLayout(DeviceFile deviceFile, Integer parts) {
        LinearLayout fileDownloadLayout = new LinearLayout(getContext());
        TextView textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        textView.setText(deviceFile.getFileName());
        fileDownloadLayout.addView(textView);
        return fileDownloadLayout;
    }

    /**
     * Removes a file download from the view.
     *
     * @param deviceFile File
     */
    public void removeDownloadView(final DeviceFile deviceFile) {
        if (deviceFile == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout downloadsLayout = (LinearLayout) getActivity().findViewById(R.id.downloads_layout);

                LinearLayout fileDownloadLayout = fileViewMap.get(deviceFile);
                downloadsLayout.removeView(fileDownloadLayout);
            }
        });

    }
}

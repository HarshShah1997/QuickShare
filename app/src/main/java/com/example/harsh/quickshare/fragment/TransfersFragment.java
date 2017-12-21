package com.example.harsh.quickshare.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.harsh.quickshare.R;
import com.example.harsh.quickshare.type.DeviceFile;
import com.example.harsh.quickshare.type.TransferRequest;

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
    private Map<TransferRequest, ProgressBar> progressBarMap = new HashMap<>();

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
        return new TransfersFragment();
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
     */
    public void addDownloadView(final DeviceFile deviceFile) {
        if (deviceFile == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout downloadsLayout = (LinearLayout) getActivity().findViewById(R.id.downloads_layout);

                LinearLayout fileDownloadLayout = generateFileDownloadLayout(deviceFile);
                fileViewMap.put(deviceFile, fileDownloadLayout);
                downloadsLayout.addView(fileDownloadLayout);
            }
        });
    }

    // Generates layout for showing a file download
    private LinearLayout generateFileDownloadLayout(DeviceFile deviceFile) {
        LinearLayout fileDownloadLayout = new LinearLayout(getContext());
        fileDownloadLayout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        textView.setText(deviceFile.getFileName());
        fileDownloadLayout.addView(textView);

        LinearLayout downloadProgressLayout = new LinearLayout(getContext());
        fileDownloadLayout.addView(downloadProgressLayout);

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
                fileViewMap.remove(deviceFile);
            }
        });
    }

    /**
     * Adds layout for indicating part progress.
     *
     * @param transferRequest Transfer Request
     */
    public void addPartProgress(TransferRequest transferRequest) {
        if (transferRequest == null) {
            return;
        }
        DeviceFile deviceFile = transferRequest.getDeviceFile();
        if (fileViewMap.get(deviceFile) == null) {
            return;
        }
        LinearLayout downloadProgressLayout = (LinearLayout) fileViewMap.get(deviceFile).getChildAt(1);

        ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        progressBar.setIndeterminate(false);
        downloadProgressLayout.addView(progressBar);

        progressBarMap.put(transferRequest, progressBar);
    }

    /**
     * Updates the progress bar corresponding to the transfer request.
     *
     * @param transferRequest Transfer request
     * @param progress        An integer between 0 to 100 indicating progress percentage
     */
    public void updatePartProgress(TransferRequest transferRequest, int progress) {
        if (transferRequest == null || progress < 0 || progress > 100) {
            return;
        }
        ProgressBar progressBar = progressBarMap.get(transferRequest);
        if (progressBar == null) {
            return;
        }
        progressBar.setProgress(progress);
    }

    /**
     * Clears the memory used for displaying part progress
     *
     * @param transferRequest Transfer Request
     */
    public void removePartProgress(TransferRequest transferRequest) {
        if (transferRequest == null) {
            return;
        }
        progressBarMap.remove(transferRequest);
    }
}

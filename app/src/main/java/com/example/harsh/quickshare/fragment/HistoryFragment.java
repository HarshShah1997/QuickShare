package com.example.harsh.quickshare.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.harsh.quickshare.R;
import com.example.harsh.quickshare.constants.TransferStatus;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    private static final float TEXT_SIZE = 22f;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HistoryFragment.
     */
    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    /**
     * Adds layout to display a downloaded file history
     *
     * @param fileName File name
     * @param result File downloading result
     */
    public void addDownloadHistoryView(String fileName, String result) {
        if (fileName == null || result == null) {
            return;
        }
        LinearLayout rootLayout = (LinearLayout) getActivity().findViewById(R.id.history_root_layout);
        LinearLayout fileHistoryLayout = generateDownloadHistoryLayout(fileName, result);

        rootLayout.addView(fileHistoryLayout, 0);
    }

    // Generate layout for indicating a file download history
    private LinearLayout generateDownloadHistoryLayout(String fileName, String result) {
        LinearLayout fileHistoryLayout = new LinearLayout(getContext());

        ImageView transferTypeIndicator = new ImageView(getContext());
        transferTypeIndicator.setScaleType(ImageView.ScaleType.CENTER);
        transferTypeIndicator.setImageResource(R.mipmap.download_progress);
        fileHistoryLayout.addView(transferTypeIndicator);

        TextView textView = new TextView(getContext());
        textView.setText(fileName);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        fileHistoryLayout.addView(textView);

        ImageView resultView = new ImageView(getContext());
        resultView.setScaleType(ImageView.ScaleType.CENTER);

        if (result.equals(TransferStatus.SUCCESS)) {
            resultView.setImageResource(R.mipmap.download_success);
        } else if (result.equals(TransferStatus.FAILED)) {
            resultView.setImageResource(R.mipmap.download_failed);
        }
        fileHistoryLayout.addView(resultView);
        return fileHistoryLayout;
    }
}

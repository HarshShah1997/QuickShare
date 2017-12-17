package com.example.harsh.quickshare.fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.harsh.quickshare.R;
import com.example.harsh.quickshare.activity.MainActivity;
import com.example.harsh.quickshare.constants.DeviceFileType;
import com.example.harsh.quickshare.type.Device;
import com.example.harsh.quickshare.type.DeviceFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FilesFragment.FilesFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FilesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilesFragment extends Fragment {

    private static final int BORDER_HEIGHT = 1;
    private static final float DEVICE_TEXT_SIZE = 30f;
    private static final float DIRECTORY_TEXT_SIZE = 22f;
    private static final float FILE_TEXT_SIZE = 20f;
    private static final String EXPAND_INDICATOR = ">  ";
    private static final String COLLAPSE_INDICATOR = "v  ";
    private static final String SPACE_PER_INDENTATION = "  ";


    private FilesFragmentInteractionListener mListener;
    private LayoutInflater mLayoutInflator;

    public FilesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FilesFragment.
     */
    public static FilesFragment newInstance() {
        FilesFragment fragment = new FilesFragment();
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
        ((MainActivity) getActivity()).init();
        mLayoutInflator = inflater;
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_files, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FilesFragmentInteractionListener) {
            mListener = (FilesFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FilesFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Adds a device into this fragment's view
     *
     * @param device - The given device
     */
    public void addDeviceView(final Device device) {
        if (device == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout devicesListLayout = (LinearLayout) getActivity().findViewById(R.id.devices_list_layout);
                LinearLayout deviceLayout = generateDeviceLayout(device);
                View deviceTitle = generateDeviceTitle(device, deviceLayout);

                devicesListLayout.addView(deviceTitle);
                devicesListLayout.addView(generateEmptyLine());
                devicesListLayout.addView(deviceLayout);

                for (DeviceFile deviceFile : device.getDeviceFiles()) {
                    if (deviceFile.getFileType().equals(DeviceFileType.DIRECTORY)) {
                        LinearLayout directoryLayout = generateDirectoryLayout(deviceFile, 1);
                        View directoryTitle = generateDirectoryTitle(deviceFile, directoryLayout, 1);

                        deviceLayout.addView(directoryTitle);
                        deviceLayout.addView(generateEmptyLine());
                        deviceLayout.addView(directoryLayout);
                    } else {
                        View fileView = generateFileView(deviceFile, 1);
                        deviceLayout.addView(fileView);
                        deviceLayout.addView(generateEmptyLine());
                    }
                }

            }
        });
    }

    // Generates layout for a device
    private LinearLayout generateDeviceLayout(Device device) {
        LinearLayout deviceLayout = new LinearLayout(getContext());
        deviceLayout.setOrientation(LinearLayout.VERTICAL);
        deviceLayout.setVisibility(LinearLayout.GONE);
        return deviceLayout;
    }

    // Generate layout for device title
    private View generateDeviceTitle(Device device, final View deviceLayout) {
        LinearLayout deviceTitleLayout = new LinearLayout(getContext());

        final View indicator = generateIndicator(DEVICE_TEXT_SIZE);
        deviceTitleLayout.addView(indicator);

        TextView textView = new TextView(getContext());
        textView.setText(device.getDeviceName());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, DEVICE_TEXT_SIZE);
        deviceTitleLayout.addView(textView);

        deviceTitleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deviceLayout.getVisibility() == LinearLayout.GONE) {
                    ((TextView) (indicator)).setText(COLLAPSE_INDICATOR);
                    deviceLayout.setVisibility(LinearLayout.VISIBLE);
                } else {
                    ((TextView) (indicator)).setText(EXPAND_INDICATOR);
                    deviceLayout.setVisibility(LinearLayout.GONE);
                }
            }
        });
        return deviceTitleLayout;
    }

    // Generates layout for directory title
    private View generateDirectoryTitle(DeviceFile directory, final View directoryLayout, int level) {
        LinearLayout directoryTitleLayout = new LinearLayout(getContext());

        View indentation = generateIndentation(DIRECTORY_TEXT_SIZE, level);
        directoryTitleLayout.addView(indentation);

        final View indicator = generateIndicator(DIRECTORY_TEXT_SIZE);
        directoryTitleLayout.addView(indicator);

        TextView textView = new TextView(getContext());
        textView.setText(directory.getFileName());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, DIRECTORY_TEXT_SIZE);
        directoryTitleLayout.addView(textView);

        directoryTitleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (directoryLayout.getVisibility() == LinearLayout.GONE) {
                    ((TextView) (indicator)).setText(COLLAPSE_INDICATOR);
                    directoryLayout.setVisibility(LinearLayout.VISIBLE);
                } else {
                    ((TextView) (indicator)).setText(EXPAND_INDICATOR);
                    directoryLayout.setVisibility(LinearLayout.GONE);
                }
            }
        });
        return directoryTitleLayout;
    }

    // Generates layout of a directory, will recurse if another directory is found
    private LinearLayout generateDirectoryLayout(DeviceFile directory, int level) {
        LinearLayout directoryLayout = new LinearLayout(getContext());
        directoryLayout.setOrientation(LinearLayout.VERTICAL);
        directoryLayout.setVisibility(LinearLayout.GONE);

        for (DeviceFile deviceFile : directory.getChildren()) {
            if (deviceFile.getFileType().equals(DeviceFileType.DIRECTORY)) {
                LinearLayout childDirectoryLayout = generateDirectoryLayout(deviceFile, level + 1);
                View childDirectoryTitle = generateDirectoryTitle(deviceFile, childDirectoryLayout, level + 1);

                directoryLayout.addView(childDirectoryTitle);
                directoryLayout.addView(generateEmptyLine());
                directoryLayout.addView(childDirectoryLayout);
            } else {
                View fileView = generateFileView(deviceFile, level);
                directoryLayout.addView(fileView);
                directoryLayout.addView(generateEmptyLine());
            }
        }
        return directoryLayout;
    }

    // Generates view for a file
    private View generateFileView(DeviceFile deviceFile, int level) {
        LinearLayout fileLayout = new LinearLayout(getContext());

        View indentation = generateIndentation(FILE_TEXT_SIZE, level);
        fileLayout.addView(indentation);

        TextView textView = new TextView(getContext());
        textView.setText(deviceFile.getFileName() + "\n"
                + generateFileSizeString(deviceFile.getFileSize()));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, FILE_TEXT_SIZE);
        fileLayout.addView(textView);
        return fileLayout;
    }

    // Generates expand and collapse indicator
    private View generateIndicator(float size) {
        TextView indicator = new TextView(getContext());
        indicator.setText(EXPAND_INDICATOR);
        indicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        return indicator;
    }

    // Generates amount of spaces based on level
    private View generateIndentation(float size, int level) {
        TextView indentation = new TextView(getContext());
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            stringBuilder.append(SPACE_PER_INDENTATION);
        }
        indentation.setText(stringBuilder.toString());
        indentation.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        return indentation;
    }

    // Generates an empty horizontal line
    private View generateEmptyLine() {
        LinearLayout emptyLine = new LinearLayout(getContext());
        emptyLine.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, BORDER_HEIGHT));
        emptyLine.setBackgroundColor(Color.BLACK);
        return emptyLine;
    }

    // Converts file size in bytes to largest possible short form
    private String generateFileSizeString(long fileSize) {
        double size = fileSize;
        List<String> fileSizeSuffixes = new ArrayList<>(Arrays.asList("bytes", "KB", "MB", "GB", "TB"));
        int suffixPointer = 0;
        while (size > 1024) {
            suffixPointer++;
            size = size / 1024;
        }
        return String.format(Locale.ENGLISH, "%.2f %s", size, fileSizeSuffixes.get(suffixPointer));
    }

        /**
         * This interface must be implemented by activities that contain this
         * fragment to allow an interaction in this fragment to be communicated
         * to the activity and potentially other fragments contained in that
         * activity.
         * <p>
         * See the Android Training lesson <a href=
         * "http://developer.android.com/training/basics/fragments/communicating.html"
         * >Communicating with Other Fragments</a> for more information.
         */
    public interface FilesFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

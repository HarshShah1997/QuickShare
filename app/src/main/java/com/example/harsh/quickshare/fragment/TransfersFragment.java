package com.example.harsh.quickshare.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.harsh.quickshare.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransfersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransfersFragment extends Fragment {

    public TransfersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TransfersFragment.
     */
    // TODO: Rename and change types and number of parameters
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
}

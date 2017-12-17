package com.example.harsh.quickshare.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.example.harsh.quickshare.R;
import com.example.harsh.quickshare.constants.Command;
import com.example.harsh.quickshare.constants.DeviceFileType;
import com.example.harsh.quickshare.exception.BroadcastAddressException;
import com.example.harsh.quickshare.fragment.FilesFragment;
import com.example.harsh.quickshare.fragment.HistoryFragment;
import com.example.harsh.quickshare.fragment.TransfersFragment;
import com.example.harsh.quickshare.info.DevicesInfo;
import com.example.harsh.quickshare.type.Device;
import com.example.harsh.quickshare.type.DeviceFile;
import com.example.harsh.quickshare.util.BroadcastUtils;
import com.google.gson.Gson;

import java.io.File;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements FilesFragment.FilesFragmentInteractionListener, BroadcastUtils.IncomingPacketListener {

    private static final String TAG = "MainActivity";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private FilesFragment mFilesFragment;
    private TransfersFragment mTransfersFragment;
    private HistoryFragment mHistoryFragment;

    private BroadcastUtils broadcastUtils;

    private DevicesInfo devicesInfo;

    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Initializing the tablayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    mFilesFragment = FilesFragment.newInstance();
                    return mFilesFragment;
                case 1:
                    mTransfersFragment = TransfersFragment.newInstance();
                    return mTransfersFragment;
                case 2:
                    mHistoryFragment = HistoryFragment.newInstance();
                    return mHistoryFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.files_label);
                case 1:
                    return getString(R.string.transfers_label);
                case 2:
                    return getString(R.string.history_label);
            }
            return null;
        }
    }

    // Initializes models and network
    public void init() {
        broadcastUtils = new BroadcastUtils(this);
        gson = new Gson();
        devicesInfo = new DevicesInfo();

        broadcastUtils.startReceivingBroadcast();
        broadcastUtils.sendBroadcast(Command.NEW);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void processIncomingPacket(String hostAddress, String data) {
        if (hostAddress == null || hostAddress.isEmpty()) {
            Log.e(TAG, "Invalid host address");
            return;
        } else if (data == null) {
            Log.e(TAG, "Data is null");
            return;
        }
        if (data.startsWith(Command.NEW)) {
            sendPresence();
        } else if (data.startsWith(Command.PRESENT)) {
            handleNewDevice(hostAddress, data.substring(Command.PRESENT.length()));
        } else {
            Log.d(TAG, "Unknown data");
        }
    }

    // Sends device info, this method is called upon receiving new command
    private void sendPresence() {
        Device device = new Device();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        device.setDeviceName(prefs.getString(getString(R.string.pref_devicename_key), getString(R.string.pref_devicename_default)));
        device.setDeviceFiles(getFilesFromDevice());

        String json = gson.toJson(device, Device.class);
        broadcastUtils.sendBroadcast(Command.PRESENT + json);
    }

    // This method updates the list of devices present and calls method for updating view
    private void handleNewDevice(String hostAddress, String json) {
        Device newDevice = gson.fromJson(json, Device.class);
        newDevice.setDeviceIPAddress(hostAddress);
        devicesInfo.addDevice(newDevice);
        mFilesFragment.addDeviceView(newDevice);
    }

    /**
     * Populates the list of shared files from the device based on preferences
     *
     * @return List of files present in the device
     */
    private List<DeviceFile> getFilesFromDevice() {
        List<DeviceFile> filesList = new ArrayList<>();
        // TODO: Get directory based on preferences
        File directory = new File(Environment.getExternalStorageDirectory(), "/Music/");
        if (directory != null) {
            DeviceFile fileDirectory = new DeviceFile();
            fileDirectory.setFileName(directory.getName());
            fileDirectory.setPath(directory.getAbsolutePath());
            fileDirectory.setFileType(DeviceFileType.DIRECTORY);
            fileDirectory.setChildren(getFilesFromDirectory(directory));
            filesList.add(fileDirectory);
        }
        return filesList;
    }

    // Recursively gets files from a directory, sets the children if its a directory
    private List<DeviceFile> getFilesFromDirectory(File directory) {
        List<DeviceFile> filesList = new ArrayList<>();
        if (directory != null) {
            File[] files = directory.listFiles();
            for (int i = 0; files != null && i < files.length; i++) {
                DeviceFile deviceFile = new DeviceFile();
                deviceFile.setFileName(files[i].getName());
                deviceFile.setFileSize(files[i].length());
                deviceFile.setPath(files[i].getAbsolutePath());

                if (files[i].isDirectory()) {
                    deviceFile.setFileType(DeviceFileType.DIRECTORY);
                    deviceFile.setChildren(getFilesFromDirectory(files[i]));
                } else {
                    deviceFile.setFileType(DeviceFileType.FILE);
                }
                filesList.add(deviceFile);
            }
        }
        return filesList;
    }
}

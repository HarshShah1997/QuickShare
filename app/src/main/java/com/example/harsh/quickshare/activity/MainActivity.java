package com.example.harsh.quickshare.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.harsh.quickshare.R;
import com.example.harsh.quickshare.constants.Command;
import com.example.harsh.quickshare.fragment.FilesFragment;
import com.example.harsh.quickshare.fragment.HistoryFragment;
import com.example.harsh.quickshare.fragment.TransfersFragment;
import com.example.harsh.quickshare.info.DevicesInfo;
import com.example.harsh.quickshare.info.FileStatusInfo;
import com.example.harsh.quickshare.task.DownloadTask;
import com.example.harsh.quickshare.type.Device;
import com.example.harsh.quickshare.type.DeviceFile;
import com.example.harsh.quickshare.type.TransferRequest;
import com.example.harsh.quickshare.type.TransferResult;
import com.example.harsh.quickshare.util.BroadcastUtils;
import com.example.harsh.quickshare.util.DeviceUtils;
import com.example.harsh.quickshare.util.FileTransferUtils;
import com.google.gson.Gson;

import java.io.IOException;
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

    // Views
    private FilesFragment mFilesFragment;
    private TransfersFragment mTransfersFragment;
    private HistoryFragment mHistoryFragment;

    // Utilities
    private BroadcastUtils broadcastUtils;
    private DeviceUtils deviceUtils;
    private FileTransferUtils fileTransferUtils;
    private Gson gson;

    // Models
    private DevicesInfo devicesInfo;
    private FileStatusInfo fileStatusInfo;

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

    // Initializes utilities, models and network
    public void init() {
        broadcastUtils = new BroadcastUtils(this);
        deviceUtils = new DeviceUtils();
        fileTransferUtils = new FileTransferUtils();
        gson = new Gson();

        devicesInfo = new DevicesInfo();
        fileStatusInfo = new FileStatusInfo();

        broadcastUtils.startReceivingBroadcast();
        broadcastUtils.sendBroadcast(Command.NEW);
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
        } else if (data.startsWith(Command.SEND_FILE)) {
            sendFile(data.substring(Command.SEND_FILE.length()));
        } else {
            Log.d(TAG, "Unknown data");
        }
    }

    // Sends device info, this method is called upon receiving new command
    private void sendPresence() {
        Device device = new Device();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        device.setDeviceName(prefs.getString(getString(R.string.pref_devicename_key), getString(R.string.pref_devicename_default)));
        device.setDeviceFiles(deviceUtils.getFilesFromDevice(this));

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

    // Begins the procedure for sending the file
    private void sendFile(String json) {
        final TransferRequest transferRequest = gson.fromJson(json, TransferRequest.class);
        if (deviceUtils.getDeviceIPAddress(this).equals(transferRequest.getFromIPAddress())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        fileTransferUtils.sendFile(transferRequest);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }).start();
        }
    }

    @Override
    public void downloadFile(final DeviceFile deviceFile) {
        List<String> nodes = devicesInfo.getDeviceContainingFile(deviceFile);
        // TODO: Update here once multiple nodes functionality is added
        if (nodes.size() == 0) {
            Log.e(TAG, "This file is not present in any devices");
            return;
        }
        List<TransferRequest> transferRequests = generateTransferRequests(nodes, deviceFile);
        fileDownloadStarted(deviceFile, transferRequests.size());
        for (final TransferRequest transferRequest : transferRequests) {
            new DownloadTask(this, transferRequest, fileTransferUtils).execute();
            sendDownloadRequest(transferRequest);
        }
        // Update file status view here
    }

    // Generates download requests to be sent to the devices
    private List<TransferRequest> generateTransferRequests(List<String> nodes, DeviceFile deviceFile) {
        List<TransferRequest> transferRequests = new ArrayList<>();

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setDeviceFile(deviceFile);
        transferRequest.setFromIPAddress(nodes.get(0));
        transferRequest.setToIPAddress(deviceUtils.getDeviceIPAddress(this));
        transferRequest.setStartOffset(0L);
        transferRequest.setSize(deviceFile.getFileSize());

        transferRequests.add(transferRequest);
        return transferRequests;
    }

    // Broadcasts download request along with command
    private void sendDownloadRequest(TransferRequest transferRequest) {
        String json = gson.toJson(transferRequest);
        String message = Command.SEND_FILE + json;
        broadcastUtils.sendBroadcast(message);
    }

    // Updates the model and view to show the file download starting
    private void fileDownloadStarted(DeviceFile deviceFile, Integer parts) {
        fileStatusInfo.addFile(deviceFile, parts);
        mTransfersFragment.addDownloadView(deviceFile, parts);
    }

    /**
     * Updates the model and view to indicate starting of a part download
     *
     * @param transferRequest Transfer request
     */
    public void partDownloadStarted(TransferRequest transferRequest) {
        if (transferRequest == null) {
            return;
        }
        mTransfersFragment.addPartProgress(transferRequest);
    }

    /**
     * Updates the view indicating progress of download
     *
     * @param transferRequest Transfer request
     * @param progress An integer between 0 to 100 indicating percentage of progress
     */
    public void updatePartProgress(TransferRequest transferRequest, int progress) {
        if (transferRequest == null || progress < 0 || progress > 100) {
            return;
        }
        mTransfersFragment.updatePartProgress(transferRequest, progress);
    }

    /**
     * Updates the model and view to indicate completion of part download
     *
     * @param transferResult Transfer Result
     */
    public void partDownloadFinished(TransferResult transferResult) {
        if (transferResult == null) {
            return;
        }
        DeviceFile deviceFile = transferResult.getTransferRequest().getDeviceFile();
        fileStatusInfo.storeResult(deviceFile, transferResult.getTransferStatus());
        if (fileStatusInfo.isComplete(deviceFile)) {
            mTransfersFragment.removeDownloadView(deviceFile);
            // TODO: Update history here
        }
    }
}

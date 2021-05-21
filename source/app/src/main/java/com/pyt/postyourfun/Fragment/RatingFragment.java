package com.pyt.postyourfun.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.pyt.postyourfun.R;
import com.pyt.postyourfun.constants.PostYourFunApp;
import com.pyt.postyourfun.dynamoDBClass.DeviceMapper;
import com.pyt.postyourfun.dynamoDBClass.ParkInformationMapper;
import com.pyt.postyourfun.dynamoDBClass.ParkMapper;
import com.pyt.postyourfun.dynamoDBClass.ParkSocialMediaMapper;
import com.pyt.postyourfun.dynamoDBManager.tableTasks.DeviceRateDBManager;
import com.pyt.postyourfun.dynamoDBManager.tableTasks.ParkInformationDBManager;
import com.pyt.postyourfun.dynamoDBManager.tableTasks.ParkSocialMediaDBManager;
import com.pyt.postyourfun.social.SocialController;
import com.pyt.postyourfun.social.SocialControllerInterface;

import java.util.ArrayList;

public class RatingFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, SocialControllerInterface {

    private Spinner theme_selector, ride_selector;
    private Button fb_share_button;
    private RatingBar speed_rate, g_force_rate, adrenaline_rate;
    private TextView rate_park_textView;

    private String user_id;
    private String park_url, image_url;
    private String selected_park_name;
    private String selected_ride_name;
    private String general_rate_text = "Rate your fun at the ";
    private String general_fb_share_text = "I just finished the ride ";

    private ArrayList<ParkMapper> all_parks = new ArrayList<>();
    private ArrayList<DeviceMapper> all_rides = new ArrayList<>();
    private ArrayList<DeviceMapper> selected_rides = new ArrayList<>();

    private SocialController _socialController = null;

    private SharedPreferences _sharedPreference;

    public static RatingFragment newInstance() {
        RatingFragment fragment = new RatingFragment();

        return fragment;
    }

    public RatingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _socialController = SocialController.sharedInstance(getActivity(), this);
        _sharedPreference = getActivity().getSharedPreferences("user_info", 0);
        user_id = _sharedPreference.getString("user_id", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rating, container, false);

        theme_selector = (Spinner) view.findViewById(R.id.theme_selecter);
        ride_selector = (Spinner) view.findViewById(R.id.ride_selector);
        speed_rate = (RatingBar) view.findViewById(R.id.speed_rate);
        g_force_rate = (RatingBar) view.findViewById(R.id.g_force_rate);
        adrenaline_rate = (RatingBar) view.findViewById(R.id.adrenaline_rate);
        fb_share_button = (Button) view.findViewById(R.id.btn_fb_share);
        rate_park_textView = (TextView) view.findViewById(R.id.txt_thempark);

        fb_share_button.setOnClickListener(this);
        theme_selector.setOnItemSelectedListener(this);
        ride_selector.setOnItemSelectedListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fb_share:
                ParkMapper selected_park = all_parks.get(theme_selector.getSelectedItemPosition());
                fb_share(selected_park.getParkId());
                aws_rate_device(user_id,
                        selected_rides.get(ride_selector.getSelectedItemPosition()).getDeviceId(),
                        String.valueOf(speed_rate.getRating()),
                        String.valueOf(g_force_rate.getRating()),
                        String.valueOf(adrenaline_rate.getRating()),
                        "");
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.theme_selecter:
                String selected_park_id = all_parks.get(position).getParkId();
                ArrayList<String> selected_ride_names = new ArrayList<>();
                selected_rides.clear();
                for (int i = 0; i < all_rides.size(); i++) {
                    if (all_rides.get(i).getParkId().equals(selected_park_id)) {
                        selected_ride_names.add(all_rides.get(i).getName());
                        selected_rides.add(all_rides.get(i));
                    }
                }
                ArrayAdapter<String> ride_name_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, selected_ride_names);
                ride_name_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ride_selector.setAdapter(ride_name_adapter);

                selected_park_name = all_parks.get(theme_selector.getSelectedItemPosition()).getName();
                selected_ride_name = selected_rides.get(ride_selector.getSelectedItemPosition()).getName();
                rate_park_textView.setText(general_rate_text + selected_ride_name + " in " + selected_park_name);
                break;
            case R.id.ride_selector:
                selected_park_name = all_parks.get(theme_selector.getSelectedItemPosition()).getName();
                selected_ride_name = selected_rides.get(ride_selector.getSelectedItemPosition()).getName();
                rate_park_textView.setText(general_rate_text + selected_ride_name + " in " + selected_park_name);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (_socialController != null) {
            _socialController.onResume();
        }

        all_parks = (ArrayList<ParkMapper>) PostYourFunApp.all_parks.clone();
        all_rides = (ArrayList<DeviceMapper>) PostYourFunApp.all_rides.clone();
        initView();
    }

    public void initView() {
        if (all_rides.size() > 0) {
            selected_rides.clear();

            ArrayList<String> them_names = new ArrayList<>();
            for (int i = 0; i < all_parks.size(); i++) {
                them_names.add(all_parks.get(i).getName());
            }

            ArrayAdapter<String> theme_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, them_names);
            theme_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            theme_selector.setAdapter(theme_adapter);

            String selected_park_id = all_parks.get(theme_selector.getSelectedItemPosition()).getParkId();
            ArrayList<String> selected_ride_names = new ArrayList<>();
            for (int i = 0; i < all_rides.size(); i++) {
                if (all_rides.get(i).getParkId().equals(selected_park_id)) {
                    selected_ride_names.add(all_rides.get(i).getName());
                    selected_rides.add(all_rides.get(i));
                }
            }

            ArrayAdapter<String> ride_name_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, selected_ride_names);
            ride_name_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ride_selector.setAdapter(ride_name_adapter);
        }
    }

    public void fb_share(String selected_parkID) {
        new GetParkInformation().execute(selected_parkID);
    }

    public void aws_rate_device(String user_id, String device_id, String speed, String g_force, String adrenaline, String comment) {
        new Give_Rate_to_Device().execute(user_id, device_id, speed, g_force, adrenaline, comment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (_socialController != null) {
            _socialController.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (_socialController != null) {
            _socialController.onDestroy();
        }
    }

    @Override
    public void onSuccess(int type, int action) {

    }

    @Override
    public void onFailure(int type, int action) {

    }

    protected class Give_Rate_to_Device extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            DeviceRateDBManager.sharedInstance(getActivity());
            DeviceRateDBManager.insertRatings(params[0], params[1], params[2], params[3], params[4], params[5]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("Rate Device: ", "Success");
        }
    }

    protected class GetParkInformation extends AsyncTask<String, Void, ParkInformationMapper> {

        @Override
        protected ParkInformationMapper doInBackground(String... params) {

            ParkInformationDBManager.sharedInstance(getActivity());
            ParkInformationMapper result_park = ParkInformationDBManager.get_ParkInformation(params[0]);
            return result_park;
        }

        @Override
        protected void onPostExecute(ParkInformationMapper parkInformationMapper) {
            super.onPostExecute(parkInformationMapper);
            park_url = parkInformationMapper.getWebsite();
            image_url = parkInformationMapper.getImage_Url();
            new GetParkSocialMediaInfo().execute(parkInformationMapper.getPark_ID());
        }
    }

    protected class GetParkSocialMediaInfo extends AsyncTask<String, Void, ParkSocialMediaMapper> {

        @Override
        protected ParkSocialMediaMapper doInBackground(String... params) {
            ParkSocialMediaDBManager.sharedInstance(getActivity());
            ParkSocialMediaMapper result = ParkSocialMediaDBManager.get_Park_Social_Media(params[0]);
            return result;
        }

        @Override
        protected void onPostExecute(ParkSocialMediaMapper parkSocialMediaMapper) {
            super.onPostExecute(parkSocialMediaMapper);
//            I just finished the ride Helix in Liseberg. Speed 5/5, G-Force 5/5 and Adrenaline kick 5/5
            String comment_text =
                    general_fb_share_text + selected_ride_name + " in " + selected_park_name + ". Speed " + String.valueOf((int) (speed_rate.getRating())) +
                            "/5" + ", G-Force " + String.valueOf((int) (g_force_rate.getRating())) + "/5" + " and Adrenaline kick " +
                            String.valueOf((int) (adrenaline_rate.getRating())) + "/5.";
            _socialController.shareWithFaceBook(RatingFragment.this,
                    comment_text,
                    all_parks.get(theme_selector.getSelectedItemPosition()).getName(),
                    park_url,
                    image_url,
                    parkSocialMediaMapper.getFacebook());
        }
    }
}
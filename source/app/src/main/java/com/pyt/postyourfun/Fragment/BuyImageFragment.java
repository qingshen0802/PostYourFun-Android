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
import android.widget.Spinner;

import com.pyt.postyourfun.Image.ImageDownloadMangerInterface;
import com.pyt.postyourfun.R;
import com.pyt.postyourfun.Utils.UserImageSQLiteHelper;
import com.pyt.postyourfun.Utils.UsersImageModel;
import com.pyt.postyourfun.activity.ShowImageActivity;
import com.pyt.postyourfun.constants.Constants;
import com.pyt.postyourfun.constants.PostYourFunApp;
import com.pyt.postyourfun.dynamoDBClass.BucketMapper;
import com.pyt.postyourfun.dynamoDBClass.DeviceMapper;
import com.pyt.postyourfun.dynamoDBClass.ImageMapper;
import com.pyt.postyourfun.dynamoDBClass.ImageQueryMapper;
import com.pyt.postyourfun.dynamoDBClass.ParkInformationMapper;
import com.pyt.postyourfun.dynamoDBClass.ParkMapper;
import com.pyt.postyourfun.dynamoDBClass.ParkSocialMediaMapper;
import com.pyt.postyourfun.dynamoDBManager.tableTasks.BucketDBManager;
import com.pyt.postyourfun.dynamoDBManager.tableTasks.ImageDBManager;
import com.pyt.postyourfun.dynamoDBManager.tableTasks.ImageQueryDBManager;
import com.pyt.postyourfun.dynamoDBManager.tableTasks.ParkDBManager;
import com.pyt.postyourfun.dynamoDBManager.tableTasks.ParkInformationDBManager;
import com.pyt.postyourfun.dynamoDBManager.tableTasks.ParkSocialMediaDBManager;
import com.pyt.postyourfun.dynamoDBManager.tableTasks.RideDBManager;
import com.pyt.postyourfun.dynamoDBManager.tableTasks.UserImageDBmanager;

import java.util.ArrayList;
import java.util.List;

import static com.pyt.postyourfun.constants.PostYourFunApp.createGUID;
import static com.pyt.postyourfun.constants.PostYourFunApp.getCurrentTimDate;

public class BuyImageFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, ImageDownloadMangerInterface {

    private Spinner park_spinner, ride_spinner;
    private Button getImagesButton;

    private ParkMapper selectedPark = new ParkMapper();
    private ArrayList<ParkMapper> all_parks = new ArrayList<>();

    private DeviceMapper selected_ride = new DeviceMapper();
    private ArrayList<DeviceMapper> all_rides = new ArrayList<>();
    private List<DeviceMapper> selected_rides = new ArrayList<>();

    private String get_image_name;

    private String full_image_url = "";
    private String thumbnail_image_url = "";
    private String userId;
    private SharedPreferences _sharedPreference;
    private UserImageSQLiteHelper dbHelper;

    private ImageDownloadMangerInterface _interface = this;

    public static BuyImageFragment newInstance() {
        BuyImageFragment fragment = new BuyImageFragment();

        return fragment;
    }

    public BuyImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _sharedPreference = getActivity().getSharedPreferences("user_info", 0);
        userId = _sharedPreference.getString("user_id", "");
        dbHelper = new UserImageSQLiteHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buy_image, container, false);

        park_spinner = (Spinner) view.findViewById(R.id.park_spinner);
        ride_spinner = (Spinner) view.findViewById(R.id.ride_spinner);
        getImagesButton = (Button) view.findViewById(R.id.images);

        getImagesButton.setOnClickListener(this);

        park_spinner.setOnItemSelectedListener(this);
        ride_spinner.setOnItemSelectedListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PostYourFunApp.all_rides.size() == 0) {
            showProgressDialog();
            new GetAllPark().execute();
            new GetAllRides().execute();
        } else {
            all_parks = PostYourFunApp.all_parks;
            all_rides = PostYourFunApp.all_rides;
            initView();
        }
//        String[] parks = new String[all_parks.size()];
    }

    @Override
    public void onClick(View v) {
        int view_id = v.getId();
        switch (view_id) {
//		case R.id.btn_buy_image:
//			if (!full_image_url.equals("")) {
//				PaymentController.sharedInstance().buyImage(BuyImageFragment.this, 8.0f, "EUR", full_image_url, _interface);
////                    ImageDownloadManager.getSharedInstance().downloadImage(full_image_url, getActivity(), _interface);
//			} else {
//				Toast.makeText(getActivity(), "Please get image first.", Toast.LENGTH_SHORT).show();
//			}
//			break;
//		case R.id.btn_get_image:
//			String device_id;
//			device_id = selected_rides.get(ride_spinner.getSelectedItemPosition()).getDeviceId();
//			if (image_number.getText().toString().equals("")) {
//				Toast.makeText(getActivity(), "Enter image number", Toast.LENGTH_SHORT).show();
//			} else {
//				new GetImageQuery().execute(device_id);
//			}
//			break;
            case R.id.images:
                Intent intent = new Intent(getActivity(), ShowImageActivity.class);
                ArrayList<String> deviceIdList = new ArrayList<>();

                DeviceMapper selectedDeviceMapper = selected_rides.get(ride_spinner.getSelectedItemPosition());
                if (selectedDeviceMapper.getHasMonitor()) {
                    deviceIdList.add(selected_rides.get(ride_spinner.getSelectedItemPosition()).getDeviceId());
                } else {
                    for (DeviceMapper mapper : selected_rides)
                        deviceIdList.add(mapper.getDeviceId());
                }

                intent.putStringArrayListExtra(ShowImageActivity.EXTRA_DEVICE_ID, deviceIdList);
                intent.putExtra(ShowImageActivity.EXTRA_SOLD, selectedDeviceMapper.getImageSold());
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.park_spinner:
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
                ride_spinner.setAdapter(ride_name_adapter);

                break;
            case R.id.ride_spinner:
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void initView() {
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < all_parks.size(); i++) {
            names.add(all_parks.get(i).getName());
//            Log.d("Park Name: ", names.get(i));
        }
//        for (int i = 0; i < all_parks.size(); i++){
//            parks[i] = all_parks.get(i).getName();
//        }
        ArrayAdapter<String> park_name_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, names);
        park_name_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        park_spinner.setAdapter(park_name_adapter);

        String selected_park_id = all_parks.get(park_spinner.getSelectedItemPosition()).getParkId();
        ArrayList<String> selected_ride_names = new ArrayList<>();
        for (int i = 0; i < all_rides.size(); i++) {
            if (all_rides.get(i).getParkId().equals(selected_park_id)) {
                selected_ride_names.add(all_rides.get(i).getName());
            }
        }

        ArrayAdapter<String> ride_name_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, selected_ride_names);
        ride_name_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ride_spinner.setAdapter(ride_name_adapter);
    }

    @Override
    public void onSuccessImageDownload(Boolean isSuccess, String image_Url, String path, String thumbUrl) {
        String transactionId = createGUID();
        String imageId = image_Url.substring(image_Url.lastIndexOf("/") + 1, image_Url.length() - 4);
        String dateTime = getCurrentTimDate(System.currentTimeMillis(), "dd.MM.yyyy");

        new InsertTransaction().execute(transactionId, userId, imageId, image_Url, dateTime, thumbUrl);

        UsersImageModel imageModel = new UsersImageModel();
        imageModel.setTransactionId(transactionId);
        imageModel.setUserId(userId);
        imageModel.setImageId(imageId);
        imageModel.setImageUrl(image_Url);
        imageModel.setDateTime(dateTime);
        imageModel.setThumbImageUrl(thumbnail_image_url);
        imageModel.setLocalPath(path);
        dbHelper.addImage(imageModel);

        ArrayList<UsersImageModel> result = new ArrayList<>();
        result = dbHelper.getAllImages();
    }

    protected class GetPark extends AsyncTask<String, Void, ParkMapper> {

        @Override
        protected ParkMapper doInBackground(String... params) {
            ParkDBManager.sharedInstance(getActivity());
            ParkMapper result = ParkDBManager.getPark(params[0]);
            return result;
        }

        @Override
        protected void onPostExecute(ParkMapper parkMapper) {
            super.onPostExecute(parkMapper);
            selectedPark = parkMapper;
        }
    }

    protected class GetAllPark extends AsyncTask<Void, Void, ArrayList<ParkMapper>> {

        @Override
        protected ArrayList<ParkMapper> doInBackground(Void... params) {
            ParkDBManager.sharedInstance(getActivity());
            ArrayList<ParkMapper> result = ParkDBManager.get_all_Parks();
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<ParkMapper> parkMappers) {
            super.onPostExecute(parkMappers);
            all_parks = parkMappers;
            PostYourFunApp.all_parks = all_parks;
            ArrayList<String> names = new ArrayList<>();
            for (int i = 0; i < all_parks.size(); i++) {
                names.add(all_parks.get(i).getName());
                Log.d("Park Name: ", names.get(i));
            }
//        for (int i = 0; i < all_parks.size(); i++){
//            parks[i] = all_parks.get(i).getName();
//        }
            ArrayAdapter<String> park_name_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, names);
            park_name_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            park_spinner.setAdapter(park_name_adapter);
        }
    }

    public class GetAllParkInfos extends AsyncTask<Void, Void, ArrayList<ParkInformationMapper>> {

        @Override
        protected ArrayList<ParkInformationMapper> doInBackground(Void... params) {
            ParkInformationDBManager.sharedInstance(getActivity());
            ArrayList<ParkInformationMapper> result = ParkInformationDBManager.get_All_ParkInformation();
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<ParkInformationMapper> parkInformationMappers) {
            super.onPostExecute(parkInformationMappers);
        }
    }

    public class GetAllParkSocialInfos extends AsyncTask<Void, Void, ArrayList<ParkSocialMediaMapper>> {

        @Override
        protected ArrayList<ParkSocialMediaMapper> doInBackground(Void... params) {
            ParkSocialMediaDBManager.sharedInstance(getActivity());
            ArrayList<ParkSocialMediaMapper> result = ParkSocialMediaDBManager.get_All_Park_Social_Media();
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<ParkSocialMediaMapper> parkSocialMediaMappers) {
            super.onPostExecute(parkSocialMediaMappers);
        }
    }

    protected class GetAllRides extends AsyncTask<Void, Void, ArrayList<DeviceMapper>> {

        @Override
        protected ArrayList<DeviceMapper> doInBackground(Void... params) {
            RideDBManager.sharedInstance(getActivity());
            ArrayList<DeviceMapper> result = RideDBManager.get_all_rides();
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<DeviceMapper> deviceMappers) {
            super.onPostExecute(deviceMappers);
            all_rides = deviceMappers;
            PostYourFunApp.all_rides = all_rides;
            String selected_park_id = all_parks.get(park_spinner.getSelectedItemPosition()).getParkId();
            selected_rides.clear();
            ArrayList<String> selected_ride_names = new ArrayList<>();
            for (int i = 0; i < all_rides.size(); i++) {
                if (all_rides.get(i).getParkId().equals(selected_park_id)) {
                    selected_ride_names.add(all_rides.get(i).getName());
                    selected_rides.add(all_rides.get(i));
                }
            }

            ArrayAdapter<String> ride_name_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, selected_ride_names);
            ride_name_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ride_spinner.setAdapter(ride_name_adapter);

            dismissProgressDialog();
        }
    }

    protected class GetImageQuery extends AsyncTask<String, Void, List<ImageQueryMapper>> {
        @Override
        protected List<ImageQueryMapper> doInBackground(String... params) {

            ImageQueryDBManager.sharedInstance(getActivity());
            List<ImageQueryMapper> mappers = ImageQueryDBManager.getImage(params[0]);
            return mappers;
        }

        @Override
        protected void onPostExecute(List<ImageQueryMapper> mappers) {
            super.onPostExecute(mappers);
            if (mappers != null && !mappers.isEmpty()) {
                new GetImage().execute(mappers.get(0).getImage_id());
            }
        }
    }

    protected class GetImage extends AsyncTask<String, Void, ImageMapper> {

        @Override
        protected ImageMapper doInBackground(String... params) {
            ImageDBManager.sharedInstance(getActivity());
//			ImageMapper result = ImageDBManager.getImage(params[0]);
//			return result;
            return null;
        }

        @Override
        protected void onPostExecute(ImageMapper imageMapper) {
            super.onPostExecute(imageMapper);
            if (imageMapper != null) {
                thumbnail_image_url = Constants.IMAGE_CONSTANT_URL + imageMapper.getRegion() + ".thumbs/tn_" + imageMapper.getImageName();
                Log.d("Image URL:", Constants.IMAGE_CONSTANT_URL + imageMapper.getRegion() + ".thumbs/tn_" + imageMapper.getImageName());
//				image_thumb.setImageUrl(thumbnail_image_url);
                full_image_url = Constants.IMAGE_CONSTANT_URL + imageMapper.getRegion() + ".pictures/" + imageMapper.getImageName();
            }
        }
    }

    protected class InsertTransaction extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            UserImageDBmanager.sharedInstance(getActivity());
            UserImageDBmanager.insertUserImage(params[0], params[1], params[2], params[3], params[4], params[5]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    protected class GetBucket extends AsyncTask<String, Void, BucketMapper> {
        @Override
        protected BucketMapper doInBackground(String... params) {
            BucketDBManager.sharedInstance(getActivity());
            BucketMapper result_Bucket = BucketDBManager.get_Bucket(params[0]);
            return result_Bucket;
        }

        @Override
        protected void onPostExecute(BucketMapper bucketMapper) {
            super.onPostExecute(bucketMapper);
        }
    }
}

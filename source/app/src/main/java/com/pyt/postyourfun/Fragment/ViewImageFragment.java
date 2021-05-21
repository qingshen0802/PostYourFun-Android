package com.pyt.postyourfun.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pyt.postyourfun.Adapter.GridViewImageAdapter;
import com.pyt.postyourfun.Adapter.GridViewImageInterface;
import com.pyt.postyourfun.Image.ImageDownloadManager;
import com.pyt.postyourfun.Image.ImageDownloadMangerInterface;
import com.pyt.postyourfun.R;
import com.pyt.postyourfun.Utils.UserImageSQLiteHelper;
import com.pyt.postyourfun.constants.Constants;
import com.pyt.postyourfun.dynamoDBClass.ParkSocialMediaMapper;
import com.pyt.postyourfun.dynamoDBClass.UserImageMapper;
import com.pyt.postyourfun.dynamoDBManager.tableTasks.ParkSocialMediaDBManager;
import com.pyt.postyourfun.dynamoDBManager.tableTasks.UserImageDBmanager;
import com.pyt.postyourfun.social.SocialController;
import com.pyt.postyourfun.social.SocialControllerInterface;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Photo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewImageFragment extends BaseFragment implements View.OnClickListener, GridViewImageInterface, SocialControllerInterface, ImageDownloadMangerInterface {

    private GridView imageGrid;
    private Button btnShareFriend;
    private ImageView expandedImageView;
    private FrameLayout containerView;

    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private final CallbackManager mCallbackManager = CallbackManager.Factory.create();

    private Context context;
    private GridViewImageAdapter gridViewImageAdapter;
    private UserImageSQLiteHelper imageSQLiteHelper;
    private ArrayList<UserImageMapper> userImages = new ArrayList<>();

    private int windowWidth;
    private int windowHeight;

    private SocialController _socialController = null;

    public static ViewImageFragment newInstance() {
        ViewImageFragment fragment = new ViewImageFragment();
        return fragment;
    }

    public ViewImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        imageSQLiteHelper = new UserImageSQLiteHelper(getActivity());
        _socialController = SocialController.sharedInstance(getActivity(), this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initImageLoader(activity);
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.diskCache(new UnlimitedDiskCache(context.getCacheDir()));
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        ImageLoader.getInstance().init(config.build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_image, container, false);

        imageGrid = (GridView) view.findViewById(R.id.image_gridView);
        btnShareFriend = (Button) view.findViewById(R.id.btn_shareFriends);
        expandedImageView = (ImageView) view.findViewById(R.id.expanedImageView);
        containerView = (FrameLayout) view.findViewById(R.id.container);

        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        btnShareFriend.setOnClickListener(this);
        gridViewImageAdapter = new GridViewImageAdapter(getActivity(), userImages, this);
        imageGrid.setAdapter(gridViewImageAdapter);

        WindowManager wm = (WindowManager) getActivity().getSystemService(context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        windowWidth = size.x;
        windowHeight = size.y;

        Log.d("View Fragment: ", "View Created");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (_socialController != null) {
            _socialController.onResume();
        }
        getUserImages();
        Log.d("View Fragment: ", "Resumed");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_shareFriends:
                if (gridViewImageAdapter.getSelectedPosition().isEmpty()) {
                    Toast.makeText(getActivity(), "Please select image", Toast.LENGTH_SHORT).show();
                } else {
                    int position = gridViewImageAdapter.getSelectedPosition().get(0);
                    UserImageMapper mapper = userImages.get(position);
                    // TODO it maybe will need
//                    new GetParkSocialMediaInfo().execute(PostYourFunApp.all_parks.get(0).getParkId(), mapper.getImageUrl());
                    String fileName = mapper.getImageUrl().substring(mapper.getImageUrl().lastIndexOf("/") + 1);
                    File purposeFile = new File(Constants.IMAGE_FULL_PATH, fileName);
                    if (purposeFile.exists()) shareImage(purposeFile.getPath());
                    else
                        ImageDownloadManager.getSharedInstance().downloadImage(mapper.getImageUrl(), mapper.getImageThumbUrl(), getActivity(), this);
                }
                break;
        }
    }

    @Override
    public void onSuccessImageDownload(Boolean isSuccess, String imageUrl, String thumbUrl, String path) {
        shareImage(path);
    }

    private void shareImage(final String path) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ShareDialog shareDialog = new ShareDialog(getActivity());
                SharePhotoContent content = new SharePhotoContent.Builder().build();
                File purposeFile = new File(path);
                if (purposeFile.exists()) {
                    if (shareDialog.canShow(content)) {
                        Photo photo = new Photo.Builder()
                                .setImage(BitmapFactory.decodeFile(purposeFile.getPath()))
                                .build();
                        SimpleFacebook.getInstance().publish(photo, true, null);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Share Image").setMessage("You can not share without Facebook App").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Image can not be found", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void getUserImages() {
        SharedPreferences _sharedPreference = getActivity().getSharedPreferences("user_info", 0);
        new GettingUserImages().execute(_sharedPreference.getString("user_id", ""));
    }

    protected class GettingUserImages extends AsyncTask<String, Void, List<UserImageMapper>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected List<UserImageMapper> doInBackground(String... params) {
            UserImageDBmanager.sharedInstance(getActivity());
            return UserImageDBmanager.getUserImages(params[0]);
        }

        @Override
        protected void onPostExecute(List<UserImageMapper> mappers) {
            super.onPostExecute(mappers);
            dismissProgressDialog();
            if (mappers != null && !mappers.isEmpty()) {
                userImages.clear();
                userImages.addAll(mappers);
                gridViewImageAdapter.notifyDataSetChanged();
            }
        }
    }


    private void zoomImageFromThumb(final View thumbView, String imageUrl) {

        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoader.getInstance().displayImage(imageUrl, expandedImageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                showProgressDialog();
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                dismissProgressDialog();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                dismissProgressDialog();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                dismissProgressDialog();
            }
        });

        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        thumbView.getGlobalVisibleRect(startBounds);
        containerView.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        expandedImageView.destroyDrawingCache();
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        expandedImageView.destroyDrawingCache();
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

    @Override
    public void onClickedImage(View v, int index) {
        zoomImageFromThumb(v, userImages.get(index).getImageUrl());
//        loadImage(userImages.get(index).getImageId());
    }

    @Override
    public void onChangeCheckbox(boolean isChecked) {

    }

    private void loadImage(String imageId) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // Don't read the pixel array into memory, only read the picture information
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(Constants.IMAGE_FULL_PATH + "/" + imageId + ".jpg", opts);
        // Get a picture from the Options resolution
        int imageHeight = opts.outHeight;
        int imageWidth = opts.outWidth;

        // Calculation of sampling rate
        int scaleX = imageWidth / windowWidth;
        int scaleY = imageHeight / windowHeight;
        int scale = 1;
        // The sampling rate in accordance with the direction of maximum prevail
        if (scaleX > scaleY && scaleY >= 1) {
            scale = scaleX;
        }
        if (scaleX < scaleY && scaleX >= 1) {
            scale = scaleY;
        }

        // False read the image pixel array into memory, in accordance with the sampling rate set
        opts.inJustDecodeBounds = false;
        // Sampling rate
        opts.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(Constants.IMAGE_FULL_PATH + "/" + imageId + ".jpg", opts);
        expandedImageView.setImageBitmap(bitmap);
    }

    @Override
    public void onSuccess(int type, int action) {

    }

    @Override
    public void onFailure(int type, int action) {

    }

    protected class GetParkSocialMediaInfo extends AsyncTask<String, Void, ParkSocialMediaMapper> {
        private String imageUrl;

        @Override
        protected ParkSocialMediaMapper doInBackground(String... params) {
            ParkSocialMediaDBManager.sharedInstance(getActivity());
            ParkSocialMediaMapper result = ParkSocialMediaDBManager.get_Park_Social_Media(params[0]);
            imageUrl = params[1];
            return result;
        }

        @Override
        protected void onPostExecute(ParkSocialMediaMapper parkSocialMediaMapper) {
            super.onPostExecute(parkSocialMediaMapper);
            ShareLinkContent.Builder shareLinkContent = new ShareLinkContent.Builder()
                    .setContentTitle(getString(R.string.post_your_fun_image_share))
                    .setImageUrl(Uri.parse(imageUrl))
                    .setPlaceId(parkSocialMediaMapper.getFacebook())
                    .setContentUrl(Uri.parse(imageUrl));
            ShareLinkContent linkContent = shareLinkContent.build();
            ShareDialog shareDialog = new ShareDialog(getActivity());
            if (shareDialog.canShow(linkContent)) {
                shareDialog.registerCallback(mCallbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException e) {
                    }
                });
                shareDialog.show(linkContent);
            }
        }
    }
}

package com.pyt.postyourfun.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pyt.postyourfun.R;
import com.pyt.postyourfun.activity.ShowImageActivity;

import java.util.List;

/**
 * Created by r8tin on 2/2/16.
 */
public class GridViewShowAllAdapter extends BaseAdapter implements View.OnClickListener {

	private LayoutInflater inflater;
	private DisplayImageOptions options;
	private List<ShowImageActivity.ImageWrapper> imageWrappers;
	private OnItemClickListener listener;

	public interface OnItemClickListener {
		void itemClick(ShowImageActivity.ImageWrapper item);
	}

	public GridViewShowAllAdapter(Context context, List<ShowImageActivity.ImageWrapper> imageWrappers) {
		inflater = LayoutInflater.from(context);
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image)
		                                           .showImageForEmptyUri(R.drawable.default_image)
		                                           .showImageOnFail(R.drawable.default_image)
		                                           .cacheInMemory(true)
		                                           .cacheOnDisk(true)
		                                           .considerExifParams(true)
		                                           .bitmapConfig(Bitmap.Config.RGB_565)
		                                           .build();
		this.imageWrappers = imageWrappers;
	}

	public OnItemClickListener getOnItemClickListener() {
		return listener;
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}

	@Override
	public int getCount() {
		return imageWrappers.size();
	}

	@Override
	public ShowImageActivity.ImageWrapper getItem(int position) {
		return imageWrappers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.item_grid_image, parent, false);
			holder = new ViewHolder();
			assert view != null;
			holder.imageView = (ImageView) view.findViewById(R.id.image);
			holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.imageView.setOnClickListener(this);
		holder.imageView.setTag(position);

		ImageLoader.getInstance().displayImage(getItem(position).getThumbnail_image_url(), holder.imageView, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				holder.progressBar.setProgress(0);
				holder.progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				holder.progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				holder.progressBar.setVisibility(View.GONE);
			}
		}, new ImageLoadingProgressListener() {
			@Override
			public void onProgressUpdate(String imageUri, View view, int current, int total) {
				holder.progressBar.setProgress(Math.round(100.0f * current / total));
			}
		});
		return view;
	}

	@Override
	public void onClick(View v) {
		if (listener != null) listener.itemClick(getItem((int) v.getTag()));
	}

	public static class ViewHolder {
		ImageView imageView;
		ProgressBar progressBar;
	}
}
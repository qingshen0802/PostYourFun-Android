package com.pyt.postyourfun.Image;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.pyt.postyourfun.constants.Constants;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Simon on 7/18/2015.
 */
public class ImageDownloadManager {
    private static ImageDownloadManager sharedInstance = null;
    private ImageDownloadMangerInterface callback = null;

    public static ImageDownloadManager getSharedInstance() {
        if (sharedInstance == null) {
            sharedInstance = new ImageDownloadManager();
        }
        return sharedInstance;
    }

    public void downloadImage(final String imageUrl, String thumbUrl, Context context, ImageDownloadMangerInterface callback) {
        this.callback = callback;
        new AsyncGetImageFromUrl(context, imageUrl, thumbUrl).execute(imageUrl);
    }

    /*
        private void saveImageToDisk(final Bitmap bitmap, final String imageUrl) {
            File outFile = new File(Constants.IMAGE_FULL_PATH);
            outFile.mkdirs();
            final boolean enabledImageDownload = outFile.exists();
            ExecutorService downloadThread = Executors.newSingleThreadExecutor();
            downloadThread.execute(new Runnable() {
                @Override
                public void run() {
                    if (enabledImageDownload) {

                        BufferedOutputStream ostream = null;
                        try {
                            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/"));
                            ostream = new BufferedOutputStream(new FileOutputStream(new File(Constants.IMAGE_FULL_PATH, fileName)), 2 * 1024);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                            if (callback != null) {
                                callback.onSuccessImageDownload(true, imageUrl);
                                return;
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (ostream != null) {
                                    ostream.flush();
                                    ostream.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        if (callback != null) {
                            callback.onSuccessImageDownload(false, "");
                        }
                    }
                }
            });
        }
    */
    protected class AsyncGetImageFromUrl extends AsyncTask<String, String, Void> {

        String imageUrl, thumbUrl;
        Context context;

        ProgressDialog mProgressDialog;

        public AsyncGetImageFromUrl(Context context, String url, String thumbUrl) {
            this.imageUrl = url;
            this.thumbUrl = thumbUrl;
            this.context = context;
            mProgressDialog = new ProgressDialog(this.context);
            mProgressDialog.setMessage("Downloading file...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            super.onProgressUpdate(progress);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(String... aurl) {
            int count;

            try {
                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Length of file: " + lenghtOfFile);
                OutputStream output = null;

                try {
                    String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                    InputStream input = new BufferedInputStream(url.openStream());
                    new File(Constants.IMAGE_FULL_PATH).mkdirs();
                    File purposeFile = new File(Constants.IMAGE_FULL_PATH, fileName);
                    output = new FileOutputStream(purposeFile);

                    byte data[] = new byte[30 * 1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                        output.write(data, 0, count);
                    }
                    if (callback != null) {
                        callback.onSuccessImageDownload(true, imageUrl, thumbUrl, purposeFile.getPath());
                        return null;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (output != null) {
                            output.flush();
                            output.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (callback != null) {
                    callback.onSuccessImageDownload(false, "", "", "");
                    return null;
                }
            } catch (Exception e) {
            }
            return null;
        }
    }
}

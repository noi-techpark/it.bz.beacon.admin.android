package it.bz.beacon.adminapp.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import io.swagger.client.ApiCallback;
import io.swagger.client.ApiException;
import io.swagger.client.model.Resource;
import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.BeaconImage;
import it.bz.beacon.adminapp.ui.detail.DetailActivity;

import static android.view.View.X;
import static io.swagger.client.auth.OAuthFlow.application;

public class BitmapTools {

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap resizeBitmap(String photoPath, int maxSideLength) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoWidth = bmOptions.outWidth;
        int photoHeight = bmOptions.outHeight;

        int scaleFactor = 1;

        if (photoWidth > photoHeight) {
            if (photoWidth > maxSideLength) {
                scaleFactor = photoWidth / maxSideLength;
            }
        } else {
            if (photoHeight > maxSideLength) {
                scaleFactor = photoHeight / maxSideLength;
            }
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }

    public static String saveToInternalStorage(Context context, Bitmap bitmap, String directoryName, String fileName) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(directoryName, Context.MODE_PRIVATE);
        File file = new File(directory, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fos);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }

    public static String deleteFromInternalStorage(Context context, String directoryName, String fileName) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(directoryName, Context.MODE_PRIVATE);
        File file = new File(directory, fileName);

        try {
            if (file.exists()) {
                file.delete();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public static void downloadImage(Context context, BeaconImage beaconImage) {
        DownloadImageTask downloadImageTask = new DownloadImageTask(beaconImage);
        downloadImageTask.execute(context);
    }

    private static class DownloadImageTask extends AsyncTask<Context, Void, Void> {

        private BeaconImage beaconImage;

        DownloadImageTask(BeaconImage beaconImage) {
            this.beaconImage = beaconImage;
        }

        @Override
        protected Void doInBackground(Context... context) {
                ContextWrapper contextWrapper = new ContextWrapper(context[0]);
                File directory = contextWrapper.getDir("images", Context.MODE_PRIVATE);
                File file = new File(directory, beaconImage.getFileName());

                try {
                    String sUrl = context[0].getString(R.string.basePath).concat("/v1/admin/beacons/").concat(String.valueOf(beaconImage.getBeaconId())).concat("/images/").concat(String.valueOf(beaconImage.getId()));
//                    URL url = new URL(sUrl);
//                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
//                    connection.addRequestProperty("Authorization", "Bearer " + AdminApplication.getStorage().getLoginUserToken());
//                    connection.addRequestProperty("Accept", "*/*");
//                    connection.setDoOutput(true);
//
//                    byte[] buffer = new byte[1024];
//
//
//                    InputStream inputStream = connection.getInputStream();
//
//                    int bufferLength = 0;
//
//                    while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
//                        fileOutput.write(buffer, 0, bufferLength);
//                    }
//                    fileOutput.close();
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(sUrl)
                            .addHeader("Authorization", "Bearer " + AdminApplication.getStorage().getLoginUserToken())
                            .addHeader("Content-Type", "application/json")
                            .build();
                    Response response = client.newCall(request).execute();

                    InputStream inputStream = response.body().byteStream();
                    FileOutputStream fileOutput = new FileOutputStream(file);

//                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//                    String result, line = reader.readLine();
//                    result = line;
//                    while((line = reader.readLine()) != null) {
////                        result += line;
//                        fileOutput.write(buffer, 0, bufferLength);
//                    }
                    int bufferLength = 0;
                    byte[] buffer = new byte[1024];
//
                    while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                        fileOutput.write(buffer, 0, bufferLength);
                    }
                    fileOutput.close();
                    response.body().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            return null;
        }
    }
}

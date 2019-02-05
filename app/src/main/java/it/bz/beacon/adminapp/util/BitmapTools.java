package it.bz.beacon.adminapp.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import it.bz.beacon.adminapp.data.entity.BeaconImage;

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
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
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

    public static void downloadImage(Context context, BeaconImage beaconImage) {
        DownloadImageTask downloadImageTask = new DownloadImageTask(beaconImage.getUrl(), beaconImage.getBeaconId());
        downloadImageTask.execute(context);
    }

    private static class DownloadImageTask extends AsyncTask<Context, Void, Void> {

        private String url;
        private long beaconImageId;

        DownloadImageTask(String url, long beaconImageId) {
            this.url = url;
            this.beaconImageId = beaconImageId;
        }

        @Override
        protected Void doInBackground(Context... context) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            saveToInternalStorage(context[0], bitmap, "images", beaconImageId + ".png");
            return null;
        }
    }
}

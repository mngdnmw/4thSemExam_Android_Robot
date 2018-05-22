package mafioso.so.so.android_robot.dal;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;

import com.google.firebase.storage.UploadTask;

import mafioso.so.so.android_robot.shared.Callback;

public class DalFacade {
    Dao mDao;

    public DalFacade() {
        mDao = new Dao();
    }

    public boolean uploadImage(Bitmap image, Location lastKnownLocation, final Callback callback) {
        return mDao.uploadImage(image,lastKnownLocation, callback);
    }
}

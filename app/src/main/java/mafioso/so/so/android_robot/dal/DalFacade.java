package mafioso.so.so.android_robot.dal;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;

public class DalFacade {
    Dao mDao;

    public DalFacade() {
        mDao = new Dao();
    }

    public boolean uploadImage(Bitmap image, Location lastKnownLocation) {
        return mDao.uploadImage(image,lastKnownLocation);
    }
}

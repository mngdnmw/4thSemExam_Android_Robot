package mafioso.so.so.android_robot.bll;

import android.graphics.Bitmap;
import android.location.Location;
import mafioso.so.so.android_robot.dal.DalFacade;
import mafioso.so.so.android_robot.shared.Callback;

public class BllFacade {

    private DalFacade mDalFac;

    public BllFacade() {
        mDalFac = new DalFacade();
    }

    public boolean uploadImage(Bitmap image, Location lastKnownLocation, final Callback callback) {
        return mDalFac.uploadImage(image, lastKnownLocation, callback);
    }
}

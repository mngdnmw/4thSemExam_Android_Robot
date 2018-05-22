package mafioso.so.so.android_robot.bll;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;

import mafioso.so.so.android_robot.dal.DalFacade;

public class BllFacade {

    DalFacade mDalFac;

    public BllFacade() {
        mDalFac = new DalFacade();
    }

    public boolean uploadImage(Bitmap image, Location lastKnownLocation){
        return mDalFac.uploadImage(image, lastKnownLocation);
    }
}

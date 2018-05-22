package mafioso.so.so.android_robot.dal;

import android.graphics.Bitmap;

import android.location.Location;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

import mafioso.so.so.android_robot.shared.Callback;


public class Dao {
    FirebaseStorage mStorage;
    StorageReference mStorageRef, mThisImageRef;
    UploadTask mUploadTask;

    final static String TAG = "Testing stuff";

    public Dao() {
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

    }

    public boolean uploadImage(Bitmap image, final Location lastKnownLocation, final Callback callback) {

        Date currentTime = Calendar.getInstance().getTime();
        mThisImageRef = mStorageRef.child("/images/" + currentTime.toString() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        mUploadTask = mThisImageRef.putBytes(data);
        // Register observers to listen for when the download is done or if it fails
        mUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callback.onTaskCompleted(false);

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                callback.onTaskCompleted(true);
                updateMetadata(lastKnownLocation);
            }
        });
        return false;
    }

    private void updateMetadata(Location lastKnownLocation) {
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("Latitude", Double.toString(lastKnownLocation.getLatitude()))
                .setCustomMetadata("Longitude", Double.toString(lastKnownLocation.getLongitude()))
                .build();

        mThisImageRef.updateMetadata(metadata)
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        Log.d(TAG, "Updated meta");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d(TAG, exception.toString());
                    }
                });
    }
}

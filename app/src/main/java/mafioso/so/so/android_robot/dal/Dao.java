package mafioso.so.so.android_robot.dal;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import mafioso.so.so.android_robot.R;

public class Dao {
    FirebaseStorage mStorage;
    StorageReference mStorageRef, mThisImageRef;

    FirebaseFirestore mDb;
    FirebaseFunctions mFunctions;
    UploadTask mUploadTask;

    final static String TAG = "Testing stuff";

    public Dao() {
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        mDb = FirebaseFirestore.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
    }

    //public void uploadImage(Context context, Bitmap image, Location lastKnownLocation)
    //public void uploadImage(Context context, final Location lastLoc) {
        public void uploadImage(Context context) {


        mThisImageRef = mStorageRef.child("/images/bitmap2.jpg");


        Log.d(TAG, "Sending image" );
        Drawable drawable = context.getDrawable(R.drawable.ic_launcher_foreground);
        Bitmap image = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        mUploadTask = mThisImageRef.putBytes(data);

        // Register observers to listen for when the download is done or if it fails
        mUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d(TAG, exception.toString() );
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d(TAG,  downloadUrl.toString());

                //updateMetadata(lastLoc);
            }
        });

    }

    private void updateMetadata(Location lastKnownLocation){
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

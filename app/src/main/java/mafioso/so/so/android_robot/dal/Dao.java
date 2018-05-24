package mafioso.so.so.android_robot.dal;

import android.graphics.Bitmap;

import android.location.Location;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mafioso.so.so.android_robot.shared.Callback;


public class Dao {
    private FirebaseFirestore mDb;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef, mThisImageRef;
    private UploadTask mUploadTask;
    private final static String TAG = "Testing stuff";

    public Dao() {
        mDb = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
    }
    //uploads image by first creating a document in firestore and then uploading the image
    public boolean uploadImage(final Bitmap image, final Location lastKnownLocation, final Callback callback) {
        new Thread() {
            @Override
            public void run() {
                createDocumentInFirestore(image, lastKnownLocation, callback);
            }

        }.start();
        return false;
    }

    private void createDocumentInFirestore(final Bitmap image, final Location lastKnownLocation, final Callback callback) {
        LocalDate currentTime = LocalDate.now();
        // Create a new imageDoc
        Map<String, Object> imageDoc = new HashMap<>();
        imageDoc.put("latitude", lastKnownLocation.getLatitude());
        imageDoc.put("longitude", lastKnownLocation.getLongitude());
        imageDoc.put("time", currentTime.toString());

        // Add a new document with a generated ID
        mDb.collection("pictures")
                .add(imageDoc)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        createFileInStorage(image, callback, documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    private void createFileInStorage(Bitmap image, final Callback callback, String uid) {
        mThisImageRef = mStorageRef.child("/images/" + uid + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        mUploadTask = mThisImageRef.putBytes(data);
        mUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                callback.onTaskCompleted(true);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callback.onTaskCompleted(false);
            }
        });

    }

}

package mafioso.so.so.android_robot.dal;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

public class Dao {
    FirebaseFirestore fbDb = FirebaseFirestore.getInstance();
    FirebaseFunctions fbFunctions = FirebaseFunctions.getInstance();
}

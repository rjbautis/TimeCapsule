package com.android121.timecapsule;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class FirebaseUtil {

    // Firebase Storage Task Listener
    interface OnStorageTaskCompleteListener {
        void onSuccess(String path);
        void onFailure();
    }

    private Context mContext;
    private FirebaseStorage storage;

    FirebaseUtil(Context context) {
        mContext = context;
        storage = FirebaseStorage.getInstance();
    }

    public void uploadStorage(Uri file, String mCapsuleId, final OnStorageTaskCompleteListener mStorageListener) {
        StorageReference storageRef = storage.getReference();
        Date date = new Date();

        // Unique path of media to be uploaded in Firebase Storage will be `capsuleId/seconds_filepath.png`
        final String path = mCapsuleId + "/" + date.getTime() + "_" + file.getLastPathSegment();
        final StorageReference capsuleRef = storageRef.child(path);

        // Upload file to Storage
        final UploadTask uploadTask = capsuleRef.putFile(file);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get download URL for the firebase storage object
                capsuleRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mStorageListener.onSuccess(uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                mStorageListener.onFailure();
            }
        });
    }
}

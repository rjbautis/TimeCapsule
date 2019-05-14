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

    private Context mContext;

    private FirebaseStorage storage;

    FirebaseUtil(Context context) {
        mContext = context;

        storage = FirebaseStorage.getInstance();
    }

    public void uploadStorage(Uri file, String mCapsuleId) {
        StorageReference storageRef = storage.getReference();
        Date date = new Date();

        // Unique path of media to be uploaded in Firebase Storage will be `capsuleId/seconds_filepath.png`
        String path = mCapsuleId + "/" + date.getTime() + "_" + file.getLastPathSegment();
        StorageReference capsuleRef = storageRef.child(path);

        // Upload file to Storage
        UploadTask uploadTask = capsuleRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(mContext, "Failed to upload picture" ,Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(mContext, "Successfully uploaded picture" ,Toast.LENGTH_LONG).show();
            }
        });
    }
}

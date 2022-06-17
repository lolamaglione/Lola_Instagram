package com.lolamaglione.lolainstagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lolamaglione.lolainstagram.R;
import com.lolamaglione.lolainstagram.databinding.ActivitySignUpBinding;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.File;

public class SignUpActivity extends AppCompatActivity {

    private EditText etUsernameSign;
    private EditText etPasswordSign;
    private Button btnSignUpFR;
    private ImageView ivProfilePicture;
    private Button btnCaptureProfilePicture;
    private ActivitySignUpBinding binding;
    private String TAG = "SignUpActivity";
    private File photoFile;
    private String photoFileName = "photo.jpg";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        etUsernameSign = binding.etUsernameSign;
        etPasswordSign = binding.etPasswordSign;
        btnSignUpFR = binding.btnSignUpFR;
        btnCaptureProfilePicture = binding.btnProfilePictureCapture;
        ivProfilePicture = binding.ivProfilePictureSign;

//        btnCaptureProfilePicture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                launchCamera();
//
//            }
//        });

        btnSignUpFR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsernameSign.getText().toString();
                System.out.println("username:" + username);
                String password = etPasswordSign.getText().toString();
                System.out.println("password:" + password);
                try {
                    saveUser(username, password);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                goToMainActivity();
            }
        });
    }

    private void saveUser(String username, String password) throws ParseException {
        ParseUser user = new ParseUser();
        user.put("username", username);
        user.put("password", password);
//        ParseUser.getCurrentUser().put("profilePicture", new ParseFile(photoFile));
//        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                ParseFile profileImage = ParseUser.getCurrentUser().getParseFile("profilePicture");
//                Glide.with(SignUpActivity.this).load(profileImage).apply(new RequestOptions().circleCrop()).into(ivProfilePicture);
//            }
//        });
//        user.saveInBackground();
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e!= null){
                    Log.e(TAG, "error:" + e);
                }
            }
        });
    }

    private void launchCamera() {
        //create Intent to take a pucture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File refernece to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // require for API >= 24
        Uri fileProvider = FileProvider.getUriForFile(this, getString(R.string.file_provider) , photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // if you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // so as long as the result is not null, it's safe to use the intent
        if (intent.resolveActivity(this.getPackageManager()) != null){
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }

    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivProfilePicture.setImageBitmap(takenImage);
                Glide.with(this).load(getPhotoFileUri(photoFileName)).apply(new RequestOptions().circleCrop()).into(ivProfilePicture);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void goToMainActivity(){
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
package com.lolamaglione.lolainstagram.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lolamaglione.lolainstagram.EndlessRecyclerViewScrollListener;
import com.lolamaglione.lolainstagram.adapters.GridPostAdapter;
import com.lolamaglione.lolainstagram.models.Post;
import com.lolamaglione.lolainstagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 * allows user to see their own profile
 */
public class ProfileFragment extends Fragment {

    private RecyclerView rvPostsGrid;
    private List<Post> profilePosts;
    private GridPostAdapter gridAdapter;
    private Button btnChangeProfile;
    private Button btnSave;
    private File photoFile;
    private String photoFileName = "photo.jpg";
    public static final String TAG = "Profile Fragment";
    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;
    protected int current_offset = 20;
    private ImageView ivProfilePicture;
    private TextView tvUsernameProfile;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPostsGrid = view.findViewById(R.id.rvGridPosts);
        ivProfilePicture = view.findViewById(R.id.ivProfile);
        tvUsernameProfile = view.findViewById(R.id.tvUsernameProfile);
        btnChangeProfile = view.findViewById(R.id.btnChangeProfile);
        btnSave = view.findViewById(R.id.btnSave);
        // initialize the array that will hold posts and create a PostAdapter
        profilePosts = new ArrayList<>();
        gridAdapter = new GridPostAdapter(getContext(), profilePosts);

        rvPostsGrid.setAdapter(gridAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3 );
        rvPostsGrid.setLayoutManager(gridLayoutManager);
        queryPosts(0);
        ParseFile profilePicture = ParseUser.getCurrentUser().getParseFile("profilePicture");
        if (profilePicture != null) {
            Glide.with(getContext()).load(profilePicture.getUrl()).apply(new RequestOptions().circleCrop()).into(ivProfilePicture);
        }
        tvUsernameProfile.setText(ParseUser.getCurrentUser().getUsername());

        btnChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        // Lookup the swipe container view
//        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.srlayout);
//        // Setup refresh listener which triggers new data loading
//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                // Your code to refresh the list here.
//                // Make sure you call swipeContainer.setRefreshing(false)
//                // once the network request has completed successfully.
//                gridAdapter.clear();
//                queryPosts(0);
//            }
//        });
//        // Configure the refreshing colors
//        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
//
//        // Retain an instance so that you can call `resetState()` for fresh searches
//        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                // Triggered only when new data needs to be appended to the list
//                // Add whatever code is needed to append new items to the bottom of the list
//                loadNextDataFromApi(page);
//            }
//        };
        // Adds the scroll listener to RecyclerView
        //rvPostsGrid.addOnScrollListener(scrollListener);
    }

    private void updateProfile() {
        ParseUser.getCurrentUser().put("profilePicture", new ParseFile(photoFile));
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ParseFile profileImage = ParseUser.getCurrentUser().getParseFile("profilePicture");
                Glide.with(getContext()).load(profileImage).apply(new RequestOptions().circleCrop()).into(ivProfilePicture);
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
        Uri fileProvider = FileProvider.getUriForFile(getContext(), getString(R.string.file_provider) , photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // if you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // so as long as the result is not null, it's safe to use the intent
        if (intent.resolveActivity(getContext().getPackageManager()) != null){
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }

    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

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
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadNextDataFromApi(int page) {
        queryPosts(page);
    }

    private void queryPosts(int page){
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        // limit query to latest 20 items
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                // for debugging purposes let's print every post description to logcat
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }

                // save received posts to list and notify adapter of new data
                profilePosts.addAll(posts);
                if (page == 0){
                    gridAdapter.notifyItemRangeInserted(page, 20);
                } else{
                    gridAdapter.notifyItemRangeInserted(current_offset, 20);
                }
                current_offset = current_offset*(page+1);
                //swipeContainer.setRefreshing(false);
            }
        });
    }


}
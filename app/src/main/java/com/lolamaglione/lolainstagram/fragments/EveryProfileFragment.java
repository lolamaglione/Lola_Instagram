package com.lolamaglione.lolainstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lolamaglione.lolainstagram.EndlessRecyclerViewScrollListener;
import com.lolamaglione.lolainstagram.adapters.GridPostAdapter;
import com.lolamaglione.lolainstagram.models.Post;
import com.lolamaglione.lolainstagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EveryProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Allows user to access everyone else's profile
 */
public class EveryProfileFragment extends Fragment {

    private RecyclerView rvPostsGrid;
    private List<Post> profilePosts;
    private GridPostAdapter gridAdapter;
    private File photoFile;
    private String photoFileName = "photo.jpg";
    public static final String TAG = "Profile Fragment";
    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;
    protected int current_offset = 20;
    private ImageView ivProfilePicture;
    private TextView tvUsernameProfile;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private String imageURL;
    private String username;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EveryProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EvertProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EveryProfileFragment newInstance(String param1, String param2) {
        EveryProfileFragment fragment = new EveryProfileFragment();
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
            imageURL = getArguments().getString("imageURL");
            username = getArguments().getString("username");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_evert_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPostsGrid = view.findViewById(R.id.rvGridPostsEveryProfile);
        ivProfilePicture = view.findViewById(R.id.ivEveryProfile);
        tvUsernameProfile = view.findViewById(R.id.tvUsernameEveryProfile);
        // initialize the array that will hold posts and create a PostAdapter
        profilePosts = new ArrayList<>();
        gridAdapter = new GridPostAdapter(getContext(), profilePosts);

        rvPostsGrid.setAdapter(gridAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3 );
        rvPostsGrid.setLayoutManager(gridLayoutManager);
        queryPosts(0);
        if (imageURL != null) {
            Glide.with(getContext()).load(imageURL).apply(new RequestOptions().circleCrop()).into(ivProfilePicture);
        }
        tvUsernameProfile.setText(username);
    }

    private void queryPosts(int page){
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        // limit query to latest 20 items
        query.whereEqualTo(Post.KEY_USER, getArguments().getParcelable("user"));
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
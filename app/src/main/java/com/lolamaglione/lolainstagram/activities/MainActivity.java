package com.lolamaglione.lolainstagram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lolamaglione.lolainstagram.Post;
import com.lolamaglione.lolainstagram.R;
import com.lolamaglione.lolainstagram.databinding.ActivityMainBinding;
import com.lolamaglione.lolainstagram.fragments.ComposeFragment;
import com.lolamaglione.lolainstagram.fragments.PostsFragment;
import com.lolamaglione.lolainstagram.fragments.ProfileFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * main activity deals with the Fragments and the bottomNavigationView
 * to access the PostFragment, ComposeFragment, and the ProfileFragment
 */
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    public final static String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up ViewBindingLibrary
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        bottomNavigationView = binding.bottomNavigation;

        //queryPosts();
        // Bottom Navigation View composing
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch(item.getItemId()) {
                    case R.id.action_compose:
                        Toast.makeText(MainActivity.this, "Compose!", Toast.LENGTH_SHORT).show();
                        fragment = new ComposeFragment();
                        break;
                    case R.id.action_home:
                        Toast.makeText(MainActivity.this, "Home!", Toast.LENGTH_SHORT).show();
                        fragment = new PostsFragment();
                        break;
                    case R.id.action_profile:
                    default:
                        Toast.makeText(MainActivity.this, "Profile!", Toast.LENGTH_SHORT).show();
                        fragment = new ProfileFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    // Must return true for the menu to be displaced
    // Create options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // when menu buttons are selected do this
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logout){
            logoutUser();
        }
        if(item.getItemId() == R.id.feed_activity){
            Intent intent = new Intent(MainActivity.this, FeedActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        Log.i(TAG, "Attempting to logout current user");
        ParseUser.logOut();
        //updating current user
        ParseUser currentUser = ParseUser.getCurrentUser();

        // go to LoginActivity once you logout
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null){
                    Log.e("Post Application", "issue with getting posts", e);
                    return;
                }
                for (Post post : posts){
                    Log.i("PostApplication", "Post: " + post.getDescription() + " username: " + post.getUser().getUsername());
                }
            }
        });
    }

}
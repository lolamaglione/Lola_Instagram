package com.lolamaglione.lolainstagram;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lolamaglione.lolainstagram.activities.PostDetailActivity;
import com.lolamaglione.lolainstagram.fragments.ComposeFragment;
import com.lolamaglione.lolainstagram.fragments.EveryProfileFragment;
import com.lolamaglione.lolainstagram.fragments.PostsFragment;
import com.lolamaglione.lolainstagram.fragments.ProfileFragment;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * PostsAdapter connect the PostsRecyclerView to the data we want to display by porviding ViewHolder
 * and binding materials.
 */
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{

    private Context context;
    private List<Post> posts;
    private List<String> likedPosts = new ArrayList<>();

    public PostsAdapter(Context context, List<Post> posts){
        this.context = context;
        this.posts = posts;
        likedPosts = (List<String>) ParseUser.getCurrentUser().get("likedPosts");
        if (likedPosts == null ) {
            likedPosts = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Post post = posts.get(position);
        holder.bind(post);

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsername;
        private ImageView ivUploadImage;
        private TextView tvDescription;
        private TextView tvTime;
        private ImageView ivProfilePicture;
        private TextView tvUsernameCaption;
        private TextView tvLikes;
        private ImageButton ibLike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivUploadImage = itemView.findViewById(R.id.ivUploadPicture);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvUsernameCaption = itemView.findViewById(R.id.tvUsernameCaption);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            ibLike = itemView.findViewById(R.id.ibLike);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        Post post = posts.get(position);
                        Intent intent = new Intent(context, PostDetailActivity.class);
                        intent.putExtra(Post.class.getSimpleName(), post);
                        context.startActivity(intent);
                    }
                }
            });

        }

        public void bind(Post post) {
            String postId = post.getObjectId();
            // Bind the post data to the view elements
            if (likedPosts.contains(postId)) {
                ibLike.setImageResource(R.drawable.ic_heart_filled);
            }
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            Date createdAt = post.getCreatedAt();
            tvTime.setText(post.calculateTimeAgo(createdAt));
            tvLikes.setText("" + post.getLikes());
            ParseFile image = post.getImage();
            if (image != null){
                Glide.with(context).load(image.getUrl()).apply(new RequestOptions()).into(ivUploadImage);
                ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
                int width = layoutParams.width;
                layoutParams.height = width;
                itemView.setLayoutParams(layoutParams);
            }
            ParseFile profileImage = post.getUser().getParseFile("profilePicture");
            if (image != null) {
                Glide.with(context).load(profileImage.getUrl()).apply(new RequestOptions().circleCrop()).into(ivProfilePicture);
            }
            tvUsernameCaption.setText(post.getUser().getUsername());
            int currentLikes = post.getLikes();
            ibLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!likedPosts.contains(postId)){
                        Toast.makeText(context, "liked", Toast.LENGTH_SHORT).show();
                        ibLike.setImageResource(R.drawable.ic_heart_filled);
                        System.out.println(currentLikes);
                        int newLikes = currentLikes + 1;
                        post.setLikes(newLikes);
                        ParseUser.getCurrentUser().addAllUnique("likedPosts", Arrays.asList(post.getObjectId()));
                        likedPosts.add(post.getObjectId());
                        notifyDataSetChanged();
                        post.saveInBackground();
                        ParseUser.getCurrentUser().saveInBackground();
                        System.out.print("list:" + ParseUser.getCurrentUser().get("likedPosts"));
                    } else {
                        int newLikes = currentLikes - 1;
                        post.setLikes(newLikes);
                        ParseUser.getCurrentUser().removeAll("likedPosts", Arrays.asList(post.getObjectId()));
                        likedPosts.remove(post.getObjectId());
                        notifyDataSetChanged();
                        post.saveInBackground();
                        ParseUser.getCurrentUser().saveInBackground();
                        ibLike.setImageResource(R.drawable.ic_heart);
                    }
                }
            });

            ivProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    Fragment fragment;
                    fragment = new EveryProfileFragment();
                    Bundle bundle = new Bundle();
                    ParseFile profileImage = post.getUser().getParseFile("profilePicture");
                    if (profileImage != null){
                        bundle.putString("imageURL", profileImage.getUrl());
                    }
                    bundle.putString("username", post.getUser().getUsername());
                    bundle.putParcelable("user", post.getUser());
                    fragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
                }
            });
        }
    }
}

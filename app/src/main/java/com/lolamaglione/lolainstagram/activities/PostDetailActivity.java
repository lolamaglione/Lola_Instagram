package com.lolamaglione.lolainstagram.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lolamaglione.lolainstagram.R;
import com.lolamaglione.lolainstagram.models.Post;
import com.lolamaglione.lolainstagram.databinding.ActivityPostDetailBinding;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * PostDetailActivity contains the details of a post (picture, likes, post, etc.).
 */
public class PostDetailActivity extends AppCompatActivity {

    private ImageView ivProfilePicture;
    private ImageView ivUploadPicture;
    private TextView tvUsername;
    private TextView tvDescription;
    private TextView tvUsernameCaption;
    private TextView tvTime;
    private TextView tvLikes;
    private ImageButton ibLike;
    private ImageButton ibComment;
    private ImageButton ibSend;
    private ImageButton ibSave;
    private ActivityPostDetailBinding binding;
    private Post post;
    private List<String> currentUserLikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        currentUserLikes = (List<String>) ParseUser.getCurrentUser().get("likedPosts");
        if (currentUserLikes == null ) {
            currentUserLikes = new ArrayList<>();
        }

        ivProfilePicture = binding.ivProfileDetail;
        ivUploadPicture = binding.ivUploadDetail;
        tvUsername = binding.tvUsernameDetail;
        tvDescription = binding.tvDescriptionDetail;
        tvUsernameCaption = binding.tvUsernameDescriptionDetails;
        tvTime = binding.tvTimeDetail;
        ibLike = binding.ibLikeDetail;
        ibComment = binding.ibCommentDetail;
        ibSend = binding.ibSendDetail;
        ibSave = binding.ibSaveDetail;
        tvLikes = binding.tvAmountLikes;

        post = (Post) getIntent().getParcelableExtra(Post.class.getSimpleName());

        String postId = post.getObjectId();
        if (currentUserLikes.contains(postId)) {
            ibLike.setImageResource(R.drawable.ic_heart_filled);
        }
        tvLikes.setText("" + post.getLikes() + " likes");
        tvUsername.setText(post.getUser().getUsername());
        tvUsernameCaption.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());
        ParseFile profileImage = post.getUser().getParseFile("profilePicture");
        if (profileImage != null) {
            Glide.with(this).load(profileImage.getUrl()).apply(new RequestOptions().circleCrop()).into(ivProfilePicture);
        }

        Date createdAt = post.getCreatedAt();
        tvTime.setText(post.calculateTimeAgo(createdAt));
        ParseFile UploadImage = post.getImage();
        if (UploadImage != null) {
            Glide.with(this).load(UploadImage.getUrl()).into(ivUploadPicture);
        }

        ibComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailActivity.this, CommentActivity.class);
                intent.putExtra("post", post);
                PostDetailActivity.this.startActivity(intent);
            }
        });

    }



}
package com.lolamaglione.lolainstagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lolamaglione.lolainstagram.Comment;
import com.lolamaglione.lolainstagram.CommentAdapter;
import com.lolamaglione.lolainstagram.Post;
import com.lolamaglione.lolainstagram.R;
import com.lolamaglione.lolainstagram.databinding.ActivityCommentBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Comment Activity will take you to a page where there are comments for a specific post.
 * here you will also be able to add comments
 */
public class CommentActivity extends AppCompatActivity {

    private RecyclerView rvComments;
    private ActivityCommentBinding binding;
    private EditText etComment;
    private Button btnComment;
    private List<Comment> allComments;
    private CommentAdapter adapter;
    public static final String TAG = "Comment Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        rvComments = binding.rvComments;
        etComment = binding.etComment;
        btnComment = binding.btnComment;

        allComments = new ArrayList<>();
        adapter = new CommentAdapter(this, allComments);
        Post post = getIntent().getParcelableExtra("post");
        rvComments.setAdapter(adapter);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        queryComments(post);

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = etComment.getText().toString();
                if(comment.isEmpty()){
                    Toast.makeText(CommentActivity.this, "no comment", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Comment being uploaded");
                uploadComment(comment, post);
                adapter.notifyItemInserted(0);
            }
        });

    }

    private void queryComments(Post post) {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_POST);
        query.include(Comment.KEY_USER);
        query.whereEqualTo(Comment.KEY_POST, post);
        query.setLimit(20);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if (e != null){
                    Log.e(TAG, "issue with getting comments", e);
                    return;
                }

                for (Comment comment : comments) {
                    Log.i(TAG, "Comments:" + comment.getComment());
                }

                allComments.addAll(comments);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void uploadComment(String comment, Post post){
        Comment comment_final = new Comment();
        comment_final.setComment(comment);
        comment_final.setUserComment(ParseUser.getCurrentUser());
        comment_final.setPostId(post);
        comment_final.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "error while saving", e);
                }
                etComment.setText("");
            }
        });
    }


}
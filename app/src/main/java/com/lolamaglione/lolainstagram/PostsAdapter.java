package com.lolamaglione.lolainstagram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseFile;

import java.util.Date;
import java.util.List;

/**
 * PostsAdapter connect the PostsRecyclerView to the data we want to display by porviding ViewHolder
 * and binding materials.
 */
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{

    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts){
        this.context = context;
        this.posts = posts;
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivUploadImage = itemView.findViewById(R.id.ivUploadPicture);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvUsernameCaption = itemView.findViewById(R.id.tvUsernameCaption);

        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            Date createdAt = post.getCreatedAt();
            tvTime.setText(post.calculateTimeAgo(createdAt));
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
        }
    }
}

package com.lolamaglione.lolainstagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lolamaglione.lolainstagram.R;
import com.lolamaglione.lolainstagram.activities.PostDetailActivity;
import com.lolamaglione.lolainstagram.models.Post;
import com.parse.ParseFile;

import java.util.List;

/**
 * Enables GridView in the profile page with the GridLayoutManager
 */
public class GridPostAdapter extends RecyclerView.Adapter<GridPostAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public GridPostAdapter(Context context, List<Post> posts){
        this.context = context;
        this.posts = posts;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridPostAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list){
        posts.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPost = itemView.findViewById(R.id.ivPost);

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
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivPost);
            }
        }
    }


}

package com.lolamaglione.lolainstagram;

import android.app.Application;

import com.lolamaglione.lolainstagram.models.Comment;
import com.lolamaglione.lolainstagram.models.Post;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Updates the Back4APP database connection
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("geT48V0um5ij6CWbs9ns83qhZ7ygHWCQZHHPUuJO")
                .clientKey("umA5gXMhdyIrvvJbJT5SSHEWHaWzcWc7FvQINkKn")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }

}

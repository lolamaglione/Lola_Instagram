package com.lolamaglione.lolainstagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("geT48V0um5ij6CWbs9ns83qhZ7ygHWCQZHHPUuJO")
                .clientKey("umA5gXMhdyIrvvJbJT5SSHEWHaWzcWc7FvQINkKn")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }

}

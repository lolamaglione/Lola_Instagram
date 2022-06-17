package com.lolamaglione.lolainstagram;


import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * create a parseClass object Comment every time someone adds a comment.
 */
@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String KEY_COMMENT = "description_comment";
    public static final String KEY_POST = "postId";
    public static final String KEY_USER = "userID";

    public String getComment() {return getString(KEY_COMMENT);}

    public void setComment(String comment) { put(KEY_COMMENT, comment);}

    public ParseUser getUserComment() {return getParseUser(KEY_USER);}

    public void setUserComment(ParseUser user) {put(KEY_USER, user);}

    public ParseObject getPostId() {return getParseObject(KEY_POST);}

    public void setPostId(ParseObject post) {put(KEY_POST, post);}
}

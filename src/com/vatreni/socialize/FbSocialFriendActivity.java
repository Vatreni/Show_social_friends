package com.vatreni.socialize;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import com.facebook.*;
import com.facebook.model.GraphMultiResult;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;
import com.vatreni.Show_social_friends.R;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ghaydysh Taras
 * gorthaur12@gmail.com
 */
public class FbSocialFriendActivity extends Activity implements SocialFriendsAdapter.OnCheckChangeListener, View.OnClickListener {
    public static final String AVATAR_URL = "pic_square";
    private ArrayList<SocialFriendModel> friends = null;
    private List<GraphUser> facebookFriends = new ArrayList<GraphUser>();
    private ListView friendsListView;
    private String FACEBOOK_FQL_QUERY = "SELECT uid, name,pic_square FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me())";
    private String FQL = "/fql";
    private static final String QUERY_KEY = "q";
    private SocialFriendsAdapter adapter;
    private SocialFriendsCheckBox checkAllCheckBox;
    private Button activitySocialFriendsButton ;

    public static void start(Activity pActivity) {
        Intent intent = new Intent(pActivity, FbSocialFriendActivity.class);
        pActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openActiveSession();
        setContentView(R.layout.activity_social_friends);
        initViews();
        friends = new ArrayList<SocialFriendModel>();
        showFacebookFriends();
    }

    private void initViews() {
        activitySocialFriendsButton = (Button) findViewById(R.id.activitySocialFriendsButton);
        activitySocialFriendsButton.setOnClickListener(this);
        friendsListView = (ListView) findViewById(R.id.activitySocialFriendsListview);
        checkAllCheckBox = (SocialFriendsCheckBox) findViewById(R.id.activitySocialFriendsCheckBox);
        checkAllCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView instanceof SocialFriendsCheckBox) {
                    if (((SocialFriendsCheckBox) buttonView).isCheckOnlyOne()) {
                        buttonView.setChecked(false);
                        ((SocialFriendsCheckBox) buttonView).setCheckOnlyOne(false);
                    } else {
                        for (SocialFriendModel friend : friends) {
                            friend.setChecked(isChecked);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        showFacebookFriends();
    }

    private boolean getFacebookFriends(Response response) {
        GraphMultiResult multiResult = response.getGraphObjectAs(GraphMultiResult.class);
        if (multiResult != null) {
            GraphObjectList<GraphObject> data = multiResult.getData();
            facebookFriends = data.castToListOf(GraphUser.class);
            return true;
        }
        return false;
    }

    private void fillFacebookFriendsList(Response response) {

        boolean getFriends = getFacebookFriends(response);
        if (getFriends) {
            for (int i = 0; i < facebookFriends.size(); i++) {
                try {
                    GraphUser user = facebookFriends.get(i);
                    String url = user.getInnerJSONObject().getString(AVATAR_URL);
                    friends.add(new SocialFriendModel(user.getInnerJSONObject().getInt("uid"), user.getName(), "", url));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            adapter = new SocialFriendsAdapter(friends, FbSocialFriendActivity.this);
            friendsListView.setAdapter(adapter);
        } else {
            friendsListView.setAdapter(null);
        }
    }

    private void showFacebookFriends() {
        Bundle params = new Bundle();
        params.putString(QUERY_KEY, FACEBOOK_FQL_QUERY);
        Session session = Session.getActiveSession();
        Request request = new Request(session, FQL, params, HttpMethod.GET, new Request.Callback() {
            public void onCompleted(Response response) {
                fillFacebookFriendsList(response);
            }
        });
        Request.executeBatchAsync(request);
    }


    private void showSelectedFriends() {
        if (friends.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < friends.size(); i++) {
                if (friends.get(i).isChecked()) {
                    builder.append(friends.get(i).getId());
                    builder.append(", ");
                }
            }
            Toast.makeText(this, builder.toString(), Toast.LENGTH_LONG).show();
        }

    }

    private void openActiveSession() {
        Session.openActiveSession(this, true, new Session.StatusCallback() {

            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (session.isOpened()) {
                    Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onStateChanged() {
        checkAllCheckBox.setCheckOnlyOne(true);
        checkAllCheckBox.setChecked(false);
        checkAllCheckBox.setCheckOnlyOne(false);
    }

    @Override
    public void onClick(View view) {
        showSelectedFriends();
    }
}

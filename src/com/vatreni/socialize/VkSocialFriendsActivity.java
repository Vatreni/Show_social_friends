package com.vatreni.socialize;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import com.vatreni.Show_social_friends.R;
import com.vk.sdk.*;
import com.vk.sdk.api.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Ghaydysh Taras
 * gorthaur12@gmail.com
 */
public class VkSocialFriendsActivity extends Activity implements SocialFriendsAdapter.OnCheckChangeListener, View.OnClickListener {
    public static final String FRIENDS_GET_REQUEST = "friends.get";
    public static final String PHOTO_200_ORIG_FIELD = "photo_200_orig";
    public static final String RESPONSE_FIELD_RESPONSE = "response";
    public static final String RESPONSE_FIELD_ITEMS = "items";
    public static final String RESPONSE_FIELD_FIRST_NAME = "first_name";
    public static final String RESPONSE_FIELD_LAST_NAME = "last_name";
    public static final String RESPONSE_FIELD_ID = "id";
    private ArrayList<SocialFriendModel> friends = null;
    private ListView friendsListView;
    private SocialFriendsAdapter adapter;
    private SocialFriendsCheckBox checkAllCheckBox;
    private Button activitySocialFriendsButton;
    private static String sTokenKey = "VK_ACCESS_TOKEN";
    private static String[] sMyScope = new String[]{VKScope.FRIENDS, VKScope.WALL, VKScope.PHOTOS, VKScope.NOHTTPS};


    public static void start(Activity pActivity) {
        Intent intent = new Intent(pActivity, VkSocialFriendsActivity.class);
        pActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VKSdk.initialize(sdkListener, getResources().getString(R.string.vk_app_id), VKAccessToken.tokenFromSharedPreferences(this, sTokenKey));
        setContentView(R.layout.activity_social_friends);
        initViews();
        friends = new ArrayList<SocialFriendModel>();
    }

    private void initViews() {
        friendsListView = (ListView) findViewById(R.id.activitySocialFriendsListview);
        activitySocialFriendsButton = (Button) findViewById(R.id.activitySocialFriendsButton);
        activitySocialFriendsButton.setOnClickListener(this);
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
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
        VKAccessToken token = VKAccessToken.tokenFromSharedPreferences(this, sTokenKey);
        if (token == null || token.isExpired()) {
            VKSdk.authorize(sMyScope, true, false);
        } else {
            getVkFriends();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKUIHelper.onActivityResult(requestCode, resultCode, data);
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

    private void getVkFriends() {
        VKRequest request = new VKRequest(FRIENDS_GET_REQUEST, VKParameters.from(VKApiConst.FIELDS, PHOTO_200_ORIG_FIELD));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onError(VKError error) {
                super.onError(error);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }

            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONObject object = response.json.getJSONObject(RESPONSE_FIELD_RESPONSE);
                    JSONArray jsonArray = object.getJSONArray(RESPONSE_FIELD_ITEMS);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);
                        String firstName = c.getString(RESPONSE_FIELD_FIRST_NAME);
                        String lastName = c.getString(RESPONSE_FIELD_LAST_NAME);
                        String avatarUrl = c.getString(PHOTO_200_ORIG_FIELD);
                        int id = c.getInt(RESPONSE_FIELD_ID);
                        friends.add(new SocialFriendModel(id, firstName, lastName, avatarUrl));
                        adapter = new SocialFriendsAdapter(friends, VkSocialFriendsActivity.this);
                        adapter.setOnCheckChangeListener(VkSocialFriendsActivity.this);
                        friendsListView.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onRenewAccessToken(VKAccessToken token) {
            super.onRenewAccessToken(token);
        }

        @Override
        public void onCaptchaError(VKError captchaError) {
            new VKCaptchaDialog(captchaError).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            VKSdk.authorize(sMyScope);
        }

        @Override
        public void onAccessDenied(VKError authorizationError) {
            new AlertDialog.Builder(VkSocialFriendsActivity.this)
                    .setMessage(authorizationError.errorMessage)
                    .show();
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            newToken.saveTokenToSharedPreferences(VkSocialFriendsActivity.this, sTokenKey);
            getVkFriends();
        }
    };

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

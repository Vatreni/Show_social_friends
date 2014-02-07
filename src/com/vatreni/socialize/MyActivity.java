package com.vatreni.socialize;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.vatreni.Show_social_friends.R;

public class MyActivity extends Activity implements View.OnClickListener {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initViews();
    }

    private void initViews() {
        findViewById(R.id.activityInviteFriendsFromFbButton).setOnClickListener(this);
        findViewById(R.id.activityInviteFriendsFromVkButton).setOnClickListener(this);

    }

    @Override
    public void onClick(View pView) {
        switch (pView.getId()) {

            case R.id.activityInviteFriendsFromFbButton:
                FbSocialFriendActivity.start(MyActivity.this);
                break;
            case R.id.activityInviteFriendsFromVkButton:
                VkSocialFriendsActivity.start(MyActivity.this);
                break;
        }
    }
}

package com.vatreni.socialize;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.vatreni.Show_social_friends.R;

import java.util.List;

/**
 * Created by Ghaydysh Taras
 * gorthaur12@gmail.com
 */
public class SocialFriendsAdapter extends ArrayAdapter<SocialFriendModel> {

    private List<SocialFriendModel> mFriendList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private OnCheckChangeListener mOnStateChangeListener;

    public void setOnCheckChangeListener(OnCheckChangeListener pOnStateChangeListener) {
        mOnStateChangeListener = pOnStateChangeListener;
    }

    public SocialFriendsAdapter(List<SocialFriendModel> pFriendList, Context pContext) {
        super(pContext, R.layout.row_social_friends_list, pFriendList);
        this.mFriendList = pFriendList;
        this.mContext = pContext;
        mLayoutInflater = LayoutInflater.from(pContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.row_social_friends_list, parent, false);
            holder = new Holder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.rowSocialFriendsImageView);
            holder.textView = (TextView) convertView.findViewById(R.id.rowSocialFriendsTextView);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.rowSocialFriendsCheckBox);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        SocialFriendModel friend = mFriendList.get(position);
//        holder.imageView.setImageUrl(friend.getAvatarUrl());
        holder.textView.setText(friend.getFirstName() + " " + friend.getLastName());

        final int positionSec = position;
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFriendList.get(positionSec).setChecked(isChecked);
                if (mOnStateChangeListener != null && !isChecked) {
                    mOnStateChangeListener.onStateChanged();
                }

            }
        }

        );
        holder.checkbox.setChecked(friend.isChecked());
        return convertView;
    }

    private class Holder {
        ImageView imageView;
        TextView textView;
        CheckBox checkbox;
    }

    public interface OnCheckChangeListener {
        void onStateChanged();
    }
}

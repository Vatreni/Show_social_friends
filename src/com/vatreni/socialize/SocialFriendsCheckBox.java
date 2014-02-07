package com.vatreni.socialize;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * Created by Ghaydysh Taras
 * gorthaur12@gmail.com
 */
public class SocialFriendsCheckBox extends CheckBox {

    private boolean checkOnlyOne = false;

    public SocialFriendsCheckBox(Context context) {
        super(context);
    }

    public SocialFriendsCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCheckOnlyOne(boolean checkOnlyOne) {
        this.checkOnlyOne = checkOnlyOne;
    }

    public boolean isCheckOnlyOne() {
        return checkOnlyOne;
    }
}

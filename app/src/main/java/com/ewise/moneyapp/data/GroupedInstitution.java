package com.ewise.moneyapp.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;

import com.ewise.moneyapp.Utils.Base64ImageConverter;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 22/4/17.
 */
public class GroupedInstitution extends com.ewise.android.pdv.api.model.provider.GroupedInstitution {

    Bitmap institutionIcon;

    public GroupedInstitution (){
        super();
    }

    public Bitmap getInstitutionIcon() {
        return institutionIcon;
    }

    public void setInstitutionIcon(Bitmap institutionIcon) {
        this.institutionIcon = institutionIcon;
    }

    public void setInstitutionIcon(String base64Image) {
        this.institutionIcon =  Base64ImageConverter.fromBase64ToBitmap(base64Image);
    }

    public void setInstitutionIcon(@DrawableRes int resId, Context context) {
        this.institutionIcon =  Base64ImageConverter.fromDrawableResIDToBitmap(resId, context);
    }

    public void setInstitutionIcon(Drawable drawable, Context context) {
        this.institutionIcon =  Base64ImageConverter.fromDrawableToBitmap(drawable, context);
    }

}

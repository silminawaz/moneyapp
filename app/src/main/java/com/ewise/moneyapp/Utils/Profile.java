package com.ewise.moneyapp.Utils;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 13/4/17.
 */

@Deprecated
public class Profile {
    //holds multiple app profiles per user (e.g. "Personal", "Business" etc
    String uuid; //system generated unique identifier for profile
    String name;
    String description;
    String  base64Image;

}
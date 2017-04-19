package com.ewise.moneyapp.Utils;

import java.util.UUID;

/**
 * Created by SilmiNawaz on 13/4/17.
 */
public class SignonProfile {

    //holds multiple app profiles per user (e.g. "Personal", "Business" etc
    public String id;   //same as name without any spaces and special characters
    public String name;
    public String description;
    public String  base64Image;

}

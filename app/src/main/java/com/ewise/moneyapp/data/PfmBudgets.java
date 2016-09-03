package com.ewise.moneyapp.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SilmiNawaz on 3/9/16.
 */
public class PfmBudgets {

    /**
     * data : [{"budgetGUID":"bt-999-9a203958a91747599ba05aa3409c01ef","partyGUID":"py-999-99-menandungog","category":{"categoryGUID":"cy-000-1","categoryParentGUID":null,"categoryType":"3","name":"Auto & Transport","description":null,"partyGUID":null},"title":"2016-8-py-999-99-menandungog__cy-000-1","month":"9","year":"2016","status":"-1","spentAmount":0,"forecastAmount":0,"amount":200,"currency":"AUD","recurring":true},{"budgetGUID":"bt-999-948055cfc7af4e978f60ec4174fb9109","partyGUID":"py-999-99-menandungog","category":{"categoryGUID":"cy-000-21","categoryParentGUID":null,"categoryType":"3","name":"Uncategorized","description":null,"partyGUID":null},"title":"2016-8-py-999-99-menandungog__cy-000-21","month":"9","year":"2016","status":"-1","spentAmount":0,"forecastAmount":0,"amount":100,"currency":"AUD","recurring":true}]
     * status : success
     * code : 0000
     * message : OKAY
     * otherCodes : null
     */

    public String status;
    public String code;
    public String message;
    public Object otherCodes;
    /**
     * budgetGUID : bt-999-9a203958a91747599ba05aa3409c01ef
     * partyGUID : py-999-99-menandungog
     * category : {"categoryGUID":"cy-000-1","categoryParentGUID":null,"categoryType":"3","name":"Auto & Transport","description":null,"partyGUID":null}
     * title : 2016-8-py-999-99-menandungog__cy-000-1
     * month : 9
     * year : 2016
     * status : -1
     * spentAmount : 0
     * forecastAmount : 0
     * amount : 200
     * currency : AUD
     * recurring : true
     */

    public List<DataObject> data;

    public static PfmBudgets objectFromData(String str) {

        return new Gson().fromJson(str, PfmBudgets.class);
    }

    public static PfmBudgets objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), PfmBudgets.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<PfmBudgets> arrayPfmBudgetsFromData(String str) {

        Type listType = new TypeToken<ArrayList<PfmBudgets>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<PfmBudgets> arrayPfmBudgetsFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<PfmBudgets>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public static class DataObject {
        public String budgetGUID;
        public String partyGUID;
        /**
         * categoryGUID : cy-000-1
         * categoryParentGUID : null
         * categoryType : 3
         * name : Auto & Transport
         * description : null
         * partyGUID : null
         */

        public CategoryObject category;
        public String title;
        public String month;
        public String year;
        public String status;
        public int spentAmount;
        public int forecastAmount;
        public int amount;
        public String currency;
        public boolean recurring;

        public static DataObject objectFromData(String str) {

            return new Gson().fromJson(str, DataObject.class);
        }

        public static DataObject objectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);

                return new Gson().fromJson(jsonObject.getString(str), DataObject.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static List<DataObject> arrayDataObjectFromData(String str) {

            Type listType = new TypeToken<ArrayList<DataObject>>() {
            }.getType();

            return new Gson().fromJson(str, listType);
        }

        public static List<DataObject> arrayDataObjectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);
                Type listType = new TypeToken<ArrayList<DataObject>>() {
                }.getType();

                return new Gson().fromJson(jsonObject.getString(str), listType);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new ArrayList();


        }

        public static class CategoryObject {
            public String categoryGUID;
            public Object categoryParentGUID;
            public String categoryType;
            public String name;
            public Object description;
            public Object partyGUID;

            public static CategoryObject objectFromData(String str) {

                return new Gson().fromJson(str, CategoryObject.class);
            }

            public static CategoryObject objectFromData(String str, String key) {

                try {
                    JSONObject jsonObject = new JSONObject(str);

                    return new Gson().fromJson(jsonObject.getString(str), CategoryObject.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            public static List<CategoryObject> arrayCategoryObjectFromData(String str) {

                Type listType = new TypeToken<ArrayList<CategoryObject>>() {
                }.getType();

                return new Gson().fromJson(str, listType);
            }

            public static List<CategoryObject> arrayCategoryObjectFromData(String str, String key) {

                try {
                    JSONObject jsonObject = new JSONObject(str);
                    Type listType = new TypeToken<ArrayList<CategoryObject>>() {
                    }.getType();

                    return new Gson().fromJson(jsonObject.getString(str), listType);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return new ArrayList();


            }
        }
    }
}

package com.ewise.moneyapp.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SilmiNawaz on 4/9/16.
 */
public class InstituitionTileMapping {

    /**
     * instId : 3333
     * tileMapping : [{"accountCategory":"*","tileCategory":"BILLS"}]
     */

    public List<InstitutionTileMappingObject> InstitutionTileMapping;

    public static InstituitionTileMapping objectFromData(String str) {

        return new Gson().fromJson(str, InstituitionTileMapping.class);
    }

    public static InstituitionTileMapping objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), InstituitionTileMapping.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<InstituitionTileMapping> arrayInstituitionTileMappingFromData(String str) {

        Type listType = new TypeToken<ArrayList<InstituitionTileMapping>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<InstituitionTileMapping> arrayInstituitionTileMappingFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<InstituitionTileMapping>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public static class InstitutionTileMappingObject {
        public String instId;
        /**
         * accountCategory : *
         * tileCategory : BILLS
         */

        public List<TileMappingObject> tileMapping;

        public static InstitutionTileMappingObject objectFromData(String str) {

            return new Gson().fromJson(str, InstitutionTileMappingObject.class);
        }

        public static InstitutionTileMappingObject objectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);

                return new Gson().fromJson(jsonObject.getString(str), InstitutionTileMappingObject.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static List<InstitutionTileMappingObject> arrayInstitutionTileMappingObjectFromData(String str) {

            Type listType = new TypeToken<ArrayList<InstitutionTileMappingObject>>() {
            }.getType();

            return new Gson().fromJson(str, listType);
        }

        public static List<InstitutionTileMappingObject> arrayInstitutionTileMappingObjectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);
                Type listType = new TypeToken<ArrayList<InstitutionTileMappingObject>>() {
                }.getType();

                return new Gson().fromJson(jsonObject.getString(str), listType);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new ArrayList();


        }

        public static class TileMappingObject {
            public String accountCategory;
            public String tileCategory;

            public static TileMappingObject objectFromData(String str) {

                return new Gson().fromJson(str, TileMappingObject.class);
            }

            public static TileMappingObject objectFromData(String str, String key) {

                try {
                    JSONObject jsonObject = new JSONObject(str);

                    return new Gson().fromJson(jsonObject.getString(str), TileMappingObject.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            public static List<TileMappingObject> arrayTileMappingObjectFromData(String str) {

                Type listType = new TypeToken<ArrayList<TileMappingObject>>() {
                }.getType();

                return new Gson().fromJson(str, listType);
            }

            public static List<TileMappingObject> arrayTileMappingObjectFromData(String str, String key) {

                try {
                    JSONObject jsonObject = new JSONObject(str);
                    Type listType = new TypeToken<ArrayList<TileMappingObject>>() {
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

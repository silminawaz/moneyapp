package com.ewise.moneyapp.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SilmiNawaz on 4/9/16.
 */
public class CategoryTileMapping {

    /**
     * Singleton class to cache category tile mapping data
     * accountCategory : CASH
     * tileCategory : CASH
     */
    private static CategoryTileMapping instance = new CategoryTileMapping();

    public CategoryTileMapping(){

    }

    public static CategoryTileMapping getInstance() {

        return instance;
    }

    public List<CategoryTileMappingObject> categoryTileMapping = new ArrayList<>();

    public synchronized void loadData(Context context){

        try {

            if (this.categoryTileMapping.isEmpty()){

                //TODO: Read data by calling an API / service (at the moment the data is read from json files)
                String fileName = "CategoryTileMapping.json";

                //open file
                AssetManager assetManager = context.getAssets();
                InputStream input = assetManager.open(fileName);
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(input));
                StringBuilder jsonDataStrBuilder = new StringBuilder();
                String inputStr;
                while ((inputStr = streamReader.readLine()) != null) {
                    jsonDataStrBuilder.append(inputStr);
                }
                JSONObject jsonObject = new JSONObject(jsonDataStrBuilder.toString());
                String jsonString = jsonObject.toString();
                CategoryTileMapping object = CategoryTileMapping.objectFromData(jsonObject.toString());
                this.categoryTileMapping = object.categoryTileMapping;
                Log.d("INFO", String.format("CategoryTileMapping.loadData() done with %d mapping objects", this.categoryTileMapping.size()));
            }
        }
        catch (JSONException e)
        {
            Log.d("JSON",e.getMessage());
        }
        catch (IOException e)
        {
            Log.d("IO",e.getMessage());
        }
    }

    public AccountCardDataObject.eAccountCategory getTileCategory (Context context, String pdvAccountCategory){

        AccountCardDataObject.eAccountCategory tileAccountCategory = AccountCardDataObject.eAccountCategory.UNKNOWN;

        for (CategoryTileMappingObject mapping : this.categoryTileMapping
                ) {

            if (mapping.accountCategory.equals(pdvAccountCategory)) {
                tileAccountCategory = AccountCardDataObject.eAccountCategory.valueOf(mapping.tileCategory);
                return tileAccountCategory;
            }
        }

        return tileAccountCategory;
    }

    public static CategoryTileMapping objectFromData(String str) {

        return new Gson().fromJson(str, CategoryTileMapping.class);
    }

    public static CategoryTileMapping objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), CategoryTileMapping.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<CategoryTileMapping> arrayCategoryTileMappingFromData(String str) {

        Type listType = new TypeToken<ArrayList<CategoryTileMapping>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<CategoryTileMapping> arrayCategoryTileMappingFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<CategoryTileMapping>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public static class CategoryTileMappingObject {
        public String accountCategory;
        public String tileCategory;

        public static CategoryTileMappingObject objectFromData(String str) {

            return new Gson().fromJson(str, CategoryTileMappingObject.class);
        }

        public static CategoryTileMappingObject objectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);

                return new Gson().fromJson(jsonObject.getString(str), CategoryTileMappingObject.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static List<CategoryTileMappingObject> arrayCategoryTileMappingObjectFromData(String str) {

            Type listType = new TypeToken<ArrayList<CategoryTileMappingObject>>() {
            }.getType();

            return new Gson().fromJson(str, listType);
        }

        public static List<CategoryTileMappingObject> arrayCategoryTileMappingObjectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);
                Type listType = new TypeToken<ArrayList<CategoryTileMappingObject>>() {
                }.getType();

                List<CategoryTileMappingObject> list = new Gson().fromJson(jsonObject.getString(str), listType);

                return list;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new ArrayList();


        }
    }
}

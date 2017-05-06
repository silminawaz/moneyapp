package com.ewise.moneyapp.data;

import android.content.Context;
import android.util.Log;

import com.ewise.moneyapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SilmiNawaz on 5/9/16.
 * Builds lists of AccountCardDataObject using PdvAccountResponse data
 * This class is used to builds the account tiles in the "Account" page
 */
public class AccountCardListDataObject {


    private List<AccountCardDataObject> accountCardList = new ArrayList<>();
    //private List<CategoryTileMappingObject> categoryTileMapping = new ArrayList<>();

    Context context;

    public AccountCardListDataObject(Context context, PdvAccountResponse accountResponse, String preferredCurrencyCode){

        try
        {
            context = context;
            //build the list of cards based on the accountResponse
            for (PdvAccountResponse.AccountsObject acct : accountResponse.accounts) {
                addAccountToList(context, acct, preferredCurrencyCode);
            }
        }
        catch (NullPointerException e){
            //TODO : exception handling - No accounts to display - PdvAccountResponse data is null?
            //something went wrong
            Log.d("ERROR", "Exception : Creating Account Card List");
            throw e;
        }

        }


    private void addAccountToList (Context context, PdvAccountResponse.AccountsObject acct, String preferredCurrencyCode)
    {
        try {
            PdvAccountResponse.AccountsObject account = PdvAccountResponse.AccountsObject.clone(acct);
            Log.d("AccountCardListData...", "addAccountToList() - START");
            //get the account category from the mapping
            CategoryTileMapping mapping = CategoryTileMapping.getInstance();
            if (mapping.categoryTileMapping.isEmpty()){
                Log.d("AccountCardListData...", "addAccountToList() - Load category tile mapping");
                mapping.loadData(context);
            }

            AccountCardDataObject.eAccountCategory eAccountCategory = mapping.getTileCategory(context, account.category);

            //now get the account category related list from the items and add this account - create it if not found
            for (AccountCardDataObject accountCard : this.accountCardList
                    ) {

                if (accountCard.category == eAccountCategory) {
                    Log.d("AccountCardListData...", "addAccountToList() - Adding account to existing card="+account.accountName);
                    accountCard.addAccount(account);
                    return;
                }
            }

            //no card for this type... lets create it
            ArrayList<PdvAccountResponse.AccountsObject> accountList = new ArrayList<>();
            Log.d("AccountCardListData...", "addAccountToList() - Adding account to new tile="+account.accountName);
            accountList.add(account);
            Log.d("AccountCardListData...", "addAccountToList() - Adding New account card="+account.accountName);
            AccountCardDataObject accountCard = new AccountCardDataObject(context, eAccountCategory, accountList, preferredCurrencyCode);
            this.accountCardList.add(accountCard);
        }
        catch (Exception e)
        {
            //something went wrong
            Log.d("ERROR", "Exception : adding account to Account Card List");
            throw e;

        }
    }


    public List<AccountCardDataObject> getAccountCardList (){
        return this.accountCardList;
    }

    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat = context.getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}

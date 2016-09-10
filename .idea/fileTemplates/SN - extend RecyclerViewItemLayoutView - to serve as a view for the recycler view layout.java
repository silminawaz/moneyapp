#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end

import android.content.Context;

import android.util.Log;
import java.util.Currency;
import java.util.Locale;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import ${DATAOBJECT_PACKAGE}.${DATAOBJECT};

import com.ewise.moneyapp.views.RecyclerViewBindInterface;
import com.ewise.moneyapp.views.RecyclerViewItemLayoutView;

#parse("File Header.java")


@EViewGroup(R.layout.${LAYOUT_ID})
public class ${NAME} extends RecyclerViewItemLayoutView<${DATAOBJECT}> implements RecyclerViewBindInterface<${DATAOBJECT}> {

// TODO : Change and replace the views bound to this layout - below are just examples - you must change this to be the correct view type and id
//    @ViewById(R.id.viewid_1)
//    TextView textview;

//    @ViewById(R.id.viewid_2)
//    TextView viewid_2;

//    @ViewById(R.id.viewid_3)
//    TextView viewid_3;

//    @ViewById(R.id.viewid_4)
//    TextView viewid_4;

    public ${NAME}(Context context){
        super(context);
    }

    public void bind (${DATAOBJECT} dataObject){

        //TODO: Bind the views to the view data object values here (below are examples only - you must change them)
        // viewid_1.setText(dataObject.value1);
        // viewid_2.setText(dataObject.value2);
        // viewid_3.setText(dataObject.value3);
        // viewid_4.setText(dataObject.value4);
    }

}

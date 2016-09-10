#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end

import android.content.Context;
import android.view.ViewGroup;

import ${DATAOBJECT_PACKAGE}.${DATAOBJECT};
import com.ewise.moneyapp.views.RecyclerViewAdapterBase;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

#parse("File Header.java")

@EBean
public class ${NAME} extends RecyclerViewAdapterBase<${DATAOBJECT}, ${RECYCLER_LAYOUTVIEW_NAME}> {


        @RootContext
        Context context;

        public ${NAME} (Context context){
            this.context = context;
        }

        @Override
        protected ${RECYCLER_LAYOUTVIEW_NAME} onCreateItemView(ViewGroup parent, int viewType) {
            return ${RECYCLER_LAYOUTVIEW_NAME}_.build(context);
        }

}

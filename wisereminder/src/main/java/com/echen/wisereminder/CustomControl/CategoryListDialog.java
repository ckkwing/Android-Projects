package com.echen.wisereminder.CustomControl;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.IInterface;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.echen.wisereminder.Adapter.CategoryListAdapter;
import com.echen.wisereminder.Data.DataManager;
import com.echen.wisereminder.Model.Category;
import com.echen.wisereminder.R;

import java.util.List;

/**
 * Created by echen on 2015/10/16.
 */
public class CategoryListDialog extends DialogFragment {

    private static final String TAG = "CategoryListDialog";
    private CategorySelectedListener m_callback = null;
    private ListView m_lstCategory = null;

    public interface CategorySelectedListener{
        void onCategorySelected(Category category);
    }

    public CategoryListDialog()
    {}

    public static CategoryListDialog newInstance(CategorySelectedListener callback)
    {
        CategoryListDialog ret = new CategoryListDialog();
        ret.initialize(callback);
        return ret;
    }

    public void initialize(CategorySelectedListener callBack) {
        m_callback = callBack;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity = getActivity();
//        activity.getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setCancelable(true);
        if (savedInstanceState != null) {
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Remove title
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //1. Create view
        View view = inflater.inflate(R.layout.dialog_category_list, container,false);
        initializeExtra(view);
        return view;
    }

    private void initializeExtra(View view) {
        m_lstCategory = (ListView)view.findViewById(R.id.lvCategory);
        final List<Category> categories = DataManager.getInstance().getCategories(false);
        CategoryListAdapter adapter = new CategoryListAdapter(view.getContext(), categories);
        m_lstCategory.setAdapter(adapter);
        m_lstCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category category = categories.get(position);
                if(null != category && null != m_callback)
                {
                    dismiss();
                    m_callback.onCategorySelected(category);
                }
            }
        });
    }

}

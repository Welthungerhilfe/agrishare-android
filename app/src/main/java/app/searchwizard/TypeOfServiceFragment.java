package app.searchwizard;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import app.agrishare.BaseFragment;
import app.agrishare.MyApplication;
import app.agrishare.R;
import app.c2.android.Utils;
import app.category.CategoryActivity;
import app.category.CategoryAdapter;
import app.dao.Category;
import app.dao.Service;
import app.database.Categories;
import io.realm.RealmResults;

/**
 * Created by ernestnyumbu on 7/9/2018.
 */

public class TypeOfServiceFragment extends BaseFragment {

    ServiceAdapter adapter;
    ArrayList<Service> servicesList;

    public TypeOfServiceFragment() {
        mtag = "name";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    TypeOfServiceFragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_type_of_service, container, false);
        fragment = this;
        initViews();
        return rootView;
    }

    private void initViews(){
        listview = (ListView) rootView.findViewById(R.id.list);
        loadFromCache();

       /* (rootView.findViewById(R.id.me_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    updateForId(0);
                }
            }
        });

        (rootView.findViewById(R.id.friend_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    updateForId(1);
                }
            }
        });

        (rootView.findViewById(R.id.group_container)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    updateForId(2);
                }
            }
        });*/

    }

    private void loadFromCache(){
        adapter = null;
        servicesList = new ArrayList<>();

        RealmResults<Categories> results = MyApplication.realm.where(Categories.class)
                .equalTo("Id", ((SearchActivity) getActivity()).catergoryId)
                .findAll();

        int size = results.size();
        if (size > 0) {
            try {
                Category category = new Category(results.get(0));
                JSONArray jsonArray = new JSONArray(category.Services);
                int services_count = jsonArray.length();

                for (int i = 0; i < services_count; i++) {
                    Service service = new Service(jsonArray.getJSONObject(i));
                    servicesList.add(service);
                }

                if (adapter == null) {
                    if (getActivity() != null) {
                        adapter = new ServiceAdapter(getActivity(), servicesList, fragment);
                        listview.setAdapter(adapter);
                        Utils.setListViewHeightBasedOnChildren(listview);
                    }
                } else {
                    adapter.notifyDataSetChanged();
                    listview.setAdapter(adapter);
                    Utils.setListViewHeightBasedOnChildren(listview);
                }
            } catch (JSONException ex){
                Log("JSONEXception Services: " + ex.getMessage());
                showFeedback(R.drawable.feedback_error, getActivity().getResources().getString(R.string.error), ex.getMessage());

            }

        }
        else {
            showFeedback(R.drawable.feedback_empty, getActivity().getResources().getString(R.string.empty), getActivity().getResources().getString(R.string.no_services_available));
        }
    }

    public void updateService(Service service){
        ((SearchActivity) getActivity()).query.put("ServiceId", String.valueOf(service.Id));
        MyApplication.searchQuery.Service = service;

        if (((SearchActivity) getActivity()).mPager.getCurrentItem() < ((SearchActivity) getActivity()).NUM_PAGES - 1){
            ((SearchActivity) getActivity()).mPager.setCurrentItem(((SearchActivity) getActivity()).mPager.getCurrentItem() + 1);
        }
    }



    @Override
    public void onResume()
    {
        super.onResume();
        if (!getUserVisibleHint())
        {
            return;
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

}

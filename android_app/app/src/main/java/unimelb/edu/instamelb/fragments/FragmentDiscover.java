package unimelb.edu.instamelb.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.SearchView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import unimelb.edu.instamelb.adapters.CustomAdapter;
import unimelb.edu.instamelb.materialtest.R;
import unimelb.edu.instamelb.users.APIRequest;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDiscover extends Fragment {
    private static final String TAG = "HomeFragment";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String USERID = "0";
    private static final String SORTBYDATE="Set to 'sort by date'";
    private static final String SORTBYLOCATION="Set to 'sort by location'";

    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private static final int DATASET_COUNT = 0;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;

    protected RadioButton mLinearLayoutRadioButton;
    protected RadioButton mGridLayoutRadioButton;

    protected RecyclerView mRecyclerView;
    protected CustomAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    public static String[] mDataset;

    public static String mUsername;
    public static String mPassword;
    public static String mEmail;
    public static String mId;

    private FragmentManager manager;

    private Context mContext;

    SearchView sv;
    CustomAdapter searchAdapter;
    String[] userList;

    public FragmentDiscover() {
        // Required empty public constructor
    }

    public static FragmentDiscover newInstance(String username, String password,String userId) {
        FragmentDiscover fragment = new FragmentDiscover();
        Bundle args = new Bundle();
        args.putString(USERNAME, username);
        args.putString(PASSWORD, password);
        args.putString(USERID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        manager = getActivity().getSupportFragmentManager();
        if (getArguments() != null) {
            mUsername = getArguments().getString(USERNAME);
            mPassword = getArguments().getString(PASSWORD);
            mId =getArguments().getString(USERID);
        }

        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.
        initDataset();
        //Log.d("DATASET>>>>>>>>>>>>", Arrays.toString(mDataset));

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_discover, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case  R.id.menu_suggest:
                //TODO suggest functions
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext=container.getContext();
        View rootView = inflater.inflate(R.layout.fragment_discover, container, false);
        rootView.setTag(TAG);

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        sv = (SearchView) rootView.findViewById(R.id.searchText);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        //Log.d("DATASET>>>>>>>>>>>>", Arrays.toString(mDataset));
        mAdapter = new CustomAdapter(mDataset,mContext);
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // END_INCLUDE(initializeRecyclerView)
        initFeedData();
        mAdapter.notifyDataSetChanged();

        searchAdapter = new CustomAdapter(mDataset,mContext);
        mRecyclerView.setAdapter(searchAdapter);
        sv.setQueryHint("Search...");
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //int pos = getPosition(query);
                //mRecyclerView.scrollToPosition(pos);
                updateFeedData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateFeedData(newText);
                return true;
            }
        });

        return rootView;
    }

    public int getPosition (String query) {
        int indexOf = 0;
        for(int i=0; i < mDataset.length; i++) {
            if(mDataset[i].contains(query)) {
                indexOf = i;
            }
        }
        return indexOf;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private void initDataset() {
        mDataset = new String[DATASET_COUNT];
        for (int i = 0; i < mDataset.length; i++) {
            mDataset[i] = "This is element #" + i;
        }
    }

    private void initFeedData() {
        String[] args={mUsername, mPassword,"users","search"};
        new DownloadTask().execute(args);
    }

    private class DownloadTask extends AsyncTask<String, Integer, List<String>> {

        @Override
        protected List doInBackground(String... strings) {
            List<String> result = new ArrayList();
            Log.d("STRINGS>>>>>", String.valueOf(strings.length));
            if(strings.length == 4) {
                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>(1);
                    Log.d("PARAMS", params.toString());
                    //params.add(new BasicNameValuePair(strings[5], strings[6]));
                    //params.add(new BasicNameValuePair(strings[7], strings[8]));
                    APIRequest request = new APIRequest(strings[0], strings[1]);
                    JSONObject object = new JSONObject(request.createRequest("GETQUERY", "/"+strings[2]+"/"+strings[3], params));
                    if (object.has("result")) {
                        JSONArray feedList = object.getJSONArray("result");
                        int length = feedList.length();
                        mDataset = new String[length];
                        Log.d("LENGTH", String.valueOf(length));
                        for (int i = 0; i < length; i++) {
                            Log.d("FEEDLIST", String.valueOf(feedList.get(i)));
                            JSONObject userData = (JSONObject) feedList.get(i);
                            mDataset[i] = userData.getString("username") + "|" + userData.getString("profile_image") + "|" + userData.getString("user_id");
                            //Log.d("DATASET", mDataset[i]);
                            //JSONObject userImage = (JSONObject) userData.getString("profile_image");
                            //Log.d("USER IMAGE", String.valueOf(userImage.getString("profile_image")));
                            //User user =new User(userData.toString());
                            //feedsList.add(user);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>(1);
                    Log.d("PARAMS", params.toString());
                    //params.add(new BasicNameValuePair(strings[5], strings[6]));
                    //params.add(new BasicNameValuePair(strings[7], strings[8]));
                    APIRequest request = new APIRequest(strings[0], strings[1]);
                    JSONObject object = new JSONObject(request.createRequest("GETQUERY", "/"+strings[2]+"/"+strings[3]+"/"+strings[4], params));
                    if (object.has("result")) {
                        JSONArray feedList = object.getJSONArray("result");
                        int length = feedList.length();
                        mDataset = new String[length];
                        Log.d("LENGTH", String.valueOf(length));
                        for (int i = 0; i < length; i++) {
                            Log.d("FEEDLIST", String.valueOf(feedList.get(i)));
                            JSONObject userData = (JSONObject) feedList.get(i);
                            mDataset[i] = userData.getString("username") + "|" + userData.getString("profile_image") + "|" + userData.getString("user_id");
                            //Log.d("DATASET", mDataset[i]);
                            //JSONObject userImage = (JSONObject) userData.getString("profile_image");
                            //Log.d("USER IMAGE", String.valueOf(userImage.getString("profile_image")));
                            //User user =new User(userData.toString());
                            //feedsList.add(user);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            //Log.d("POST>>>>>>>>>>>>", "data has changed!");
            //Log.d("POSTDATASET>>>>>>>>>>>>", Arrays.toString(mDataset));
            mAdapter.notifyDataSetChanged();
            mAdapter = new CustomAdapter(mDataset,mContext);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private void updateFeedData(String query) {
        String[] args={mUsername, mPassword,"users","search","?q="+query};
        new DownloadTask().execute(args);
    }

}

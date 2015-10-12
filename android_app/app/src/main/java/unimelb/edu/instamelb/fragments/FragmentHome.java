package unimelb.edu.instamelb.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import unimelb.edu.instamelb.adapters.RecyclerViewAdapter;
import unimelb.edu.instamelb.extras.SortListener;
import unimelb.edu.instamelb.logging.L;
import unimelb.edu.instamelb.materialtest.R;
import unimelb.edu.instamelb.users.APIRequest;
import unimelb.edu.instamelb.users.Comment;
import unimelb.edu.instamelb.users.Photo;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment implements SortListener{
    private static final String TAG = "HomeFragment";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String SORTBYDATE="Set to 'sort by date'";
    private static final String SORTBYLOCATION="Set to 'sort by location'";

    public static String mUsername;
    public static String mPassword;
    public static String mEmail;
    public static boolean sort_by_date=true;
    private View mHomeView;
    private Context mContext;
    private RecyclerView recyclerView;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Photo> feedsList=new ArrayList<>();

    private  RecyclerViewAdapter adapter;
    private LinearLayoutManager layoutManager;
    private FragmentManager manager;
    private ViewGroup mGroup;

    public FragmentHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param username Parameter 1.
     * @param password Parameter 2.
     * @return A new instance of fragment FragmentSearch.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHome newInstance(String username, String password,String email) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        args.putString(USERNAME, username);
        args.putString(PASSWORD, password);
        args.putString(EMAIL, email);
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
            mEmail=getArguments().getString(EMAIL);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.menu_feed, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case  R.id.menu_feed_sort:
                if (item.getTitle().toString().equals(SORTBYLOCATION)){
                    item.setTitle(SORTBYDATE);
                }else{
                    item.setTitle(SORTBYLOCATION);
                }
                sort_by_date=!sort_by_date;
                feedsList.clear();
                adapter.notifyDataSetChanged();
                initFeedData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSortByName(){
        L.t(getActivity(), "sort name search");
    }

    @Override
    public void onSortByDate() {

    }

    @Override
    public void onSortByRating() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext=container.getContext();
        mHomeView =inflater.inflate(R.layout.fragment_home, container, false);
        mGroup=container;
        recyclerView= (RecyclerView) mHomeView.findViewById(R.id.recyclerView);
        layoutManager=new LinearLayoutManager(mContext);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter=new RecyclerViewAdapter(feedsList,mContext);
        recyclerView.setAdapter(adapter);
        initFeedData();

        swipeRefreshLayout=(SwipeRefreshLayout) mHomeView.findViewById(R.id.swipe_container);
        TypedValue typed_value = new TypedValue();
        getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
        swipeRefreshLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                feedsList.clear();
                adapter.notifyDataSetChanged();
                initFeedData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        return mHomeView;
    }

    private void initFeedData() {
        String[] args={mUsername, mPassword,"users","self","feed","sort_date",String.valueOf(sort_by_date),"sort_location",String.valueOf(!sort_by_date)};
        new DownloadTask().execute(args);
    }

    private class DownloadTask extends AsyncTask<String, Integer, List<String>> {

        @Override
        protected List doInBackground(String... strings) {
            List<String> result = new ArrayList();
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>(1);
                params.add(new BasicNameValuePair(strings[5], strings[6]));
                params.add(new BasicNameValuePair(strings[7], strings[8]));
                APIRequest request = new APIRequest(strings[0], strings[1]);
                JSONObject object = new JSONObject(request.createRequest("GETQUERY", "/"+strings[2]+"/"+strings[3]+"/"+strings[4], params));
                if (object.has("feed")) {
                    JSONArray feedList = object.getJSONArray("feed");
                    int length = feedList.length();
                    for (int i = 0; i < length; i++) {
                        ArrayList<Comment> oneCommentList=new ArrayList<>();
                        ArrayList<String> likeList=new ArrayList<>();
                        Photo photo =new Photo((JSONObject)feedList.get(i));
                        params = new ArrayList<>(1);
                        params.add(new BasicNameValuePair("photo",String.valueOf(photo.getPhoto_id())));
                        params.add(new BasicNameValuePair("comments", ""));
                        object = new JSONObject(request.createRequest("GET", "/", params));

                        if (object.has("comments")) {
                            JSONArray commentList = object.getJSONArray("comments");
                            int commlength = commentList.length();
                            for (int j = 0; j < commlength; j++) {
                                oneCommentList.add(new Comment((JSONObject) commentList.get(j)));
                            }
                        }
                        photo.setComment_list(oneCommentList);
                        params=new ArrayList<>();
                        params.add(new BasicNameValuePair("photo",String.valueOf(photo.getPhoto_id() )));
                        params.add(new BasicNameValuePair("likes", ""));
                        object = new JSONObject(request.createRequest("GET", "/", params));
                        if (object.has("likes")) {
                            JSONArray likesList = object.getJSONArray("likes");
                            int likelength = likesList.length();
                            for (int j = 0; j < likelength; j++) {
                                JSONObject oneLike= (JSONObject)likesList.get(j);
                                likeList.add(oneLike.getString("username"));
                                if (mUsername.equals(oneLike.getString("username"))) {
                                    photo.setUser_has_liked(true);
                                }
                            }
                        }
                        photo.setLiker_list(likeList);
                        feedsList.add(photo);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<String> result) {


            swipeRefreshLayout.setVisibility(View.VISIBLE);
           adapter.notifyDataSetChanged();
        }
    }
}

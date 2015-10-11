package unimelb.edu.instamelb.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import unimelb.edu.instamelb.fragments.FragmentHome;
import unimelb.edu.instamelb.materialtest.R;
import unimelb.edu.instamelb.pojo.Information;
import unimelb.edu.instamelb.users.APIRequest;
import unimelb.edu.instamelb.users.User;
import unimelb.edu.instamelb.widget.urlimageviewhelper.UrlImageViewHelper;

/**
 * Created by Windows on 22-12-2014.
 */
public class AdapterDrawer extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Information> data= Collections.emptyList();
    private static final int TYPE_HEADER=0;
    private static final int TYPE_ITEM=1;
    private LayoutInflater inflater;
    private Context context;
    private User mUser;
    private HeaderHolder headerHolder;
    public AdapterDrawer(Context context, List<Information> data){
        this.context=context;
        inflater=LayoutInflater.from(context);
        this.data=data;
    }

    public void delete(int position){
        data.remove(position);
        notifyItemRemoved(position);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==TYPE_HEADER){
            View view=inflater.inflate(R.layout.drawer_header, parent,false);
            HeaderHolder holder=new HeaderHolder(view);
            return holder;
        }
        else{
            View view=inflater.inflate(R.layout.item_drawer, parent,false);
            ItemHolder holder=new ItemHolder(view);
            return holder;
        }

    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return TYPE_HEADER;
        }
        else {
            return TYPE_ITEM;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderHolder ){
            headerHolder=(HeaderHolder) holder;
        }
        else{
            ItemHolder itemHolder= (ItemHolder) holder;
            Information current=data.get(position-1);
            itemHolder.title.setText(current.title);
            itemHolder.icon.setImageResource(current.iconId);
        }

    }
    @Override
    public int getItemCount() {
        return data.size()+1;
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;
        public ItemHolder(View itemView) {
            super(itemView);
            title= (TextView) itemView.findViewById(R.id.listText);
            icon= (ImageView) itemView.findViewById(R.id.listIcon);
        }
    }
    class HeaderHolder extends RecyclerView.ViewHolder {
        CircularImageView header_image;
        TextView header_username;
        TextView header_email;

        public HeaderHolder(View itemView) {
            super(itemView);
            header_image=(CircularImageView) itemView.findViewById(R.id.header_image_profile);
            header_email=(TextView) itemView.findViewById(R.id.header_email);
            header_username=(TextView) itemView.findViewById(R.id.header_username);
            String[] args={FragmentHome.mUsername, FragmentHome.mPassword,"users", "self"};
            //new DownloadTask().execute(args);

        }
        private class DownloadTask extends AsyncTask<String, Integer, List<String>> {
            @Override
            protected List doInBackground(String... strings) {
                List<String> result =new ArrayList<>();
                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>(1);
                    params.add(new BasicNameValuePair(strings[2], strings[3]));
                    APIRequest request = new APIRequest(strings[0],strings[1]);
                    result.add(request.createRequest("GET", "/", params));
                } catch (Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            @Override
            protected void onPostExecute(List<String>  result) {
                mUser = new User(result.get(0));

                UrlImageViewHelper.setUrlDrawable(headerHolder.header_image, mUser.getmProfileImageUrl(),
                        R.drawable.ic_contact_picture);
                headerHolder.header_email.setText(mUser.getmEmail());
                headerHolder.header_username.setText(mUser.getmUserName());

            }
        }
    }
}

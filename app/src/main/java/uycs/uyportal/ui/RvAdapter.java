package uycs.uyportal.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import uycs.uyportal.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AungKo on 10/26/2015.
 */
public class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder> {

    List<User> userDetail = new ArrayList<>();

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView nameImg;
        public TextView name;
        public TextView email;
        public TextView pwd;
        public TextView major;
        public TextView username;

        public ViewHolder(View itemView) {
            super(itemView);
            nameImg = (ImageView)itemView.findViewById(R.id.nImage);
            name = (TextView)itemView.findViewById(R.id.InameText);
            email = (TextView)itemView.findViewById(R.id.IemailText);
            pwd = (TextView)itemView.findViewById(R.id.IpasswordText);
            major = (TextView)itemView.findViewById(R.id.ImajorText);
            username = (TextView)itemView.findViewById(R.id.IuserText);
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        User user = userDetail.get(i);
        viewHolder.nameImg.setImageDrawable(user.getmNameImg());
        viewHolder.name.setText(user.getmName());
        viewHolder.username.setText(user.getmUsername());
        viewHolder.email.setText(user.getmEmail());
        viewHolder.major.setText(user.getmMajor());
        viewHolder.pwd.setText(user.getmPwd());
    }

    @Override
    public int getItemCount() {
        return userDetail.size();
    }

    RvAdapter(List<User> user){
        super();
        userDetail = user;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recyclerview_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }



}

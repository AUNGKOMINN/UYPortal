package uycs.uyportal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import uycs.uyportal.R;
import uycs.uyportal.model.posts;

/**
 * Created by Dell on 10/28/2015.
 */
public class postadapter extends RecyclerView.Adapter<postholder> {


    private List<posts> mainpost;

    public postadapter(List<posts> mainpost) {
        this.mainpost = mainpost;
        notifyDataSetChanged();
    }

    @Override
    public postholder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custompost, parent, false);
        postholder holder = new postholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(postholder holder, int position) {
        posts _newpost=mainpost.get(position);
        holder.usernames.setText(_newpost.getUsername());
        holder.contents.setText(_newpost.getStatus());
        holder.toPlaces.setText(_newpost.getToplace());
        holder.icons.setLetter(_newpost.getUsername().charAt(0));

    }

    @Override
    public int getItemCount() {
        return mainpost.size();
    }
}

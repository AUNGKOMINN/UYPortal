package uycs.uyportal.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import uycs.uyportal.R;
import uycs.uyportal.ui.StatusTextView;
import uycs.uyportal.util.FontCache;
import uycs.uyportal.util.LetterImageView;

/**
 * Created by Dell on 10/28/2015.
 */

public class postholder extends RecyclerView.ViewHolder {

    public static Context context;
    public TextView usernames,toPlaces;
    public StatusTextView contents;
    public LetterImageView icons;

    public postholder(View itemView) {
        super(itemView);

        contents=(StatusTextView) itemView.findViewById(R.id.contents);
        usernames=(TextView) itemView.findViewById(R.id.postusername);
        toPlaces=(TextView) itemView.findViewById(R.id.to);
        icons=(LetterImageView) itemView.findViewById(R.id.myicon);

        Typeface Ruthie
                = Typeface.createFromAsset(context.getAssets(), "fonts/Ruthie.ttf");
        usernames.setTypeface(Ruthie);
        Typeface RoboRegular
                = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        toPlaces.setTypeface(RoboRegular);

        Typeface RoboLight
                = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
        contents.setTypeface(RoboLight);


    }
   /* private void setCustomTypeFace(TextView v, String tf){
        v.setTypeface(FontCache.get(), getApplicationContext()));
    }*/

}

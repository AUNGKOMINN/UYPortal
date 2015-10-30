package uycs.uyportal.ui.Setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uycs.uyportal.R;

/**
 * Created by Dell on 10/29/2015.
 */
public class setting_fragment extends android.support.v4.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.setting_fragment, container, false);
        return inflatedView;
    }
}

package com.souvik.splash.detail;


import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.souvik.splash.MainActivity;
import com.souvik.splash.R;
import com.souvik.splash.util.BitmapCache;

public class DetailFragment extends DialogFragment {

    private ImageView imageView;
    private String url;
    private FloatingActionButton save;
    private boolean saveVisibility;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,
                android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, null);
        imageView = (ImageView) view.findViewById(R.id.image);
        save = (FloatingActionButton) view.findViewById(R.id.save);
        url = getArguments().getString("url");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        BitmapCache.getCache().get(url, new BitmapCache.Callback() {
            @Override
            public void success(String url, final Bitmap bitmap) {
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                saveVisibility = !saveVisibility;
                                save.setVisibility(saveVisibility ? View.VISIBLE:View.GONE);
                            }
                        });
                        save.setVisibility(View.VISIBLE);
                        saveVisibility = true;
                    }
                });

            }

            @Override
            public void error(String url, Exception e) {
                ((MainActivity)getActivity()).displayMessage("Unable to download image.");
            }
        }, false);
    }
}

package com.mxn.soul.flowingpager;


import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.IOException;
import kr.pe.burt.android.lib.androidgradientimageview.AndroidGradientImageView;



/**
 * Created by mxn on 2019/7/1.
 * MusicDetailFragment
 */

public class MusicDetailFragment extends Fragment {

    private AndroidGradientImageView albumImage  ;
    private AssetFileDescriptor fd ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fd = null;
        try {
            fd = getActivity().getAssets().openFd("test.mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_detail, container,
                false);
        albumImage = (AndroidGradientImageView) view.findViewById(R.id.iv_play);
        return  view ;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadingCover() ;
    }

    private void loadingCover() {
        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(fd.getFileDescriptor(),
                fd.getStartOffset(),fd.getLength());
        byte[] picture = mediaMetadataRetriever.getEmbeddedPicture();
        Bitmap resource= BitmapFactory.decodeByteArray(picture,0,picture.length);
        albumImage.setImageBitmap(resource);
    }


}


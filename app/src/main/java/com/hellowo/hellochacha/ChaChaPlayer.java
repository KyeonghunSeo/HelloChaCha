package com.hellowo.hellochacha;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.hellowo.hellochacha.ChaChaBoard.Cell;
import com.hellowo.hellochacha.util.FileUtil;

public class ChaChaPlayer {
    FrameLayout playerView;
    ImageView iconImage;
    TextView nameText;
    String name;
    Uri imageUri;
    Cell currentCell;
    List<ChaChaPlayer> killedPlayerList;

    public ChaChaPlayer(String name, Uri imageUri) {
        this.name = name;
        this.imageUri = imageUri;
        Log.d("aaa", name + "/" + imageUri);
        killedPlayerList = new ArrayList<>();
    }

    public void addPlayerView(FrameLayout rootView, int cellSize) {
        LayoutInflater inflater = LayoutInflater.from(rootView.getContext());
        playerView = (FrameLayout)inflater.inflate(R.layout.item_player, null, false);
        playerView.setLayoutParams(new FrameLayout.LayoutParams(cellSize, cellSize));
        nameText = (TextView)playerView.findViewById(R.id.nameText);
        iconImage = (ImageView)playerView.findViewById(R.id.iconImage);

        nameText.setText(name);

        Glide.with(rootView.getContext())
                .load(R.mipmap.ic_launcher)
                .into(iconImage);

        playerView.setVisibility(View.GONE);
        rootView.bringChildToFront(playerView);
        rootView.addView(playerView);
    }

    public void removeView(FrameLayout rootView) {
        rootView.removeView(playerView);
    }

    public void move(Cell cell) {
        playerView.setVisibility(View.VISIBLE);
        currentCell = cell;
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(
                ObjectAnimator.ofFloat(playerView, "translationX",
                        playerView.getTranslationX(),
                        cell.imageView.getTranslationX())
                        .setDuration(250),
                ObjectAnimator.ofFloat(playerView, "translationY",
                        playerView.getTranslationY(),
                        cell.imageView.getTranslationY())
                        .setDuration(250)
        );
        animSet.setInterpolator(new FastOutSlowInInterpolator());
        animSet.start();
    }
}

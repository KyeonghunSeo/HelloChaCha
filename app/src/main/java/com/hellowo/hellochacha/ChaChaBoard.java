package com.hellowo.hellochacha;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ChaChaBoard {
    Context context;
    FrameLayout rootView;
    List<ChaChaPlayer> playerList;
    List<Chip> hidingCellList;
    List<Cell> pathCellList;

    int[] characterImageIds;

    public ChaChaBoard(FrameLayout frameLayout) {
        context = frameLayout.getContext();
        rootView = frameLayout;
        playerList = new ArrayList<>();
        hidingCellList = new ArrayList<>();
        pathCellList = new ArrayList<>();
        /*
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        draw();
                    }
                });
                */
    }

    public void initCells(int[] characterImageIds, int level) {
        for(Chip chip : hidingCellList) {
            chip.removeView(rootView);
        }

        for(Cell cell : pathCellList) {
            cell.removeView(rootView);
        }

        hidingCellList.clear();
        pathCellList.clear();

        this.characterImageIds = characterImageIds;
        int hidingCellCount = characterImageIds.length;

        for (int i = 0; i < hidingCellCount; i++) {
            Chip chip = new Chip(i, rootView);
            hidingCellList.add(chip);
        }

        for (int i = 0; i < hidingCellCount * level; i++) {
            Cell cell = new Cell(i % hidingCellCount, rootView);
            pathCellList.add(cell);
        }
    }

    public void addPlayer(ChaChaPlayer player) {
        playerList.add(player);
    }

    private void drawBoard() {
        int width = rootView.getWidth();
        int height = rootView.getHeight();

        int widthCount = pathCellList.size() / 3;
        int heightCount = ( pathCellList.size() - ( widthCount * 2 ) ) / 2;
        int cellSize = height / (heightCount + 2);
        int widthOffset = width / widthCount;
        int widthMargin = (widthOffset - cellSize) / 2;

        for (int i = 0; i < pathCellList.size(); i++) {
            Cell cell = pathCellList.get(i);

            cell.imageView.setLayoutParams(new FrameLayout.LayoutParams(cellSize, cellSize));

            if(i < widthCount) {

                cell.imageView.setTranslationX(i * widthOffset + widthMargin);

            }else if(i >= widthCount && i < widthCount + heightCount){

                cell.imageView.setTranslationX(width - cellSize);
                cell.imageView.setTranslationY((i - widthCount + 1) * cellSize);

            }else if(i >= widthCount + heightCount && i < widthCount * 2 + heightCount){

                cell.imageView.setTranslationX(
                        width - (i - (widthCount + heightCount) + 1) * widthOffset + widthMargin);
                cell.imageView.setTranslationY(height - cellSize);

            }else {

                cell.imageView.setTranslationY(
                        height - (i - (widthCount * 2 + heightCount) + 2) * cellSize);

            }
        }

        int chipHeightCount = 3;
        if(hidingCellList.size() < 10) {
            chipHeightCount = 2;
        }
        int chipWidthCount = hidingCellList.size() / chipHeightCount;
        int chipsWidthPos = (width - chipWidthCount * cellSize) / 2;
        int chipHeightPos = (height - chipHeightCount * cellSize) / 2;

        for (int i = 0; i < hidingCellList.size(); i++) {
            Chip chip = hidingCellList.get(i);

            chip.frontView.setLayoutParams(new FrameLayout.LayoutParams(cellSize, cellSize));
            chip.backView.setLayoutParams(new FrameLayout.LayoutParams(cellSize, cellSize));

            chip.frontView.setTranslationX(chipsWidthPos + (i % chipWidthCount * cellSize));
            chip.backView.setTranslationX(chipsWidthPos + (i % chipWidthCount * cellSize));
            chip.frontView.setTranslationY(chipHeightPos + (i / chipWidthCount * cellSize));
            chip.backView.setTranslationY(chipHeightPos + (i / chipWidthCount * cellSize));
        }
    }

    public void startGame() {
        drawBoard();
    }

    class Cell {
        ImageView imageView;
        int id;

        public Cell(int id, FrameLayout rootView) {
            this.id = id;
            imageView = new ImageView(rootView.getContext());
            imageView.setImageResource(characterImageIds[id]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            rootView.addView(imageView);
        }

        public void removeView(FrameLayout rootView) {
            rootView.removeView(imageView);
        }
    }

    class Chip {
        ImageView frontView;
        ImageView backView;
        int id;
        boolean isOpened;

        public Chip(int id, FrameLayout rootView) {
            this.id = id;
            frontView = new ImageView(rootView.getContext());
            frontView.setImageResource(characterImageIds[id]);
            frontView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            frontView.setVisibility(View.GONE);
            rootView.addView(frontView);

            backView = new ImageView(rootView.getContext());
            backView.setImageResource(R.mipmap.ic_launcher);
            backView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            rootView.addView(backView);
        }

        public void removeView(FrameLayout rootView) {
            rootView.removeView(frontView);
            rootView.removeView(backView);
        }
    }
}

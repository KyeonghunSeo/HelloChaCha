package com.hellowo.hellochacha;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChaChaBoard {
    Context context;
    FrameLayout rootView;
    List<ChaChaPlayer> playerList;
    List<Chip> chipList;
    List<Cell> cellList;
    Cell headerCell;
    ChaChaTurn turn;
    int[] characterImageIds;

    boolean isOpenedChip;
    int widthCount;
    int heightCount;
    int cellSize;
    int widthOffset;
    int widthMargin;

    int chipHeightCount = 3;
    int chipWidthCount;
    int chipsWidthPos;
    int chipHeightPos;

    public ChaChaBoard(FrameLayout frameLayout) {
        context = frameLayout.getContext();
        rootView = frameLayout;
        playerList = new ArrayList<>();
        chipList = new ArrayList<>();
        cellList = new ArrayList<>();
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
        this.characterImageIds = characterImageIds;
        int hidingCellCount = characterImageIds.length;

        for (int i = 0; i < hidingCellCount; i++) {
            Chip chip = new Chip(i, rootView);
            chipList.add(chip);
        }

        headerCell = new Cell(0, rootView);
        cellList.add(headerCell);
        Cell prevCell = headerCell;

        for (int i = 1; i < hidingCellCount * level; i++) {
            Cell cell = new Cell(i % hidingCellCount, rootView);
            cellList.add(cell);
            prevCell.nextCell = cell;
            prevCell = cell;
        }

        prevCell.nextCell = headerCell;
    }

    private void resetGame() {
        for(Chip chip : chipList) {
            chip.removeView(rootView);
        }

        for(Cell cell : cellList) {
            cell.removeView(rootView);
        }

        for(ChaChaPlayer player : playerList) {
            player.removeView(rootView);
        }

        chipList.clear();
        cellList.clear();
        playerList.clear();
    }

    public void addPlayer(ChaChaPlayer player) {
        player.addPlayerView(rootView, cellSize);
        playerList.add(player);
    }

    public void startGame() {
        onBoardPlayers();
        startMatch();
    }

    public void drawBoard() {
        int width = rootView.getWidth();
        int height = rootView.getHeight();

        widthCount = cellList.size() / 3;
        heightCount = ( cellList.size() - ( widthCount * 2 ) ) / 2;
        cellSize = height / (heightCount + 2);
        widthOffset = width / widthCount;
        widthMargin = (widthOffset - cellSize) / 2;

        for (int i = 0; i < cellList.size(); i++) {
            Cell cell = cellList.get(i);
            cell.imageView.setVisibility(View.VISIBLE);

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

        chipHeightCount = 3;
        if(chipList.size() < 10) {
            chipHeightCount = 2;
        }
        chipWidthCount = chipList.size() / chipHeightCount;
        chipsWidthPos = (width - chipWidthCount * cellSize) / 2;
        chipHeightPos = (height - chipHeightCount * cellSize) / 2;

        for (int i = 0; i < chipList.size(); i++) {
            Chip chip = chipList.get(i);
            chip.frontView.setVisibility(View.GONE);
            chip.backView.setVisibility(View.VISIBLE);

            chip.frontView.setLayoutParams(new FrameLayout.LayoutParams(cellSize, cellSize));
            chip.backView.setLayoutParams(new FrameLayout.LayoutParams(cellSize, cellSize));

            chip.frontView.setTranslationX(chipsWidthPos + (i % chipWidthCount * cellSize));
            chip.backView.setTranslationX(chipsWidthPos + (i % chipWidthCount * cellSize));
            chip.frontView.setTranslationY(chipHeightPos + (i / chipWidthCount * cellSize));
            chip.backView.setTranslationY(chipHeightPos + (i / chipWidthCount * cellSize));
        }
    }

    private void onBoardPlayers() {
        int initInterval = cellList.size() / playerList.size();
        for (int i = 0; i < playerList.size(); i++) {
            playerList.get(i).move(cellList.get(i * initInterval));
        }
    }

    private void startMatch() {
        turn = new ChaChaTurn();
        turn.playerIndex = 0;
    }

    class Cell {
        Cell nextCell;
        ImageView imageView;
        int id;

        public Cell(int id, FrameLayout rootView) {
            this.id = id;
            imageView = new ImageView(rootView.getContext());
            imageView.setImageResource(characterImageIds[id]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setVisibility(View.GONE);
            rootView.addView(imageView);
        }

        public void removeView(FrameLayout rootView) {
            rootView.removeView(imageView);
        }

        public Cell getNextCell() {
            return nextCell;
        }
    }

    class Chip {
        ImageView frontView;
        ImageView backView;
        int id;

        final Handler closeHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                close();
            }
        };

        public Chip(final int id, FrameLayout rootView) {
            this.id = id;
            frontView = new ImageView(rootView.getContext());
            frontView.setImageResource(characterImageIds[id]);
            frontView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            frontView.setVisibility(View.GONE);
            rootView.addView(frontView);

            backView = new ImageView(rootView.getContext());
            backView.setImageResource(R.mipmap.ic_launcher);
            backView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            backView.setVisibility(View.GONE);
            rootView.addView(backView);

            backView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!isOpenedChip) {
                        open();

                        ChaChaPlayer player = getCurrentTurnPlayer();
                        Cell nextCell = player.currentCell.getNextCell();

                        if(id == nextCell.id) {
                            player.move(nextCell);
                        }else{

                        }
                    }
                }
            });
        }

        private void open() {
            isOpenedChip = true;

            Animator anim = ObjectAnimator
                    .ofFloat(backView, "rotationY", 0f, 90f)
                    .setDuration(125);

            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {}
                @Override
                public void onAnimationEnd(Animator animator) {
                    backView.setVisibility(View.GONE);
                    frontView.setVisibility(View.VISIBLE);
                    ObjectAnimator
                            .ofFloat(frontView, "rotationY", 270f, 360f)
                            .setDuration(125).start();
                    closeHandler.sendEmptyMessageDelayed(0, 500);
                }
                @Override
                public void onAnimationCancel(Animator animator) {}
                @Override
                public void onAnimationRepeat(Animator animator) {}
            });

            anim.start();
        }

        private void close() {
            isOpenedChip = false;

            Animator anim = ObjectAnimator
                    .ofFloat(frontView, "rotationY", 0f, 90f)
                    .setDuration(125);

            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {}
                @Override
                public void onAnimationEnd(Animator animator) {
                    frontView.setVisibility(View.GONE);
                    backView.setVisibility(View.VISIBLE);
                    ObjectAnimator
                            .ofFloat(backView, "rotationY", 270f, 360f)
                            .setDuration(125).start();
                }
                @Override
                public void onAnimationCancel(Animator animator) {}
                @Override
                public void onAnimationRepeat(Animator animator) {}
            });

            anim.start();
        }

        public void removeView(FrameLayout rootView) {
            rootView.removeView(frontView);
            rootView.removeView(backView);
        }
    }

    private ChaChaPlayer getCurrentTurnPlayer() {
        return playerList.get(turn.playerIndex);
    }
}

package com.hellowo.hellochacha;

import java.util.ArrayList;
import java.util.List;

public class ChaChaPlayer {
    String name;
    int color;
    int currentPosition;
    List<ChaChaPlayer> killedPlayerList;

    public ChaChaPlayer(String name) {
        this.name = name;
        killedPlayerList = new ArrayList<>();
    }
}

package radial;

import java.awt.Color;

public class KeyGrouping {
    private Color color;
    private String keys;
    private String nextKeys;
    private KeyGrouping swapGrouping;
    private boolean swapped = false;
    

    public KeyGrouping(Color color, String keys) {
        this.color = color;
        this.keys = keys;
    }

    public Color getColor() {
        return this.color;
    }

    public String getKeys() {
        return this.keys;
    }

    public String getNextKeys() {
        return this.nextKeys;
    }
    
    public KeyGrouping getSwapGrouping() {
        return this.swapGrouping;
    }
    
    public boolean isSwapped() {
        return this.swapped;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public void setNextKeys(String keys) {
        this.nextKeys = keys;
    }
    
    public void setSwapGrouping(KeyGrouping grouping) {
        this.swapGrouping = grouping;
        this.swapped = true;
    }
}

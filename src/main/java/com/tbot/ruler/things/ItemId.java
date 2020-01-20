package com.tbot.ruler.things;

public class ItemId {

    private String value;

    public ItemId(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object another) {
        return (another instanceof ItemId)
            && ((ItemId) another).value.equals(value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public String getValue() {
        return this.value;
    }
}

package com.example.mysteriousresurgence.capability;

import net.minecraft.nbt.CompoundTag;

public class Sanity implements ISanity {
    private float sanity = 100.0F;
    private float maxSanity = 100.0F;

    @Override
    public float getSanity() {
        return sanity;
    }

    @Override
    public void setSanity(float sanity) {
        this.sanity = Math.max(0, Math.min(maxSanity, sanity));
    }

    @Override
    public void addSanity(float amount) {
        this.setSanity(this.sanity + amount);
    }

    @Override
    public void consumeSanity(float amount) {
        this.setSanity(this.sanity - amount);
    }

    @Override
    public float getMaxSanity() {
        return maxSanity;
    }

    @Override
    public void setMaxSanity(float maxSanity) {
        this.maxSanity = maxSanity;
    }

    public void copyFrom(Sanity source) {
        this.sanity = source.sanity;
        this.maxSanity = source.maxSanity;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putFloat("sanity", sanity);
        nbt.putFloat("max_sanity", maxSanity);
    }

    public void loadNBTData(CompoundTag nbt) {
        sanity = nbt.getFloat("sanity");
        maxSanity = nbt.getFloat("max_sanity");
    }
}

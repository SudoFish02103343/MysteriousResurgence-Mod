package com.example.mysteriousresurgence.capability;

public interface ISanity {
    float getSanity();
    void setSanity(float sanity);
    void addSanity(float amount);
    void consumeSanity(float amount);
    float getMaxSanity();
    void setMaxSanity(float maxSanity);
}

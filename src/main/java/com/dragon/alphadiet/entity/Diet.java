package com.dragon.alphadiet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2017/3/22.
 */
@Entity
public class Diet implements Parcelable {
    @Id
    private String dietId;
    private String dietName;
    private int dietWeight;
    private String dietType;
    private String dietDate;

    public String getDietId() {
        return dietId;
    }

    public void setDietId(String dietId) {
        this.dietId = dietId;
    }

    public String getDietName() {
        return dietName;
    }

    public void setDietName(String dietName) {
        this.dietName = dietName;
    }

    public int getDietWeight() {
        return dietWeight;
    }

    public void setDietWeight(int dietWeight) {
        this.dietWeight = dietWeight;
    }

    public String getDietType() {
        return dietType;
    }

    public void setDietType(String dietType) {
        this.dietType = dietType;
    }

    public String getDietDate() {
        return dietDate;
    }

    public void setDietDate(String dietDate) {
        this.dietDate = dietDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.dietId);
        dest.writeString(this.dietName);
        dest.writeInt(this.dietWeight);
        dest.writeString(this.dietType);
        dest.writeString(this.dietDate);
    }

    public Diet() {
    }

    protected Diet(Parcel in) {
        this.dietId = in.readString();
        this.dietName = in.readString();
        this.dietWeight = in.readInt();
        this.dietType = in.readString();
        this.dietDate = in.readString();
    }

    @Generated(hash = 407708604)
    public Diet(String dietId, String dietName, int dietWeight, String dietType,
            String dietDate) {
        this.dietId = dietId;
        this.dietName = dietName;
        this.dietWeight = dietWeight;
        this.dietType = dietType;
        this.dietDate = dietDate;
    }

    public static final Parcelable.Creator<Diet> CREATOR = new Parcelable.Creator<Diet>() {
        @Override
        public Diet createFromParcel(Parcel source) {
            return new Diet(source);
        }

        @Override
        public Diet[] newArray(int size) {
            return new Diet[size];
        }
    };
}

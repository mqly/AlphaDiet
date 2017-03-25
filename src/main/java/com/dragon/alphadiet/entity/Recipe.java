package com.dragon.alphadiet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/23.
 */
@Entity
public class Recipe implements Parcelable,Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String recipeId;
    private String recipeImage;
    private String recipeName;
    private String recipeMaterial;
    private String recipeStep;
    private String recipeType;

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeImage() {
        return recipeImage;
    }

    public void setRecipeImage(String recipeImage) {
        this.recipeImage = recipeImage;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeMaterial() {
        return recipeMaterial;
    }

    public void setRecipeMaterial(String recipeMaterial) {
        this.recipeMaterial = recipeMaterial;
    }

    public String getRecipeStep() {
        return recipeStep;
    }

    public void setRecipeStep(String recipeStep) {
        this.recipeStep = recipeStep;
    }

    public String getRecipeType() {
        return recipeType;
    }

    public void setRecipeType(String recipeType) {
        this.recipeType = recipeType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.recipeId);
        dest.writeString(this.recipeImage);
        dest.writeString(this.recipeName);
        dest.writeString(this.recipeMaterial);
        dest.writeString(this.recipeStep);
        dest.writeString(this.recipeType);
    }

    public Recipe() {
    }

    protected Recipe(Parcel in) {
        this.recipeId = in.readString();
        this.recipeImage = in.readString();
        this.recipeName = in.readString();
        this.recipeMaterial = in.readString();
        this.recipeStep = in.readString();
        this.recipeType = in.readString();
    }

    @Generated(hash = 488429816)
    public Recipe(String recipeId, String recipeImage, String recipeName, String recipeMaterial,
            String recipeStep, String recipeType) {
        this.recipeId = recipeId;
        this.recipeImage = recipeImage;
        this.recipeName = recipeName;
        this.recipeMaterial = recipeMaterial;
        this.recipeStep = recipeStep;
        this.recipeType = recipeType;
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}

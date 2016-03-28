package com.echen.wisereminder.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by echen on 2016/3/1.
 */
public class Test implements Parcelable {
    private int age;
    private String name;
    private String gender;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.age);
        dest.writeString(this.name);
        dest.writeString(this.gender);
    }

    public Test() {
    }

    protected Test(Parcel in) {
        this.age = in.readInt();
        this.name = in.readString();
        this.gender = in.readString();
    }

    public static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {
        public Test createFromParcel(Parcel source) {
            return new Test(source);
        }

        public Test[] newArray(int size) {
            return new Test[size];
        }
    };
}

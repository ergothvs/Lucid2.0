package server;

import java.util.List;
import java.util.ArrayList;

import client.inventory.Item;

public class MerchItemPackage {

    private long lastsaved;
    private long mesos = 0;
    private int packageid;
    private List<Item> items = new ArrayList<>();

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setSavedTime(long lastsaved) {
        this.lastsaved = lastsaved;
    }

    public long getSavedTime() {
        return lastsaved;
    }

    public long getMesos() {
        return mesos;
    }

    public void setMesos(long set) {
        mesos = set;
    }

    public int getPackageid() {
        return packageid;
    }

    public void setPackageid(int packageid) {
        this.packageid = packageid;
    }
}

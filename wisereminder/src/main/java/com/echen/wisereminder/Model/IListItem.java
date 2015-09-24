package com.echen.wisereminder.Model;

import java.util.List;

/**
 * Created by echen on 2015/6/4.
 */
public interface IListItem {
    String getName();
    void setName(String name);
    List<IListItem> getChildren();

//    void setChildren(List<IListItem> children);
}

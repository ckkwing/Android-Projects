package com.echen.androidcommon.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echen on 2015/2/12.
 */
public class Folder extends FileSystemInfo {
    private List<FileSystemInfo> children = new ArrayList<>();

    public Folder()
    {
        super();
    }

    public Folder(String path)
    {
        this.path = path;
        this.displayName = getFolderName(this.path);
    }

    private String getFolderName(String path)
    {
        return  path.substring(path.lastIndexOf("/")+1, path.length());
    }

    public List<FileSystemInfo> getChildren() {return children;}
}

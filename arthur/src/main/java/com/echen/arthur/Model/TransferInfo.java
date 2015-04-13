package com.echen.arthur.Model;

import java.io.File;
import java.io.Serializable;

/**
 * Created by echen on 2015/3/18.
 */
public class TransferInfo implements Serializable {

    protected File sourceFile = null;
    protected boolean isForceTransfer = false;

    public TransferInfo(File file)
    {
        this.sourceFile = file;
    }

    public File getSourceFile() { return sourceFile; }
    public void setSourceFile(File file) { this.sourceFile = file; }

    public boolean getIsForceTransfer() { return isForceTransfer; }
    public void setForceTransfer(boolean isForceTransfer) { this.isForceTransfer = isForceTransfer; }
}

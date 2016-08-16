package com.my.FetchData;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by jiazhang on 5/31/16.
 */
@SuppressLint({"NewApi", "ValidFragment"})
public class Operator extends DialogFragment {
    public enum Op {
        EDIT,
        DELETE
    }

    interface Observer {
        public void onOperation(Op p, int id);
    }

    private Observer mObserver;
    public int mId;

    public Operator(Observer ob, int id) {
        mObserver = ob;
        mId = id;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(new String[]{"Edit", "Delete"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mObserver != null) {
                            mObserver.onOperation(i == 0 ? Op.EDIT : Op.DELETE, mId);
                        }
                    }
                });
        return builder.create();
    }
}

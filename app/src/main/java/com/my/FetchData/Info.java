package com.my.FetchData;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

/**
 * Created by jiazhang on 5/30/16.
 */
@SuppressLint({"NewApi", "ValidFragment"})
public class Info extends DialogFragment {
    public enum Type {
        ADD,
        EDIT,
        SEARCH
    }

    interface Listener {
        public void onOK(String title, String url, Type type, boolean includeHistory);
    }

    private Listener mListener;
    private Type mType;
    private String mTitle = "title";
    private String mUrl = "url";

    @SuppressLint({"NewApi", "ValidFragment"})
    public Info(Type type, Listener l) {
        mType = type;
        mListener = l;
    }

    @SuppressLint({"NewApi", "ValidFragment"})
    public Info(String title, String url, Type type, Listener l) {
        mType = type;
        mTitle = title;
        mUrl = url;
        mListener = l;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceStates) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.info, null);
        final EditText title = (EditText) dialogView.findViewById(R.id.title);
        final EditText url = (EditText) dialogView.findViewById(R.id.url);
        final RadioButton incHsy = (RadioButton) dialogView.findViewById(R.id.incHsy);

        title.setHint(mTitle);
        url.setHint(mUrl);

        if (mType == Type.SEARCH) {
            title.setVisibility(View.GONE);
            url.setHint("search");
            incHsy.setVisibility(View.VISIBLE);
        }
        if (mType == Type.EDIT) {
            url.setEnabled(false);
        }

        builder.setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (mListener != null) {
                            String t = title.getText().toString();
                            String u = url.getText().toString();
                            if (TextUtils.isEmpty(t)) {
                                t = mTitle;
                            }
                            if (TextUtils.isEmpty(u)) {
                                u = mUrl;
                            }
                            mListener.onOK(t, u, mType, incHsy.isChecked());
                        }
                    }
                });
        return builder.create();
    }
}

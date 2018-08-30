package com.tgi.librarygooglelogin.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tgi.librarygooglelogin.R;

public class AlertDialogUtil {

    private static AlertDialog alertDialog;

    public static void displayUserCode(final Context context, final String userCode){
        View rootView=View.inflate(
                context,
                R.layout.dialog_display_user_code,
                null
        );
        TextView tvUserCode=rootView.findViewById(R.id.dialog_display_user_code_tv_code);
        Button btnCopy=rootView.findViewById(R.id.dialog_display_user_code_btn_copy);
        tvUserCode.setText(userCode);
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager manager= (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                manager.setText(userCode);
                alertDialog.dismiss();
            }
        });

        alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.title_your_user_code))
                .setView(rootView)
                .setCancelable(false)
                .create();
        alertDialog.show();
    }
}

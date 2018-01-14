package com.mofa.metropolia.architectmuseo.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.WindowManager;

import com.mofa.metropolia.architectmuseo.R;

/**
 * Created by wenhao on 25.11.2017.
 */

public class DialogHelper {

    public static ProgressDialog createProgressDialog(Context mContext){
        ProgressDialog result = new ProgressDialog(mContext);
        try {
            result.show();
            result.setCancelable(true);
            result.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            result.setContentView(R.layout.layout_progress_dialog);
            result.dismiss();
        }
        catch (NullPointerException npe){
            npe.printStackTrace();
        }

        return result;
    }

}

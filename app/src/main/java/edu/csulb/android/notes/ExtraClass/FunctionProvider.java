package edu.csulb.android.notes.ExtraClass;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;


/**
 * Created by KEYUR on 25-02-2016.
 */

public class FunctionProvider {
    public static void toastIt(Context context, String str){
        Toast toast = Toast.makeText(context, str,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
        toast.show();
    }


    public static void toastItBoottom(Context context, String str){
        Toast toast = Toast.makeText(context, str,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
        toast.show();
    }

}

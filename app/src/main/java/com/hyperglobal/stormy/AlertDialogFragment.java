package com.hyperglobal.stormy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by bmac on 11/04/2015.
 */
public class AlertDialogFragment extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity(); // get the activity that threw the call
        AlertDialog.Builder builder = new AlertDialog.Builder(context) // build the dialog
                .setTitle(context.getString(R.string.error_title)) // set the title
                .setMessage(context.getString(R.string.error_message)) // set the message
                .setPositiveButton(context.getString(R.string.error_ok_button_text), null); // add a button

        AlertDialog dialog = builder.create(); // create the built dialog
        return dialog; // return it
    }

}

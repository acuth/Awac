package acuth.awac;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by adrian on 02/03/15.
 */
public class AlertFragment extends DialogFragment {
    private Awac mAwac;

    private String mAction;
    private String mMsg;
    private String mOKLabel;
    private String mCancelLabel;
    private String mOKAction;
    private String mCancelAction;

    public void init(String msg,String okLabel,String cancelLabel,String action,String okAction,String cancelAction) {
        mMsg = msg;
        mOKLabel = okLabel;
        mCancelLabel = cancelLabel;
        mAction = action;
        mOKAction = okAction;
        mCancelAction = cancelAction;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mAwac = (Awac) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Expected Awac activity.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAwac = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mMsg)
                .setPositiveButton(mOKLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mAwac.onAction(mAction+":"+mOKAction);
                    }
                })
                .setNegativeButton(mCancelLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mAwac.onAction(mAction+":"+mCancelAction);
                    }
                });
        return builder.create();
    }

    @Override
    /**
     * Cope with the case the user cancels the dialog by clicking outside it
     */
    public void onCancel(DialogInterface dialog) {
        mAwac.onAction(mAction+":"+mCancelAction);
        super.onCancel(dialog);
    }
}
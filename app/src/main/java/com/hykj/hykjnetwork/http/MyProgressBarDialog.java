package com.hykj.hykjnetwork.http;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.hykj.network.R;
import com.hykj.network.utils.ReflectUtils;

/**
 * 进度条DialogFragment
 */
public class MyProgressBarDialog extends DialogFragment {
    private static final String TAG = MyProgressBarDialog.class.getSimpleName();
    FragmentActivity mActivity;
    private String message;
    private boolean isCancel = false;
    private ProgressCancelListener progressCancelListener;

    public MyProgressBarDialog init(FragmentActivity activity) {
        this.mActivity = activity;
        return this;
    }

    public MyProgressBarDialog setCancel(boolean cancel) {
        isCancel = cancel;
        if (getDialog() != null) {
            getDialog().setCancelable(isCancel);
        }
        return this;
    }

    public MyProgressBarDialog setProgressCancelListener(ProgressCancelListener progressCancelListener) {
        this.progressCancelListener = progressCancelListener;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), R.style.CustomDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            int size = 200;
            if (mActivity != null) {
                size = size2px(mActivity, TypedValue.COMPLEX_UNIT_DIP, 100);
            }
            window.setLayout(size, size);
            window.setGravity(Gravity.CENTER);
        }
        getDialog().setCancelable(isCancel);
        getDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (progressCancelListener != null)
                    progressCancelListener.onCancelListener();
            }
        });
        getDialog().setCanceledOnTouchOutside(false);
    }

    public MyProgressBarDialog setData(String message) {
        this.message = message;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_progress_bar_circle, container, false);
        if (message != null)
            ((TextView) view.findViewById(R.id.tv_msg)).setText(message);
        view.findViewById(R.id.tv_msg).setVisibility(TextUtils.isEmpty(message) ? View.GONE : View.VISIBLE);
        return view;
    }

    /**
     * 显示弹窗
     *
     * @param message 弹窗信息
     */
    public void showProgress(String message) {
        if (mActivity != null) {
            this.message = message;
            FragmentManager manager = mActivity.getSupportFragmentManager();
            if (!isAdded()) {
                if (manager.findFragmentByTag(TAG) == null || manager.findFragmentByTag(TAG) != this) {
                    ReflectUtils.setFieldValue(this, "mDismissed", false);
                    ReflectUtils.setFieldValue(this, "mShownByMe", true);
                    ReflectUtils.setFieldValue(this, "mViewDestroyed", false);
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.add(this, TAG);
                    ft.commitAllowingStateLoss();
                }
            }
        }
    }

    @Override
    public void dismiss() {
        if (mActivity != null && !mActivity.isFinishing() && isAdded())
            super.dismiss();
    }

    public static int size2px(Context context, int unit, int size) {
        return (int) TypedValue.applyDimension(unit, size, context.getResources().getDisplayMetrics());
    }

    public interface ProgressCancelListener {
        /**
         * 　然而,注意对话框也可以被"取消". 这是一个特殊的情形, 它意味着对话框被用户显式的取消掉.
         * 这将在用户按下"back"键时, 或者对话框显式的调用cancel()(按下对话框的cancel按钮)时发生. 当一个对话框被取消时,
         * OnDismissListener将仍然被通知, 但如果你希望在对话框被显示取消(而不是正常解除)时被通知,
         * 则你应该使用setOnCancelListener()注册一个DialogInterface.OnCancelListener.
         */
        void onCancelListener();
    }
}

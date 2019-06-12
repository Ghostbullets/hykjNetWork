package com.hykj.network.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IntDef;
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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 进度条DialogFragment
 */
public class ProgressBarDialog extends DialogFragment {
    private static final String TAG = ProgressBarDialog.class.getSimpleName();
    FragmentActivity mActivity;
    private String message;
    private boolean isCancel = false;
    private ProgressCancelListener progressCancelListener;
    private Integer backgroundType;
    private Object backgroundResource;

    public ProgressBarDialog init(FragmentActivity activity) {
        this.mActivity = activity;
        return this;
    }

    public ProgressBarDialog setCancel(boolean cancel) {
        isCancel = cancel;
        if (getDialog() != null) {
            getDialog().setCancelable(isCancel);
        }
        return this;
    }

    public ProgressBarDialog setProgressCancelListener(ProgressCancelListener progressCancelListener) {
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

    public ProgressBarDialog setData(String message) {
        this.message = message;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_progress_bar_circle, container, false);
        if (backgroundType != null) {
            switch (backgroundType) {
                case BackgroundType.colorInt:
                    if (backgroundResource instanceof Integer)
                        view.setBackgroundColor((Integer) backgroundResource);
                    break;
                case BackgroundType.resId:
                    if (backgroundResource instanceof Integer)
                        view.setBackgroundResource((Integer) backgroundResource);
                    break;
                case BackgroundType.drawable:
                    if (backgroundResource instanceof Drawable)
                        view.setBackground((Drawable) backgroundResource);
                    break;
            }
        }
        if (message != null)
            ((TextView) view.findViewById(R.id.tv_msg)).setText(message);
        view.findViewById(R.id.tv_msg).setVisibility(TextUtils.isEmpty(message) ? View.GONE : View.VISIBLE);
        return view;
    }

    /**
     * 显示弹窗
     *
     * @param message
     */
    public void showProgress(String message) {
        showProgress(message, null, null);
    }

    /**
     * 显示弹窗
     * @param message 弹窗信息
     * @param backgroundType 背景类型
     * @param backgroundResource 背景资源
     */
    public void showProgress(String message, @BackgroundType Integer backgroundType, Object backgroundResource) {
        if (mActivity != null) {
            this.message = message;
            this.backgroundType = backgroundType;
            this.backgroundResource = backgroundResource;
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

    @Retention(RetentionPolicy.CLASS)
    @IntDef({BackgroundType.resId, BackgroundType.colorInt, BackgroundType.drawable})
    public @interface BackgroundType {//背景类型
        int colorInt = 0;
        int resId = 1;
        int drawable = 2;
    }
}

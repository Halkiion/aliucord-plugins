package com.github.halkiion.plugins;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.Nullable;

import com.aliucord.Utils;
import com.aliucord.utils.ReflectUtils;
import com.discord.databinding.ViewDialogConfirmationBinding;

import com.lytefast.flexinput.R;

import de.robv.android.xposed.XC_MethodHook;

public class Utility {
    public static void showToast(String message, int duration) {
        Utils.mainThread.post(() -> {
            b.a.d.m.e(Utils.getAppContext(), message, duration, null);
        });
    }

    public static void hideKeyboard(View view) {
        if (view == null)
            return;
        Context context = view.getContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showDiscardChangesDialog(Context context, XC_MethodHook.MethodHookParam param,
            @Nullable Runnable onDiscardConfirmed) {
        ViewDialogConfirmationBinding binding = ViewDialogConfirmationBinding.b(LayoutInflater.from(context));
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(binding.a)
                .create();

        binding.d.setText(R.h.discard_changes);
        binding.e.setText(R.h.discard_changes_description);

        binding.b.setOnClickListener(view -> dialog.dismiss());
        binding.c.setText(R.h.okay);
        binding.c.setOnClickListener(view -> {
            dialog.dismiss();
            if (onDiscardConfirmed != null)
                onDiscardConfirmed.run();
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
            param.setResult(true);
        });

        dialog.show();
    }

    public static void showDiscardChangesDialog(Context context, XC_MethodHook.MethodHookParam param) {
        showDiscardChangesDialog(context, param, null);
    }

    public static class Reflect {
        public static Object getField(Object obj, String fieldName) {
            try {
                return ReflectUtils.getField(obj, fieldName);
            } catch (Throwable ignored) {
                return null;
            }
        }

        public static boolean setField(Object obj, String fieldName, Object value) {
            try {
                ReflectUtils.setField(obj, fieldName, value);
                return true;
            } catch (Throwable ignored) {
                return false;
            }
        }

        public static Object invokeMethod(Object obj, String methodName, Object... args) {
            try {
                return ReflectUtils.invokeMethod(obj, methodName, args);
            } catch (Throwable ignored) {
                return null;
            }
        }
    }
}
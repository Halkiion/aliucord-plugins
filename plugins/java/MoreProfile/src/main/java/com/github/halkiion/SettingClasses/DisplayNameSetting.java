package com.github.halkiion.plugins.SettingClasses;

import com.github.halkiion.plugins.*;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliucord.Utils;
import com.discord.models.user.MeUser;
import com.discord.widgets.settings.account.WidgetSettingsAccount;
import com.discord.widgets.settings.account.WidgetSettingsAccountUsernameEdit;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;

public class DisplayNameSetting {
    public static boolean isDisplayNameMode = false;
    private static String lastDisplayName = null;

    private static final String SETTING_TAG = "display_name_setting";
    private static LinearLayout usernameRowRef = null;

    private static String currentEditValue = null;
    private static boolean isProgrammaticEdit = false;
    private static String originalUsername = null;

    private static String extractDisplayName(Object model) {
        if (model instanceof WidgetSettingsAccount.Model) {
            MeUser user = ((WidgetSettingsAccount.Model) model).getMeUser();
            if (user != null) {
                String name = UserValues.getDisplayName(user);
                if (name != null)
                    return name;
            }
        }
        return "";
    }

    private static void clearTextWatchers(EditText editText) {
        ArrayList<?> listeners = (ArrayList<?>) Utility.Reflect.getField(editText, "mListeners");
        if (listeners != null)
            listeners.clear();
    }

    private static void updateSaveFab(EditText editText, FloatingActionButton saveFab) {
        if (editText.getText() != null && !editText.getText().toString().equals(lastDisplayName))
            saveFab.show();
        else
            saveFab.hide();
    }

    private static void setEditText(EditText editText, String value) {
        if (editText != null && value != null) {
            editText.setText(value);
            editText.setSelection(value.length());
        }
    }

    public static void onAccountConfigureUI(XC_MethodHook.MethodHookParam param, Context context) {
        isDisplayNameMode = false;

        var binding = WidgetSettingsAccount.access$getBinding$p((WidgetSettingsAccount) param.thisObject);
        LinearLayout mainColumn = (LinearLayout) binding.x.getChildAt(0);

        usernameRowRef = binding.p;
        if (usernameRowRef == null)
            return;

        int insertIndex = 2;
        LinearLayout settingRow = (mainColumn != null) ? (LinearLayout) mainColumn.findViewWithTag(SETTING_TAG) : null;

        String displayName = lastDisplayName;
        if (displayName == null) {
            Object model = param.args[0];
            displayName = extractDisplayName(model);
        }
        if (displayName == null)
            displayName = "";
        lastDisplayName = displayName;

        if (settingRow == null) {
            mainColumn.setLayoutTransition(null);
            settingRow = ViewBuilders.DisplayName.createSettingRow(mainColumn.getContext(),
                    Strings.getString("display_name"),
                    displayName, SETTING_TAG, usernameRowRef);
            mainColumn.addView(settingRow, insertIndex);
            settingRow.setOnClickListener(v -> {
                isDisplayNameMode = true;
                WidgetSettingsAccountUsernameEdit.Companion.launch(v.getContext());
            });
        } else {
            TextView value = (TextView) settingRow.getChildAt(1);
            value.setText(displayName);
        }
        currentEditValue = null;
        originalUsername = null;
    }

    public static void onEditScreenConfigureUI(XC_MethodHook.MethodHookParam param) {
        if (!isDisplayNameMode)
            return;

        WidgetSettingsAccountUsernameEdit frag = (WidgetSettingsAccountUsernameEdit) param.thisObject;
        View view = frag.getView();
        if (view == null)
            return;
        setupDisplayNameEditScreen(frag, view);
    }

    private static void setupDisplayNameEditScreen(WidgetSettingsAccountUsernameEdit frag, View view) {
        frag.setActionBarTitle(Strings.getString("edit_display_name"));

        TextInputLayout usernameWrap = view.findViewById(Utils.getResId("edit_account_username_wrap", "id"));
        if (usernameWrap == null)
            return;

        usernameWrap.setHint(Strings.getString("display_name"));
        EditText usernameEdit = usernameWrap.getEditText();
        if (usernameEdit == null)
            return;

        usernameEdit.setFilters(new InputFilter[] { new InputFilter.LengthFilter(32) });

        clearTextWatchers(usernameEdit);

        if (originalUsername == null)
            originalUsername = usernameEdit.getText().toString();

        isProgrammaticEdit = true;
        if (currentEditValue != null) {
            setEditText(usernameEdit, currentEditValue);
        } else {
            setEditText(usernameEdit, lastDisplayName);
        }
        isProgrammaticEdit = false;

        View saveBtnView = view.findViewById(Utils.getResId("settings_account_save", "id"));
        if (!(saveBtnView instanceof FloatingActionButton))
            return;
        FloatingActionButton saveFab = (FloatingActionButton) saveBtnView;

        updateSaveFab(usernameEdit, saveFab);

        usernameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isProgrammaticEdit)
                    return;
                String newText = s.toString();
                if (!newText.equals(originalUsername))
                    currentEditValue = newText;
                updateSaveFab(usernameEdit, saveFab);
            }
        });

        saveFab.setOnClickListener(v -> {
            String newDisplayName = usernameEdit.getText().toString();
            String oldDisplayName = lastDisplayName;
            lastDisplayName = newDisplayName;
            currentEditValue = null;
            originalUsername = null;

            APIRequest.setDisplayName(newDisplayName, view.getContext(), success -> {
                if (!success)
                    lastDisplayName = oldDisplayName;
            });

            saveFab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onHidden(FloatingActionButton fab) {
                    Utility.hideKeyboard(view);
                    if (frag.isAdded())
                        frag.requireActivity().onBackPressed();
                }
            });
        });
    }
}
package com.github.halkiion.plugins;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

import com.aliucord.Utils;
import com.discord.models.user.MeUser;
import com.discord.widgets.settings.account.WidgetSettingsAccount;
import com.discord.widgets.settings.account.WidgetSettingsAccountUsernameEdit;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.lytefast.flexinput.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;

public class DisplayNameSetting {
    public static boolean isDisplayNameMode = false;
    public static String lastDisplayName = null;

    private static final String SETTING_TAG = "display_name_setting";
    private static LinearLayout usernameRowRef = null;

    private static String currentEditValue = null;
    private static boolean isProgrammaticEdit = false;
    private static String originalUsername = null;

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
            MeUser user = ((WidgetSettingsAccount.Model) model).getMeUser();
            if (user != null)
                displayName = UserValues.getDisplayName(user);
        }
        if (displayName == null)
            displayName = "";
        lastDisplayName = displayName;

        if (settingRow == null) {
            mainColumn.setLayoutTransition(null);
            settingRow = createSettingRow(mainColumn.getContext(), Strings.getString("display_name"), displayName,
                    SETTING_TAG);
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

    private static LinearLayout createSettingRow(Context context, String labelText, String valueText, String tag) {
        if (usernameRowRef == null)
            return null;
        TextView usernameLabel = (TextView) usernameRowRef.getChildAt(0);
        TextView usernameValue = (TextView) usernameRowRef.getChildAt(1);

        LinearLayout rowClone = new LinearLayout(context);
        rowClone.setOrientation(LinearLayout.HORIZONTAL);
        rowClone.setTag(tag);
        rowClone.setBackground(usernameRowRef.getBackground());
        ViewGroup.LayoutParams origParams = usernameRowRef.getLayoutParams();
        if (origParams != null)
            rowClone.setLayoutParams(origParams);

        TextView label = new TextView(context, null, 0, R.i.UiKit_Settings_Item_Icon);
        label.setLayoutParams(usernameLabel.getLayoutParams());
        label.setText(labelText);
        copyTextViewStyle(label, usernameLabel);

        TextView value = new TextView(context, null, 0, R.i.UiKit_Settings_Item_Icon);
        value.setLayoutParams(usernameValue.getLayoutParams());
        value.setText(valueText);
        value.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        copyTextViewStyle(value, usernameValue);

        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(Utils.getResId("ic_navigate_next", "attr"), typedValue, true)) {
            Drawable chevron = ContextCompat.getDrawable(context, typedValue.resourceId);
            if (chevron != null)
                chevron.mutate();
            value.setCompoundDrawablesWithIntrinsicBounds(null, null, chevron, null);
            value.setCompoundDrawablePadding(usernameValue.getCompoundDrawablePadding());
        }

        rowClone.addView(label);
        rowClone.addView(value);

        return rowClone;
    }

    private static void clearTextWatchers(EditText editText) {
        ArrayList<?> listeners = (ArrayList<?>) Utility.Reflect.getField(editText, "mListeners");
        if (listeners != null)
            listeners.clear();
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

        if (originalUsername == null) {
            originalUsername = usernameEdit.getText().toString();
        }

        isProgrammaticEdit = true;
        if (currentEditValue != null) {
            usernameEdit.setText(currentEditValue);
            usernameEdit.setSelection(currentEditValue.length());
        } else {
            usernameEdit.setText(lastDisplayName);
            usernameEdit.setSelection(lastDisplayName.length());
        }
        isProgrammaticEdit = false;

        View saveBtnView = view.findViewById(Utils.getResId("settings_account_save", "id"));
        if (!(saveBtnView instanceof FloatingActionButton))
            return;

        FloatingActionButton saveFab = (FloatingActionButton) saveBtnView;
        if (usernameEdit.getText() != null && !usernameEdit.getText().toString().equals(lastDisplayName))
            saveFab.show();
        else
            saveFab.hide();

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
                if (!newText.equals(originalUsername)) {
                    currentEditValue = newText;
                }
                if (!newText.equals(lastDisplayName))
                    saveFab.show();
                else
                    saveFab.hide();
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

    private static void copyTextViewStyle(TextView target, TextView source) {
        target.setTextColor(source.getTextColors());
        target.setTextSize(source.getTextSize()
                / source.getContext().getResources().getDisplayMetrics().scaledDensity);
        target.setTypeface(source.getTypeface());
        target.setGravity(source.getGravity());
        target.setPadding(
                source.getPaddingLeft(),
                source.getPaddingTop(),
                source.getPaddingRight(),
                source.getPaddingBottom());
        target.setBackground(source.getBackground());
    }
}
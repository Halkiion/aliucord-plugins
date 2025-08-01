package com.github.halkiion.plugins.SettingClasses;

import com.github.halkiion.plugins.*;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

import com.aliucord.Utils;
import com.discord.databinding.WidgetSettingsUserProfileBinding;
import com.discord.widgets.settings.profile.SettingsUserProfileViewModel;
import com.discord.widgets.settings.profile.TouchInterceptingCoordinatorLayout;
import com.discord.widgets.settings.profile.WidgetEditUserOrGuildMemberProfile;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import de.robv.android.xposed.XC_MethodHook;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import kotlin.jvm.functions.Function1;

public class PronounsSetting {
    private static void resetState() {
        originalPronouns = null;
        currentPronounsEdit = null;
        discordShowSaveFab = false;
        pronounsEditTextFinal = null;
        pronounsPreviewTextFinal = null;
        updateSaveFab = null;
        isProgrammaticPronounsEdit = false;
    }

    public static final int PRONOUNS_HEADER_ID = View.generateViewId();
    public static final int PRONOUNS_CARD_ID = View.generateViewId();

    private static TextInputEditText pronounsEditTextFinal = null;
    private static TextView pronounsPreviewTextFinal = null;

    private static String originalPronouns = null;
    private static String currentPronounsEdit = null;

    private static volatile boolean discordShowSaveFab = false;
    private static boolean isProgrammaticPronounsEdit = false;

    private static Runnable updateSaveFab = null;

    public static String getPronounsPreviewText() {
        return pronounsPreviewTextFinal != null ? pronounsPreviewTextFinal.getText().toString() : "";
    }

    private static void setPronounsPreviewText(Object userProfile) {
        String pronouns = UserValues.getPronouns(userProfile);
        if (pronounsPreviewTextFinal != null)
            pronounsPreviewTextFinal.setText(pronouns != null ? pronouns : "");
    }

    private static void syncPreviewToEdit() {
        if (pronounsPreviewTextFinal != null && pronounsEditTextFinal != null)
            pronounsPreviewTextFinal.setText(pronounsEditTextFinal.getText().toString());
    }

    private static boolean pronounsDirty() {
        return pronounsEditTextFinal != null && originalPronouns != null
                && !pronounsEditTextFinal.getText().toString().equals(originalPronouns);
    }

    private static void showPreviewCard(View preview, View edit) {
        if (preview != null)
            preview.setVisibility(View.VISIBLE);
        if (edit != null)
            edit.setVisibility(View.GONE);
    }

    private static void showEditCard(View preview, View edit) {
        if (preview != null)
            preview.setVisibility(View.GONE);
        if (edit != null)
            edit.setVisibility(View.VISIBLE);
    }

    private static void setEditTextSelectionToEnd(TextInputEditText editText) {
        if (editText != null && editText.getText() != null)
            editText.setSelection(editText.getText().length());
    }

    private static void setEditText(TextInputEditText editText, String text) {
        if (editText != null) {
            editText.setText(text);
            setEditTextSelectionToEnd(editText);
        }
    }

    public static void onProfileConfigureUI(XC_MethodHook.MethodHookParam param, Context context) {
        WidgetEditUserOrGuildMemberProfile instance = (WidgetEditUserOrGuildMemberProfile) param.thisObject;
        WidgetSettingsUserProfileBinding binding = (WidgetSettingsUserProfileBinding) Utility.Reflect
                .invokeMethod(instance, "getBinding");
        if (binding == null)
            return;

        View root = binding.getRoot();
        if (!(root instanceof ViewGroup))
            return;

        int bioPreviewCardId = Utils.getResId("bio_preview_card", "id");
        View bioPreviewCard = root.findViewById(bioPreviewCardId);
        LinearLayout ll = (bioPreviewCard != null && bioPreviewCard.getParent() instanceof LinearLayout)
                ? (LinearLayout) bioPreviewCard.getParent()
                : null;
        if (ll == null)
            return;
        final LinearLayout llFinal = ll;

        int bioHeaderId = Utils.getResId("bio_header", "id");
        TextView origHeader = null;
        for (int i = 0; i < ll.getChildCount(); i++) {
            View child = ll.getChildAt(i);
            if (child != null && child.getId() == bioHeaderId && child instanceof TextView) {
                origHeader = (TextView) child;
                break;
            }
        }

        TextInputLayout bioEditorWrap = binding.d;
        TextInputEditText bioEditorInput = binding.c;

        BiConsumer<TextView, TextView> copyTextViewStyle = ViewBuilders.Pronouns::copyTextViewStyle;
        BiConsumer<TextInputEditText, TextInputEditText> copyEditTextStyle = ViewBuilders.Pronouns::copyEditTextStyle;
        BiConsumer<TextInputLayout, TextInputLayout> copyTextInputLayoutStyle = ViewBuilders.Pronouns::copyTextInputLayoutStyle;

        Object userProfile = null;
        Object viewStateObj = param.args[0];
        if (viewStateObj != null &&
                viewStateObj.getClass().getName()
                        .equals("com.discord.widgets.settings.profile.SettingsUserProfileViewModel$ViewState$Loaded")) {
            userProfile = Utility.Reflect.invokeMethod(viewStateObj, "getUserProfile");
        }

        if (ll.findViewById(PRONOUNS_HEADER_ID) == null && origHeader != null) {
            ViewBuilders.Pronouns.addPronounsHeader(ll, origHeader, copyTextViewStyle);
        }

        if (ll.findViewById(PRONOUNS_CARD_ID) == null && bioPreviewCard instanceof CardView) {
            CardView origCard = (CardView) bioPreviewCard;

            ViewBuilders.Pronouns.PreviewCardResult previewResult = ViewBuilders.Pronouns.addPronounsPreviewCard(ll,
                    origCard, copyTextViewStyle, userProfile);
            final CardView pronounsPreviewCardFinal = previewResult.cardView;
            pronounsPreviewTextFinal = previewResult.textView;
            LinearLayout.LayoutParams pronounsLayoutParams = previewResult.editCardLayoutParams;

            final CardView pronounsEditCard = ViewBuilders.Pronouns.createPronounsEditCard(ll.getContext(),
                    pronounsLayoutParams, origCard.getRadius());
            final TextInputLayout pronounsEditorWrapFinal = ViewBuilders.Pronouns
                    .createPronounsEditorWrap(ll.getContext(), bioEditorWrap);
            pronounsEditTextFinal = ViewBuilders.Pronouns.createPronounsEditText(ll.getContext(), bioEditorInput);

            pronounsEditorWrapFinal.addView(pronounsEditTextFinal);
            pronounsEditCard.addView(pronounsEditorWrapFinal);
            ll.addView(pronounsEditCard, ll.getChildCount());

            pronounsPreviewCardFinal.setClickable(true);
            pronounsPreviewCardFinal.setFocusable(true);
            pronounsPreviewCardFinal.setOnClickListener(v -> {
                showEditCard(pronounsPreviewCardFinal, pronounsEditCard);
                setEditText(pronounsEditTextFinal, getPronounsPreviewText());
                pronounsEditTextFinal.requestFocus();
                InputMethodManager imm = (InputMethodManager) llFinal.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.showSoftInput(pronounsEditTextFinal, InputMethodManager.SHOW_IMPLICIT);
            });

            pronounsEditTextFinal.setOnFocusChangeListener((v, hasFocus) -> {
                if (updateSaveFab != null)
                    updateSaveFab.run();
                if (!hasFocus) {
                    showPreviewCard(pronounsPreviewCardFinal, pronounsEditCard);
                    syncPreviewToEdit();
                }
            });

            ll.addView(pronounsPreviewCardFinal, ll.getChildCount());

            pronounsEditTextFinal.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER)) {
                    if (pronounsEditTextFinal != null) {
                        pronounsEditTextFinal.clearFocus();
                        Utility.hideKeyboard(pronounsEditTextFinal);
                    }
                    return true;
                }
                return false;
            });

            int saveFabId = Utils.getResId("save_fab", "id");
            View saveFabView = root.findViewById(saveFabId);
            if (saveFabView instanceof FloatingActionButton) {
                FloatingActionButton saveFab = (FloatingActionButton) saveFabView;

                if (originalPronouns == null) {
                    String previewPronounsVal = UserValues.getPronouns(userProfile);
                    if (pronounsPreviewCardFinal.getChildCount() > 0) {
                        View pronounsTextView = pronounsPreviewCardFinal.getChildAt(0);
                        if (pronounsTextView instanceof TextView)
                            previewPronounsVal = ((TextView) pronounsTextView).getText().toString();
                    }
                    originalPronouns = previewPronounsVal;
                }

                isProgrammaticPronounsEdit = true;
                String toShow = (currentPronounsEdit != null && !currentPronounsEdit.equals(originalPronouns))
                        ? currentPronounsEdit
                        : originalPronouns;
                if (currentPronounsEdit == null || currentPronounsEdit.equals(originalPronouns))
                    currentPronounsEdit = null;
                setEditText(pronounsEditTextFinal, toShow);
                isProgrammaticPronounsEdit = false;

                pronounsPreviewTextFinal.setText(toShow);

                pronounsEditTextFinal.addTextChangedListener(new android.text.TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(android.text.Editable s) {
                        if (isProgrammaticPronounsEdit)
                            return;
                        String newText = s.toString();
                        if (!newText.equals(originalPronouns)) {
                            currentPronounsEdit = newText;
                        } else {
                            currentPronounsEdit = null;
                        }
                        pronounsPreviewTextFinal.setText(newText);
                    }
                });

                updateSaveFab = () -> {
                    if (pronounsEditTextFinal.isFocused()) {
                        saveFab.setVisibility(View.GONE);
                        return;
                    }
                    saveFab.setVisibility((pronounsDirty() || discordShowSaveFab) ? View.VISIBLE : View.GONE);
                };

                saveFab.setOnClickListener(v -> {
                    SettingsUserProfileViewModel viewModel = (SettingsUserProfileViewModel) Utility.Reflect
                            .invokeMethod(instance, "getViewModel");
                    if (viewModel != null)
                        viewModel.saveChanges(context);

                    originalPronouns = pronounsEditTextFinal.getText().toString();
                    currentPronounsEdit = null;

                    if (pronounsDirty())
                        APIRequest.setPronouns(originalPronouns, context);

                    updateSaveFab.run();
                });
            }

            if (root instanceof TouchInterceptingCoordinatorLayout) {
                TouchInterceptingCoordinatorLayout ticLayout = (TouchInterceptingCoordinatorLayout) root;
                Function1<? super MotionEvent, Boolean> originalHandler = ticLayout.getOnInterceptTouchEvent();
                ticLayout.setOnInterceptTouchEvent(event -> {
                    boolean discordHandled = originalHandler != null && originalHandler.invoke(event);
                    int actionMasked = event.getActionMasked();
                    if (actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_CANCEL) {
                        float x = event.getRawX();
                        float y = event.getRawY();
                        boolean insidePronouns = isInsideView(pronounsEditTextFinal, x, y);
                        boolean insideBio = isInsideView(bioEditorInput, x, y);
                        if (!insidePronouns && !insideBio && pronounsEditTextFinal != null) {
                            Utility.hideKeyboard(pronounsEditTextFinal);
                            pronounsEditTextFinal.clearFocus();
                        }
                    }
                    return discordHandled;
                });
            }
            if (updateSaveFab != null)
                updateSaveFab.run();
        }
    }

    public static void onConfigureFab(XC_MethodHook.MethodHookParam param, Context context) {
        Object viewState = param.args[0];
        boolean showSaveFab = false;
        Object result = Utility.Reflect.invokeMethod(viewState, "getShowSaveFab");
        if (result instanceof Boolean)
            showSaveFab = (Boolean) result;

        discordShowSaveFab = showSaveFab;

        if (updateSaveFab != null)
            updateSaveFab.run();
    }

    public static void onHandleBackPressed(XC_MethodHook.MethodHookParam param, Context context) {
        if (pronounsDirty() && !discordShowSaveFab) {
            Context pluginContext = pronounsEditTextFinal != null ? pronounsEditTextFinal.getContext() : context;
            Utility.showDiscardChangesDialog(pluginContext, param, PronounsSetting::resetState);
            param.setResult(true);
            return;
        }

        Object discardConfirmedObj = Utility.Reflect.getField(param.thisObject, "discardConfirmed");
        if (discardConfirmedObj instanceof AtomicBoolean && ((AtomicBoolean) discardConfirmedObj).get()) {
            resetState();
            return;
        }

        resetState();
    }

    private static boolean isInsideView(View view, float x, float y) {
        if (view == null || view.getVisibility() != View.VISIBLE)
            return false;
        int[] loc = new int[2];
        view.getLocationOnScreen(loc);
        return (x >= loc[0] && x <= loc[0] + view.getWidth() &&
                y >= loc[1] && y <= loc[1] + view.getHeight());
    }
}
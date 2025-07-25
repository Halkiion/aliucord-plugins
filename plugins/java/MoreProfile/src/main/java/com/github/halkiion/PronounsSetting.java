package com.github.halkiion.plugins;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
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

import com.lytefast.flexinput.R;

import de.robv.android.xposed.XC_MethodHook;

import java.util.function.BiConsumer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.jvm.functions.Function1;


public class PronounsSetting {
    private static String originalPronouns = null;
    private static TextInputEditText pronounsEditTextFinal = null;
    private static TextView pronounsPreviewTextFinal = null;
    private static volatile boolean discordShowSaveFab = false;
    private static final AtomicBoolean discardConfirmed = new AtomicBoolean(false);

    public static String lastPronouns = null;
    private static String currentPronounsEditValue = null;
    private static boolean isProgrammaticEdit = false;

    private static boolean storedPronouns = false;

    public static void setPronounsPreviewText(XC_MethodHook.MethodHookParam param) {
        if (storedPronouns && !pronounsPreviewTextFinal.isAttachedToWindow())
            return;

        storedPronouns = true;
        Object RNUserObj = param.args[0];
        String pronouns = UserValues.getPronouns(RNUserObj);
        if (pronounsPreviewTextFinal != null)
            pronounsPreviewTextFinal.setText(pronouns);
    }

    public static String getPronounsPreviewText() {
        return pronounsPreviewTextFinal != null ? pronounsPreviewTextFinal.getText().toString() : "";
    }

    public static void onProfileConfigureUI(XC_MethodHook.MethodHookParam param, Context context) {
        WidgetEditUserOrGuildMemberProfile instance = (WidgetEditUserOrGuildMemberProfile) param.thisObject;

        WidgetSettingsUserProfileBinding binding = null;
        try {
            Method getBinding = WidgetEditUserOrGuildMemberProfile.class.getDeclaredMethod("getBinding");
            getBinding.setAccessible(true);
            binding = (WidgetSettingsUserProfileBinding) getBinding.invoke(instance);
        } catch (Exception ignored) {
        }
        if (binding == null)
            return;

        View root = binding.getRoot();
        if (!(root instanceof ViewGroup))
            return;

        int bioPreviewCardId = Utils.getResId("bio_preview_card", "id");
        View bioPreviewCard = root.findViewById(bioPreviewCardId);
        LinearLayout ll = null;
        if (bioPreviewCard != null && bioPreviewCard.getParent() instanceof LinearLayout) {
            ll = (LinearLayout) bioPreviewCard.getParent();
        }
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

        BiConsumer<TextView, TextView> copyTextViewStyle = PronounsSetting::copyTextViewStyle;
        BiConsumer<TextInputEditText, TextInputEditText> copyEditTextStyle = PronounsSetting::copyEditTextStyle;
        BiConsumer<TextInputLayout, TextInputLayout> copyTextInputLayoutStyle = PronounsSetting::copyTextInputLayoutStyle;

        if (origHeader != null) {
            addPronounsHeader(ll, origHeader, copyTextViewStyle);
        }

        String previewPronouns = UserValues.getPronouns();

        if (bioPreviewCard instanceof CardView) {
            CardView origCard = (CardView) bioPreviewCard;
            final CardView pronounsPreviewCardFinal = new CardView(ll.getContext());
            pronounsPreviewCardFinal.setLayoutParams(origCard.getLayoutParams());
            pronounsPreviewCardFinal.setCardElevation(origCard.getCardElevation());
            pronounsPreviewCardFinal.setRadius(origCard.getRadius());
            pronounsPreviewCardFinal.setUseCompatPadding(origCard.getUseCompatPadding());
            pronounsPreviewCardFinal.setCardBackgroundColor(origCard.getCardBackgroundColor());

            TextView localPronounsPreviewTextFinal;
            if (origCard.getChildCount() > 0) {
                View origInner = origCard.getChildAt(0);
                if (origInner instanceof TextView) {
                    TextView origInnerTV = (TextView) origInner;
                    localPronounsPreviewTextFinal = new TextView(ll.getContext());
                    localPronounsPreviewTextFinal.setLayoutParams(origInnerTV.getLayoutParams());
                    localPronounsPreviewTextFinal.setText(UserValues.getPronouns());
                    copyTextViewStyle.accept(localPronounsPreviewTextFinal, origInnerTV);
                    pronounsPreviewCardFinal.addView(localPronounsPreviewTextFinal);
                } else {
                    localPronounsPreviewTextFinal = new TextView(ll.getContext());
                }
            } else {
                localPronounsPreviewTextFinal = new TextView(ll.getContext());
            }

            pronounsPreviewTextFinal = localPronounsPreviewTextFinal;

            ViewGroup.MarginLayoutParams previewCardParams = null;
            try {
                previewCardParams = (ViewGroup.MarginLayoutParams) pronounsPreviewCardFinal
                        .getLayoutParams();
            } catch (Throwable ignored) {
            }

            LinearLayout.LayoutParams pronounsLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            if (previewCardParams != null) {
                pronounsLayoutParams.setMargins(
                        previewCardParams.leftMargin,
                        previewCardParams.topMargin,
                        previewCardParams.rightMargin,
                        previewCardParams.bottomMargin);
            }

            final CardView pronounsEditCard = new CardView(ll.getContext());
            pronounsEditCard.setLayoutParams(pronounsLayoutParams);
            pronounsEditCard.setRadius(origCard.getRadius());
            pronounsEditCard.setCardBackgroundColor(null);
            pronounsEditCard.setVisibility(View.GONE);

            final TextInputLayout pronounsEditorWrapFinal = new TextInputLayout(ll.getContext(), null,
                    R.i.UiKit_TextInputLayout);
            pronounsEditorWrapFinal.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            copyTextInputLayoutStyle.accept(pronounsEditorWrapFinal, bioEditorWrap);
            pronounsEditorWrapFinal.setHint(Strings.getString("pronouns_hint"));
            pronounsEditorWrapFinal.setVisibility(View.VISIBLE);

            try {
                Field boxCollapsedPaddingTopPxField = TextInputLayout.class
                        .getDeclaredField("boxCollapsedPaddingTopPx");
                boxCollapsedPaddingTopPxField.setAccessible(true);
                int bioPaddingTopPx = boxCollapsedPaddingTopPxField.getInt(bioEditorWrap);
                boxCollapsedPaddingTopPxField.setInt(pronounsEditorWrapFinal, bioPaddingTopPx);
            } catch (Throwable ignored) {
            }

            pronounsEditTextFinal = new TextInputEditText(ll.getContext());
            LinearLayout.LayoutParams pronounsEditParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            ViewGroup.LayoutParams bioEditParams = bioEditorInput.getLayoutParams();
            if (bioEditParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams bioMargin = (ViewGroup.MarginLayoutParams) bioEditParams;
                pronounsEditParams.setMargins(bioMargin.leftMargin, bioMargin.topMargin,
                        bioMargin.rightMargin, bioMargin.bottomMargin);
            }
            pronounsEditTextFinal.setLayoutParams(pronounsEditParams);

            copyEditTextStyle.accept(pronounsEditTextFinal, bioEditorInput);

            pronounsEditTextFinal.setFilters(new InputFilter[] { new InputFilter.LengthFilter(40) });
            pronounsEditorWrapFinal.addView(pronounsEditTextFinal);
            pronounsEditCard.addView(pronounsEditorWrapFinal);
            ll.addView(pronounsEditCard, ll.getChildCount());

            pronounsPreviewCardFinal.setClickable(true);
            pronounsPreviewCardFinal.setFocusable(true);
            pronounsPreviewCardFinal.setOnClickListener(v -> {
                pronounsPreviewCardFinal.setVisibility(View.GONE);
                pronounsEditCard.setVisibility(View.VISIBLE);
                pronounsEditTextFinal.setText(pronounsPreviewTextFinal.getText());
                pronounsEditTextFinal.requestFocus();
                InputMethodManager imm = (InputMethodManager) llFinal.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(pronounsEditTextFinal, InputMethodManager.SHOW_IMPLICIT);
                }
            });

            pronounsEditTextFinal.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    pronounsEditCard.setVisibility(View.GONE);
                    pronounsPreviewCardFinal.setVisibility(View.VISIBLE);
                    pronounsPreviewTextFinal.setText(
                            pronounsEditTextFinal.getText().length() > 0 ? pronounsEditTextFinal.getText()
                                    : UserValues.getPronouns());
                }
            });

            ll.addView(pronounsPreviewCardFinal, ll.getChildCount());

            pronounsEditTextFinal.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER)) {
                    pronounsEditTextFinal.clearFocus();
                    return true;
                }
                return false;
            });

            int saveFabId = Utils.getResId("save_fab", "id");
            View saveFabView = root.findViewById(saveFabId);
            if (saveFabView instanceof FloatingActionButton) {
                FloatingActionButton saveFab = (FloatingActionButton) saveFabView;

                if (originalPronouns == null) {
                    String previewPronounsVal = UserValues.getPronouns();
                    if (pronounsPreviewCardFinal.getChildCount() > 0) {
                        View pronounsTextView = pronounsPreviewCardFinal.getChildAt(0);
                        if (pronounsTextView instanceof TextView) {
                            previewPronounsVal = ((TextView) pronounsTextView).getText().toString();
                        }
                    }
                    originalPronouns = previewPronounsVal;
                }
                pronounsEditTextFinal.setText(originalPronouns);

                Runnable updateSaveFab = () -> {
                    boolean pronounsDirty = !pronounsEditTextFinal.getText().toString()
                            .equals(originalPronouns);

                    if (pronounsDirty || discordShowSaveFab) {
                        saveFab.show();
                    } else {
                        saveFab.hide();
                    }
                };

                pronounsEditTextFinal.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        updateSaveFab.run();
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });

                updateSaveFab.run();

                saveFab.setOnClickListener(v -> {
                    SettingsUserProfileViewModel viewModel = null;
                    try {
                        Method getViewModel = WidgetEditUserOrGuildMemberProfile.class
                                .getDeclaredMethod("getViewModel");
                        getViewModel.setAccessible(true);
                        viewModel = (SettingsUserProfileViewModel) getViewModel.invoke(instance);
                    } catch (Exception ignored) {
                    }

                    if (viewModel != null) {
                        viewModel.saveChanges(context);
                    }

                    APIRequest.setPronouns(pronounsEditTextFinal.getText().toString(), context);

                    originalPronouns = pronounsEditTextFinal.getText().toString();
                    updateSaveFab.run();
                });
            }

            if (root instanceof TouchInterceptingCoordinatorLayout) {
                TouchInterceptingCoordinatorLayout ticLayout = (TouchInterceptingCoordinatorLayout) root;

                Function1<? super MotionEvent, Boolean> originalHandler = ticLayout
                        .getOnInterceptTouchEvent();

                ticLayout.setOnInterceptTouchEvent(event -> {
                    boolean discordHandled = false;
                    if (originalHandler != null) {
                        discordHandled = originalHandler.invoke(event);
                    }

                    int actionMasked = event.getActionMasked();
                    if (actionMasked == MotionEvent.ACTION_UP
                            || actionMasked == MotionEvent.ACTION_CANCEL) {
                        float x = event.getRawX();
                        float y = event.getRawY();
                        boolean insidePronouns = isInsideView(pronounsEditTextFinal, x, y);
                        boolean insideBio = isInsideView(bioEditorInput, x, y);

                        if (!insidePronouns && !insideBio) {
                            InputMethodManager imm = (InputMethodManager) pronounsEditTextFinal.getContext()
                                    .getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(pronounsEditTextFinal.getWindowToken(), 0);
                                imm.hideSoftInputFromWindow(bioEditorInput.getWindowToken(), 0);
                            }
                            pronounsEditTextFinal.clearFocus();
                            bioEditorInput.clearFocus();
                        }
                    }

                    return discordHandled;
                });
            }
        }
    }

    public static void onConfigureFab(XC_MethodHook.MethodHookParam param, Context context) {
        Object viewState = param.args[0];
        boolean showSaveFab = false;
        try {
            Method getShowSaveFab = viewState.getClass().getDeclaredMethod("getShowSaveFab");
            getShowSaveFab.setAccessible(true);
            Object result = getShowSaveFab.invoke(viewState);
            if (result instanceof Boolean)
                showSaveFab = (Boolean) result;
        } catch (Exception e) {
        }
        discordShowSaveFab = showSaveFab;
    }

    public static void onHandleBackPressed(XC_MethodHook.MethodHookParam param, Context context) {
        boolean pronounsDirty = pronounsEditTextFinal != null && originalPronouns != null
                && !pronounsEditTextFinal.getText().toString().equals(originalPronouns);

        if (pronounsDirty && !discordShowSaveFab) {
            Context pluginContext = pronounsEditTextFinal != null ? pronounsEditTextFinal.getContext() : context;
            Utility.showDiscardChangesDialog(pluginContext, param);
            param.setResult(true);
        }
    }

    private static boolean isInsideView(View view, float x, float y) {
        if (view == null || view.getVisibility() != View.VISIBLE)
            return false;
        int[] loc = new int[2];
        view.getLocationOnScreen(loc);
        return (x >= loc[0] && x <= loc[0] + view.getWidth() &&
                y >= loc[1] && y <= loc[1] + view.getHeight());
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

    private static void copyEditTextStyle(TextInputEditText target, TextInputEditText source) {
        target.setPadding(
                source.getPaddingLeft(),
                source.getPaddingTop(),
                source.getPaddingRight(),
                source.getPaddingBottom());
        target.setTextSize(source.getTextSize()
                / source.getContext().getResources().getDisplayMetrics().scaledDensity);
        target.setTypeface(source.getTypeface());
        target.setGravity(source.getGravity());
        target.setTextColor(source.getTextColors());
        target.setBackground(source.getBackground());
        target.setInputType(source.getInputType());
        target.setMaxLines(source.getMaxLines());
        target.setImeOptions(source.getImeOptions());
        InputFilter[] filters = source.getFilters();
        if (filters != null)
            target.setFilters(filters);
    }

    private static void copyTextInputLayoutStyle(TextInputLayout target, TextInputLayout source) {
        target.setBoxBackgroundMode(source.getBoxBackgroundMode());
        target.setBoxBackgroundColor(source.getBoxBackgroundColor());
        target.setBoxStrokeColor(source.getBoxStrokeColor());
        target.setBoxStrokeWidth(source.getBoxStrokeWidth());
        target.setBoxStrokeWidthFocused(source.getBoxStrokeWidthFocused());
        target.setHintTextColor(source.getHintTextColor());
    }

    private static void addPronounsHeader(LinearLayout ll, TextView origHeader,
            BiConsumer<TextView, TextView> styleCopier) {
        TextView pronounsHeader = new TextView(ll.getContext(), null, 0,
                R.i.UiKit_Settings_Item_Header);
        pronounsHeader.setLayoutParams(origHeader.getLayoutParams());
        pronounsHeader.setText(Strings.getString("pronouns_header"));
        styleCopier.accept(pronounsHeader, origHeader);
        ll.addView(pronounsHeader, ll.getChildCount());
    }
}
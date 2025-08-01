package com.github.halkiion.plugins;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.aliucord.Utils;
import com.lytefast.flexinput.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.function.BiConsumer;

public class ViewBuilders {
    public static class DisplayName {
        public static LinearLayout createSettingRow(
                Context context,
                String labelText,
                String valueText,
                String tag,
                LinearLayout usernameRowRef) {
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

        public static void copyTextViewStyle(TextView target, TextView source) {
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

    public static class Pronouns {
        public static class PreviewCardResult {
            public final CardView cardView;
            public final TextView textView;
            public final LinearLayout.LayoutParams editCardLayoutParams;

            public PreviewCardResult(CardView cardView, TextView textView, LinearLayout.LayoutParams params) {
                this.cardView = cardView;
                this.textView = textView;
                this.editCardLayoutParams = params;
            }
        }

        public static PreviewCardResult addPronounsPreviewCard(
                LinearLayout ll,
                CardView origCard,
                BiConsumer<TextView, TextView> copyTextViewStyle,
                Object userProfile) {
            CardView pronounsPreviewCardFinal = new CardView(ll.getContext());
            pronounsPreviewCardFinal.setId(Setting.Pronouns.PRONOUNS_CARD_ID);
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
                    localPronounsPreviewTextFinal.setText(UserValues.getPronouns(userProfile));
                    copyTextViewStyle.accept(localPronounsPreviewTextFinal, origInnerTV);
                    pronounsPreviewCardFinal.addView(localPronounsPreviewTextFinal);
                } else {
                    localPronounsPreviewTextFinal = new TextView(ll.getContext());
                }
            } else {
                localPronounsPreviewTextFinal = new TextView(ll.getContext());
            }

            ViewGroup.MarginLayoutParams previewCardParams = null;
            try {
                previewCardParams = (ViewGroup.MarginLayoutParams) pronounsPreviewCardFinal.getLayoutParams();
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

            return new PreviewCardResult(pronounsPreviewCardFinal, localPronounsPreviewTextFinal,
                    pronounsLayoutParams);
        }

        public static CardView createPronounsEditCard(Context context, LinearLayout.LayoutParams layoutParams,
                float radius) {
            CardView card = new CardView(context);
            card.setLayoutParams(layoutParams);
            card.setCardElevation(0f);
            card.setRadius(radius);
            card.setCardBackgroundColor(null);
            card.setVisibility(View.GONE);
            return card;
        }

        public static TextInputLayout createPronounsEditorWrap(Context context, TextInputLayout bioEditorWrap) {
            TextInputLayout wrap = new TextInputLayout(context, null, R.i.UiKit_TextInputLayout);
            wrap.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            copyTextInputLayoutStyle(wrap, bioEditorWrap);
            wrap.setHint(Strings.getString("pronouns_hint"));
            wrap.setVisibility(View.VISIBLE);
            Integer bioPaddingTopPx = (Integer) Utility.Reflect.getField(bioEditorWrap, "boxCollapsedPaddingTopPx");
            if (bioPaddingTopPx != null)
                Utility.Reflect.setField(wrap, "boxCollapsedPaddingTopPx", bioPaddingTopPx);
            return wrap;
        }

        public static TextInputEditText createPronounsEditText(Context context, TextInputEditText bioEditorInput) {
            TextInputEditText editText = new TextInputEditText(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            ViewGroup.LayoutParams bioEditParams = bioEditorInput.getLayoutParams();
            if (bioEditParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams bioMargin = (ViewGroup.MarginLayoutParams) bioEditParams;
                params.setMargins(bioMargin.leftMargin, bioMargin.topMargin,
                        bioMargin.rightMargin, bioMargin.bottomMargin);
            }
            editText.setLayoutParams(params);

            copyEditTextStyle(editText, bioEditorInput);
            editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(40) });
            return editText;
        }

        public static void copyTextViewStyle(TextView target, TextView source) {
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

        public static void copyEditTextStyle(TextInputEditText target, TextInputEditText source) {
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

        public static void copyTextInputLayoutStyle(TextInputLayout target, TextInputLayout source) {
            target.setBoxBackgroundMode(source.getBoxBackgroundMode());
            target.setBoxBackgroundColor(source.getBoxBackgroundColor());
            target.setBoxStrokeColor(source.getBoxStrokeColor());
            target.setBoxStrokeWidth(source.getBoxStrokeWidth());
            target.setBoxStrokeWidthFocused(source.getBoxStrokeWidthFocused());
            target.setHintTextColor(source.getHintTextColor());
        }

        public static void addPronounsHeader(LinearLayout ll, TextView origHeader,
                BiConsumer<TextView, TextView> styleCopier) {
            TextView pronounsHeader = new TextView(ll.getContext(), null, 0,
                    R.i.UiKit_Settings_Item_Header);
            pronounsHeader.setId(Setting.Pronouns.PRONOUNS_HEADER_ID);
            pronounsHeader.setLayoutParams(origHeader.getLayoutParams());
            pronounsHeader.setText(Strings.getString("pronouns_header"));
            styleCopier.accept(pronounsHeader, origHeader);
            ll.addView(pronounsHeader, ll.getChildCount());
        }
    }
}
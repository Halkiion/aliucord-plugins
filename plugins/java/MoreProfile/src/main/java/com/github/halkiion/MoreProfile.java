package com.github.halkiion.plugins;

import android.content.Context;

import com.aliucord.api.rn.user.RNUserProfile;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.Hook;
import com.aliucord.Utils;

import com.discord.api.user.UserProfile;
import com.discord.models.user.MeUser;
import com.discord.stores.StoreUserProfile;
import com.discord.widgets.settings.account.WidgetSettingsAccount;
import com.discord.widgets.settings.account.WidgetSettingsAccountUsernameEdit;
import com.discord.widgets.settings.profile.WidgetEditUserOrGuildMemberProfile;

import de.robv.android.xposed.XC_MethodHook;

import java.lang.reflect.Method;


@AliucordPlugin
public class MoreProfile extends Plugin {
    @Override
    public void start(Context context) {
        APIRequest.versionFetcher.initialise();

        patcher.patch(
                WidgetSettingsAccount.class,
                "configureUI",
                new Class[] { WidgetSettingsAccount.Model.class },
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                        DisplayNameSetting.onAccountConfigureUI(param, context);
                    }
                });

        patcher.patch(
                WidgetSettingsAccountUsernameEdit.class,
                "configureUI",
                new Class[] { MeUser.class },
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                        DisplayNameSetting.onEditScreenConfigureUI(param);
                    }
                });

        patcher.patch(
                WidgetEditUserOrGuildMemberProfile.class,
                "onViewBound",
                new Class[] { android.view.View.class },
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                        PronounsSetting.onProfileConfigureUI(param, context);
                    }
                });

        patcher.patch(StoreUserProfile.class,
                "handleUserProfile",
                new Class[] { UserProfile.class },
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                        PronounsSetting.setPronounsPreviewText(param);
                    }
                });

        try {
            Class<?> loadedClass = Class
                    .forName("com.discord.widgets.settings.profile.SettingsUserProfileViewModel$ViewState$Loaded");
            patcher.patch(
                    WidgetEditUserOrGuildMemberProfile.class,
                    "configureFab",
                    new Class[] { loadedClass },
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                            PronounsSetting.onConfigureFab(param, context);
                        }
                    });
            patcher.patch(
                    WidgetEditUserOrGuildMemberProfile.class,
                    "handleBackPressed",
                    new Class[] { loadedClass },
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                            PronounsSetting.onHandleBackPressed(param, context);
                        }
                    });
        } catch (ClassNotFoundException e) {
        }
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
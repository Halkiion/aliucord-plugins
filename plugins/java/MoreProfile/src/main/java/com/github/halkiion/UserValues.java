package com.github.halkiion.plugins;

import com.aliucord.api.rn.user.RNUserProfile;
import com.aliucord.wrappers.users.UserWrapperKt;

import com.discord.models.user.MeUser;


public class UserValues {
    private static Object RNUserObj = null;
    private static Object MeUserObj = null;

    public static String getPronouns(Object userProfileObj) {
        RNUserObj = userProfileObj;
        if (!(userProfileObj instanceof RNUserProfile)) return null;
        var userProfile = ((RNUserProfile) userProfileObj).getUserProfile();
        return userProfile != null && userProfile.getPronouns() != null ? userProfile.getPronouns() : null;
    }

    public static String getDisplayName(Object userObj) {
        MeUserObj = userObj;
        if (!(userObj instanceof MeUser)) return null;
        String displayName = UserWrapperKt.getGlobalName((MeUser) userObj);
        return displayName != null && !displayName.isEmpty() ? displayName : null;
    }

    public static String getPronouns() {
        return RNUserObj != null ? getPronouns(RNUserObj) : null;
    }

    public static String getDisplayName() {
        return MeUserObj != null ? getDisplayName(MeUserObj) : null;
    }
}
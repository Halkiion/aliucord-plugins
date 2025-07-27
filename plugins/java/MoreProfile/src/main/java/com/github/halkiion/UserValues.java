package com.github.halkiion.plugins;

import com.aliucord.api.rn.user.RNUserProfile;

import java.lang.reflect.Field;


public class UserValues {
    private static Object lastRNUserObj = null;
    private static Object lastMeUserObj = null;

    public static String getPronouns(Object userProfileObj) {
        lastRNUserObj = userProfileObj;
        if (userProfileObj instanceof RNUserProfile) {
            RNUserProfile rn = (RNUserProfile) userProfileObj;
            if (rn.getUserProfile() != null && rn.getUserProfile().getPronouns() != null)
                return rn.getUserProfile().getPronouns();
        }
        return null;
    }

    public static String getDisplayName(Object userObj) {
        lastMeUserObj = userObj;
        try {
            Field f = userObj.getClass().getDeclaredField("globalName");
            f.setAccessible(true);
            Object val = f.get(userObj);
            if (val != null && !val.toString().isEmpty())
                return val.toString();
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
        return null;
    }

    public static String getPronouns() {
        return lastRNUserObj != null ? getPronouns(lastRNUserObj) : null;
    }

    public static String getDisplayName() {
        return lastMeUserObj != null ? getDisplayName(lastMeUserObj) : null;
    }
}
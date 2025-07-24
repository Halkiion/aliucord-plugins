package com.github.halkiion.plugins;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;
import android.widget.Toast;
import android.os.Build;

import com.aliucord.Http;
import com.aliucord.Utils;
import com.discord.utilities.rest.RestAPI;

import java.util.function.Consumer;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;

import org.json.JSONObject;


public class APIRequest {
    private static final String AUTH_TOKEN = RestAPI.AppHeadersProvider.INSTANCE.getAuthToken();
    private static final String OS_VERSION = Build.VERSION.RELEASE;

    private static String FIREFOX_NIGHTLY_VERSION = "143.0";
    private static int CLIENT_BUILD_NUMBER = 422112;

    private static String CLIENT_BUILD_NUMBER_STRING = String.valueOf(CLIENT_BUILD_NUMBER);
    private static String DEVICE_LOCALE = Locale.getDefault().toString().replace("_", "-");

    private static String getMobileUA() {
        return "Mozilla/5.0 (Android " + OS_VERSION
                + "; Mobile; rv:" + FIREFOX_NIGHTLY_VERSION + ") Gecko/" + FIREFOX_NIGHTLY_VERSION + " Firefox/"
                + FIREFOX_NIGHTLY_VERSION;
    }

    public static String getSuperProps() {
        try {
            JSONObject props = new JSONObject();
            props.put("os", "Android");
            props.put("browser", "Android Mobile");
            props.put("device", "Android");
            props.put("system_locale", DEVICE_LOCALE);
            props.put("has_client_mods", false);
            props.put("browser_user_agent", getMobileUA());
            props.put("browser_version", FIREFOX_NIGHTLY_VERSION);
            props.put("os_version", OS_VERSION);
            props.put("referrer", "https://top.gg/");
            props.put("referring_domain", "top.gg");
            props.put("referrer_current", "");
            props.put("referring_domain_current", "");
            props.put("release_channel", "stable");
            props.put("client_build_number", CLIENT_BUILD_NUMBER);
            props.put("client_event_source", JSONObject.NULL);
            props.put("client_launch_id", UUID.randomUUID().toString());
            props.put("launch_signature", UUID.randomUUID().toString());
            props.put("client_heartbeat_session_id", UUID.randomUUID().toString());
            props.put("client_app_state", "focused");
            return Base64.encodeToString(props.toString().getBytes(), Base64.NO_WRAP);
        } catch (Exception e) {
            return "";
        }
    }

    public static Map<String, String> buildDefaultHeaders() {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("accept", "*/*");
        headers.put("accept-language", Locale.getDefault().toLanguageTag() + ",en-US;q=0.9,zh-Hans-CN;q=0.8");
        headers.put("authorization", AUTH_TOKEN);
        headers.put("content-type", "application/json");
        headers.put("origin", "https://discord.com");
        headers.put("priority", "u=1, i");
        headers.put("referer", "https://discord.com/channels/@me");
        headers.put("user-agent", getMobileUA());
        headers.put("x-debug-options", "bugReporterEnabled");
        headers.put("x-discord-locale", DEVICE_LOCALE);
        headers.put("x-discord-timezone", TimeZone.getDefault().getID());
        headers.put("x-super-properties", getSuperProps());
        return headers;
    }

    public static void sendApiRequest(
            String url,
            String method,
            Map<String, String> headers,
            String rawBody,
            Context context,
            Consumer<String> onSuccess,
            Consumer<Exception> onError,
            CaptchaHandler onCaptcha) {
        new Thread(() -> {
            try {
                Http.Request req = new Http.Request(url, method);
                if (headers != null)
                    headers.forEach(req::setHeader);

                Http.Response res = rawBody == null ? req.execute() : req.executeWithBody(rawBody);
                String responseText = (res != null) ? res.text() : "null response";
                if (onSuccess != null)
                    onSuccess.accept(responseText);
            } catch (Exception e) {
                String responseText = e.getMessage();
                String body = null;
                int idx = responseText == null ? -1 : responseText.indexOf('\n');
                if (idx != -1 && responseText.length() > idx + 1) {
                    body = responseText.substring(idx + 1);
                }
                if (body != null && body.contains("captcha_sitekey") && onCaptcha != null) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        String sitekey = obj.optString("captcha_sitekey", null);
                        String rqdata = obj.optString("captcha_rqdata", null);
                        String rqtoken = obj.optString("captcha_rqtoken", null);
                        String sessionId = obj.optString("captcha_session_id", null);

                        handleCaptcha(context, sitekey, rqdata, rqtoken, sessionId, onCaptcha);
                        return;
                    } catch (Exception parseE) {
                        if (onError != null)
                            onError.accept(parseE);
                        return;
                    }
                }
                if (onError != null)
                    onError.accept(e);
            }
        }).start();
    }

    public interface CaptchaHandler {
        void onCaptchaSolved(String captchaToken, String rqdata, String rqtoken, String sessionId);
    }

    private static void handleCaptcha(
            Context context,
            String sitekey,
            String rqdata,
            String rqtoken,
            String sessionId,
            CaptchaHandler handler) {
        Activity activity = getActivityFromContext(context);
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            activity.runOnUiThread(() -> {
                CaptchaHelper.showCaptchaDialog(activity, sitekey, captchaToken -> {
                    handler.onCaptchaSolved(captchaToken, rqdata, rqtoken, sessionId);
                });
            });
        } else {
            Utility.showToast(Strings.getString("no_valid_activity"), Toast.LENGTH_LONG);
        }
    }

    public static void patchCurrentUser(
            JSONObject payload,
            Context context,
            Consumer<String> onSuccess,
            Consumer<Exception> onError,
            CaptchaHandler onCaptcha) {
        String url = "https://discord.com/api/v9/users/@me";
        String method = "PATCH";
        Map<String, String> headers = buildDefaultHeaders();

        sendApiRequest(
                url,
                method,
                headers,
                payload.toString(),
                context,
                onSuccess,
                onError,
                (captchaToken, rqdata, rqtoken, sessionId) -> {
                    try {
                        payload.put("captcha_key", captchaToken);
                        if (rqdata != null)
                            payload.put("captcha_rqdata", rqdata);
                        if (rqtoken != null)
                            payload.put("captcha_rqtoken", rqtoken);
                        if (sessionId != null)
                            payload.put("captcha_session_id", sessionId);
                    } catch (Exception ignored) {
                    }

                    sendApiRequest(
                            url,
                            method,
                            headers,
                            payload.toString(),
                            context,
                            onSuccess,
                            onError,
                            null);
                });
    }

    public static void setDisplayName(String newDisplayName, Context context, Consumer<Boolean> callback) {
        String field = "global_name";
        JSONObject payload = new JSONObject();
        try {
            payload.put(field, newDisplayName);
        } catch (Exception ignored) {
        }
        patchCurrentUser(
                payload,
                context,
                resp -> {
                    Utility.showToast(Strings.getString("display_name_saved"), Toast.LENGTH_LONG);
                    if (callback != null)
                        callback.accept(true);
                },
                e -> {
                    Utility.showToast(parseError(e, Strings.getString("display_name_save_failed"), field),
                            Toast.LENGTH_LONG);
                    if (callback != null)
                        callback.accept(false);
                },
                null);
    }

    public static void setPronouns(String pronouns, Context context) {
        String field = "pronouns";
        JSONObject payload = new JSONObject();
        try {
            payload.put(field, pronouns);
        } catch (Exception ignored) {
        }
        patchCurrentUser(
                payload,
                context,
                resp -> {
                },
                e -> Utility.showToast(parseError(e, Strings.getString("pronouns_save_failed"), field),
                        Toast.LENGTH_LONG),
                null);
    }

    private static Activity getActivityFromContext(Context context) {
        if (context instanceof Activity)
            return (Activity) context;
        try {
            Context base = (Context) context.getClass().getMethod("getBaseContext").invoke(context);
            if (base instanceof Activity)
                return (Activity) base;
        } catch (Exception ignored) {
        }
        return null;
    }

    public static String parseError(Exception e, String fallback, String errField) {
        if (e == null || e.getMessage() == null)
            return fallback;
        try {
            String msg = e.getMessage();
            int jsonStart = msg.indexOf('{');
            if (jsonStart != -1) {
                JSONObject obj = new JSONObject(msg.substring(jsonStart));
                JSONObject err = obj.getJSONObject("errors")
                        .getJSONObject(errField)
                        .getJSONArray("_errors")
                        .getJSONObject(0);
                String errorMsg = err.optString("message", null);
                if (errorMsg != null && !errorMsg.isEmpty())
                    return errorMsg;
            }
        } catch (Exception ignored) {
        }
        return fallback;
    }

    /*
     * Fetch latest Firefox Nightly version and Discord web client build number
     */
    public static class versionFetcher {
        private static final Pattern SCRIPT_SRC_PATTERN = Pattern.compile(
                "<script[^>]*src=[\"']([^\"']+\\.js)[\"']", Pattern.CASE_INSENSITIVE);
        private static final Pattern BUILD_NUMBER_PATTERN = Pattern.compile("b\\s*=\\s*[\"'](\\d+)[\"']");

        public static void initialise() {
            new Thread(() -> {
                try {
                    Http.Request req = new Http.Request(
                            "https://product-details.mozilla.org/1.0/firefox_versions.json", "GET");
                    Http.Response res = req.execute();
                    String json = res.text();
                    JSONObject obj = new JSONObject(json);
                    String nightly = obj.optString("FIREFOX_NIGHTLY", null);
                    if (nightly != null && !nightly.isEmpty()) {
                        nightly = nightly.replaceAll("^([0-9.]+).*", "$1");
                        FIREFOX_NIGHTLY_VERSION = nightly;
                    }
                } catch (Exception ignored) {
                }

                try {
                    Http.Request req = new Http.Request("https://discord.com/login", "GET");
                    req.setHeader("User-Agent", getMobileUA());
                    Http.Response resp = req.execute();
                    String html = resp.text();

                    ArrayList<String> assetUrls = new ArrayList<>();
                    Matcher scriptMatcher = SCRIPT_SRC_PATTERN.matcher(html);
                    while (scriptMatcher.find()) {
                        String jsPath = scriptMatcher.group(1);
                        if (!jsPath.startsWith("http"))
                            jsPath = "https://discord.com" + jsPath;
                        assetUrls.add(jsPath);
                    }

                    for (String jsUrl : assetUrls) {
                        try {
                            Http.Response jsResp = new Http.Request(jsUrl, "GET").execute();
                            String jsContent = jsResp.text();
                            Matcher buildMatcher = BUILD_NUMBER_PATTERN.matcher(jsContent);
                            if (buildMatcher.find()) {
                                CLIENT_BUILD_NUMBER = Integer.parseInt(buildMatcher.group(1));
                                break;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                } catch (Exception ignored) {
                }
            }).start();
        }
    }
}
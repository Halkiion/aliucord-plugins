package com.github.halkiion.plugins;

import android.content.Context;

import com.aliucord.Utils;

import com.discord.stores.StoreStream;
import com.discord.stores.StoreUserSettingsSystem;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class Strings {
    public static final Map<String, Map<String, String>> STRINGS = new HashMap<>();

    static {
        // English (en)
        put("pronouns_header", "en", "PRONOUNS");
        put("pronouns_hint", "en", "Enter pronouns");
        put("edit_display_name", "en", "Edit Display Name");
        put("display_name", "en", "Display Name");
        put("no_valid_activity", "en", "No valid Activity to show captcha dialog.");
        put("display_name_saved", "en", "Display Name saved.");
        put("display_name_save_failed", "en", "Failed to save Display Name");
        put("pronouns_save_failed", "en", "Failed to save Pronouns");

        // Danish (da)
        put("pronouns_header", "da", "PRONOMENER");
        put("pronouns_hint", "da", "Indtast pronomener");
        put("edit_display_name", "da", "Rediger visningsnavn");
        put("display_name", "da", "Visningsnavn");
        put("no_valid_activity", "da", "Ingen gyldig aktivitet til at vise captcha-dialog.");
        put("display_name_saved", "da", "Visningsnavn gemt.");
        put("display_name_save_failed", "da", "Kunne ikke gemme visningsnavn");
        put("pronouns_save_failed", "da", "Kunne ikke gemme pronomener");

        // German (de)
        put("pronouns_header", "de", "PRONOMEN");
        put("pronouns_hint", "de", "Pronomen eingeben");
        put("edit_display_name", "de", "Anzeigenamen bearbeiten");
        put("display_name", "de", "Anzeigename");
        put("no_valid_activity", "de", "Keine gültige Aktivität zum Anzeigen des Captcha-Dialogs.");
        put("display_name_saved", "de", "Anzeigename gespeichert.");
        put("display_name_save_failed", "de", "Anzeigename konnte nicht gespeichert werden");
        put("pronouns_save_failed", "de", "Pronomen konnten nicht gespeichert werden");

        // Spanish (es)
        put("pronouns_header", "es", "PRONOMBRES");
        put("pronouns_hint", "es", "Ingrese pronombres");
        put("edit_display_name", "es", "Editar nombre para mostrar");
        put("display_name", "es", "Nombre para mostrar");
        put("no_valid_activity", "es", "No hay actividad válida para mostrar el diálogo de captcha.");
        put("display_name_saved", "es", "Nombre para mostrar guardado.");
        put("display_name_save_failed", "es", "Error al guardar el nombre para mostrar");
        put("pronouns_save_failed", "es", "Error al guardar los pronombres");

        // French (fr)
        put("pronouns_header", "fr", "PRONOMS");
        put("pronouns_hint", "fr", "Entrez les pronoms");
        put("edit_display_name", "fr", "Modifier le nom affiché");
        put("display_name", "fr", "Nom affiché");
        put("no_valid_activity", "fr", "Aucune activité valide pour afficher la boîte de dialogue captcha.");
        put("display_name_saved", "fr", "Nom affiché enregistré.");
        put("display_name_save_failed", "fr", "Échec de l'enregistrement du nom affiché");
        put("pronouns_save_failed", "fr", "Échec de l'enregistrement des pronoms");

        // Croatian (hr)
        put("pronouns_header", "hr", "ZAMJENICE");
        put("pronouns_hint", "hr", "Unesite zamjenice");
        put("edit_display_name", "hr", "Uredi prikazano ime");
        put("display_name", "hr", "Prikazano ime");
        put("no_valid_activity", "hr", "Nema valjane aktivnosti za prikaz captcha dijaloga.");
        put("display_name_saved", "hr", "Prikazano ime spremljeno.");
        put("display_name_save_failed", "hr", "Neuspjelo spremanje prikazanog imena");
        put("pronouns_save_failed", "hr", "Neuspjelo spremanje zamjenica");

        // Italian (it)
        put("pronouns_header", "it", "PRONOMI");
        put("pronouns_hint", "it", "Inserisci i pronomi");
        put("edit_display_name", "it", "Modifica nome visualizzato");
        put("display_name", "it", "Nome visualizzato");
        put("no_valid_activity", "it", "Nessuna attività valida per mostrare la finestra captcha.");
        put("display_name_saved", "it", "Nome visualizzato salvato.");
        put("display_name_save_failed", "it", "Impossibile salvare il nome visualizzato");
        put("pronouns_save_failed", "it", "Impossibile salvare i pronomi");

        // Lithuanian (lt)
        put("pronouns_header", "lt", "ĮVARDŽIAI");
        put("pronouns_hint", "lt", "Įveskite įvardžius");
        put("edit_display_name", "lt", "Redaguoti rodomą vardą");
        put("display_name", "lt", "Rodomas vardas");
        put("no_valid_activity", "lt", "Nėra tinkamos veiklos rodyti captcha dialogui.");
        put("display_name_saved", "lt", "Rodomas vardas išsaugotas.");
        put("display_name_save_failed", "lt", "Nepavyko išsaugoti rodomo vardo");
        put("pronouns_save_failed", "lt", "Nepavyko išsaugoti įvardžių");

        // Hungarian (hu)
        put("pronouns_header", "hu", "NÉVMÁSOK");
        put("pronouns_hint", "hu", "Adja meg a névmásokat");
        put("edit_display_name", "hu", "Megjelenített név szerkesztése");
        put("display_name", "hu", "Megjelenített név");
        put("no_valid_activity", "hu", "Nincs érvényes tevékenység a captcha párbeszédablak megjelenítéséhez.");
        put("display_name_saved", "hu", "Megjelenített név elmentve.");
        put("display_name_save_failed", "hu", "Nem sikerült menteni a megjelenített nevet");
        put("pronouns_save_failed", "hu", "Nem sikerült menteni a névmásokat");

        // Dutch (nl)
        put("pronouns_header", "nl", "VOORNAAMWOORDEN");
        put("pronouns_hint", "nl", "Voer voornaamwoorden in");
        put("edit_display_name", "nl", "Bewerk weergavenaam");
        put("display_name", "nl", "Weergavenaam");
        put("no_valid_activity", "nl", "Geen geldige activiteit om captcha-dialoog te tonen.");
        put("display_name_saved", "nl", "Weergavenaam opgeslagen.");
        put("display_name_save_failed", "nl", "Weergavenaam opslaan mislukt");
        put("pronouns_save_failed", "nl", "Opslaan van voornaamwoorden mislukt");

        // Norwegian (no)
        put("pronouns_header", "no", "PRONOMER");
        put("pronouns_hint", "no", "Skriv inn pronomen");
        put("edit_display_name", "no", "Rediger visningsnavn");
        put("display_name", "no", "Visningsnavn");
        put("no_valid_activity", "no", "Ingen gyldig aktivitet for å vise captcha-dialog.");
        put("display_name_saved", "no", "Visningsnavn lagret.");
        put("display_name_save_failed", "no", "Klarte ikke å lagre visningsnavn");
        put("pronouns_save_failed", "no", "Klarte ikke å lagre pronomen");

        // Polish (pl)
        put("pronouns_header", "pl", "ZAIMKI");
        put("pronouns_hint", "pl", "Wprowadź zaimki");
        put("edit_display_name", "pl", "Edytuj nazwę wyświetlaną");
        put("display_name", "pl", "Nazwa wyświetlana");
        put("no_valid_activity", "pl", "Brak prawidłowej aktywności do wyświetlenia okna captcha.");
        put("display_name_saved", "pl", "Nazwa wyświetlana zapisana.");
        put("display_name_save_failed", "pl", "Nie udało się zapisać nazwy wyświetlanej");
        put("pronouns_save_failed", "pl", "Nie udało się zapisać zaimków");

        // Brazilian Portuguese (pt-BR)
        put("pronouns_header", "pt-BR", "PRONOMES");
        put("pronouns_hint", "pt-BR", "Insira os pronomes");
        put("edit_display_name", "pt-BR", "Editar nome de exibição");
        put("display_name", "pt-BR", "Nome de exibição");
        put("no_valid_activity", "pt-BR", "Nenhuma atividade válida para mostrar o diálogo captcha.");
        put("display_name_saved", "pt-BR", "Nome de exibição salvo.");
        put("display_name_save_failed", "pt-BR", "Falha ao salvar o nome de exibição");
        put("pronouns_save_failed", "pt-BR", "Falha ao salvar os pronomes");

        // Romanian (ro)
        put("pronouns_header", "ro", "PRONUME");
        put("pronouns_hint", "ro", "Introduceți pronumele");
        put("edit_display_name", "ro", "Editați numele afișat");
        put("display_name", "ro", "Nume afișat");
        put("no_valid_activity", "ro", "Nicio activitate validă pentru a afișa dialogul captcha.");
        put("display_name_saved", "ro", "Nume afișat salvat.");
        put("display_name_save_failed", "ro", "Nu s-a putut salva numele afișat");
        put("pronouns_save_failed", "ro", "Nu s-au putut salva pronumele");

        // Finnish (fi)
        put("pronouns_header", "fi", "PRONOMINIT");
        put("pronouns_hint", "fi", "Anna pronominit");
        put("edit_display_name", "fi", "Muokkaa näyttönimeä");
        put("display_name", "fi", "Näyttönimi");
        put("no_valid_activity", "fi", "Ei kelvollista toimintoa captcha-ikkunan näyttämiseen.");
        put("display_name_saved", "fi", "Näyttönimi tallennettu.");
        put("display_name_save_failed", "fi", "Näyttönimen tallennus epäonnistui");
        put("pronouns_save_failed", "fi", "Pronominien tallennus epäonnistui");

        // Swedish (sv)
        put("pronouns_header", "sv", "PRONOMEN");
        put("pronouns_hint", "sv", "Ange pronomen");
        put("edit_display_name", "sv", "Redigera visningsnamn");
        put("display_name", "sv", "Visningsnamn");
        put("no_valid_activity", "sv", "Ingen giltig aktivitet för att visa captcha-dialog.");
        put("display_name_saved", "sv", "Visningsnamn sparat.");
        put("display_name_save_failed", "sv", "Kunde inte spara visningsnamn");
        put("pronouns_save_failed", "sv", "Kunde inte spara pronomen");

        // Vietnamese (vi)
        put("pronouns_header", "vi", "ĐẠI TỪ");
        put("pronouns_hint", "vi", "Nhập đại từ");
        put("edit_display_name", "vi", "Chỉnh sửa tên hiển thị");
        put("display_name", "vi", "Tên hiển thị");
        put("no_valid_activity", "vi", "Không có hoạt động hợp lệ để hiển thị hộp thoại captcha.");
        put("display_name_saved", "vi", "Đã lưu tên hiển thị.");
        put("display_name_save_failed", "vi", "Lưu tên hiển thị thất bại");
        put("pronouns_save_failed", "vi", "Lưu đại từ thất bại");

        // Turkish (tr)
        put("pronouns_header", "tr", "ZAMİRLER");
        put("pronouns_hint", "tr", "Zamirleri girin");
        put("edit_display_name", "tr", "Görünen Adı Düzenle");
        put("display_name", "tr", "Görünen Ad");
        put("no_valid_activity", "tr", "Captcha penceresini göstermek için geçerli bir etkinlik yok.");
        put("display_name_saved", "tr", "Görünen Ad kaydedildi.");
        put("display_name_save_failed", "tr", "Görünen Ad kaydedilemedi");
        put("pronouns_save_failed", "tr", "Zamirler kaydedilemedi");

        // Czech (cs)
        put("pronouns_header", "cs", "ZÁJMENA");
        put("pronouns_hint", "cs", "Zadejte zájmena");
        put("edit_display_name", "cs", "Upravit zobrazované jméno");
        put("display_name", "cs", "Zobrazované jméno");
        put("no_valid_activity", "cs", "Žádná platná aktivita pro zobrazení captcha dialogu.");
        put("display_name_saved", "cs", "Zobrazované jméno uloženo.");
        put("display_name_save_failed", "cs", "Nepodařilo se uložit zobrazované jméno");
        put("pronouns_save_failed", "cs", "Nepodařilo se uložit zájmena");

        // Greek (el)
        put("pronouns_header", "el", "ΑΝΤΩΝΥΜΙΕΣ");
        put("pronouns_hint", "el", "Εισαγάγετε αντωνυμίες");
        put("edit_display_name", "el", "Επεξεργασία εμφανιζόμενου ονόματος");
        put("display_name", "el", "Εμφανιζόμενο όνομα");
        put("no_valid_activity", "el", "Δεν υπάρχει έγκυρη δραστηριότητα για εμφάνιση του captcha.");
        put("display_name_saved", "el", "Το εμφανιζόμενο όνομα αποθηκεύτηκε.");
        put("display_name_save_failed", "el", "Αποτυχία αποθήκευσης του εμφανιζόμενου ονόματος");
        put("pronouns_save_failed", "el", "Αποτυχία αποθήκευσης αντωνυμιών");

        // Bulgarian (bg)
        put("pronouns_header", "bg", "МЕСТОИМЕНИЯ");
        put("pronouns_hint", "bg", "Въведете местоимения");
        put("edit_display_name", "bg", "Редактиране на показваното име");
        put("display_name", "bg", "Показвано име");
        put("no_valid_activity", "bg", "Няма валидна дейност за показване на captcha диалог.");
        put("display_name_saved", "bg", "Показваното име е запазено.");
        put("display_name_save_failed", "bg", "Неуспешно запазване на показваното име");
        put("pronouns_save_failed", "bg", "Неуспешно запазване на местоимения");

        // Russian (ru)
        put("pronouns_header", "ru", "МЕСТОИМЕНИЯ");
        put("pronouns_hint", "ru", "Введите местоимения");
        put("edit_display_name", "ru", "Изменить отображаемое имя");
        put("display_name", "ru", "Отображаемое имя");
        put("no_valid_activity", "ru", "Нет действительной активности для показа диалога captcha.");
        put("display_name_saved", "ru", "Отображаемое имя сохранено.");
        put("display_name_save_failed", "ru", "Не удалось сохранить отображаемое имя");
        put("pronouns_save_failed", "ru", "Не удалось сохранить местоимения");

        // Ukrainian (uk)
        put("pronouns_header", "uk", "ЗАЙМЕННИКИ");
        put("pronouns_hint", "uk", "Введіть займенники");
        put("edit_display_name", "uk", "Редагувати відображуване ім'я");
        put("display_name", "uk", "Відображуване ім'я");
        put("no_valid_activity", "uk", "Немає дійсної активності для показу діалогу captcha.");
        put("display_name_saved", "uk", "Відображуване ім'я збережено.");
        put("display_name_save_failed", "uk", "Не вдалося зберегти відображуване ім'я");
        put("pronouns_save_failed", "uk", "Не вдалося зберегти займенники");

        // Japanese (ja)
        put("pronouns_header", "ja", "代名詞");
        put("pronouns_hint", "ja", "代名詞を入力");
        put("edit_display_name", "ja", "表示名を編集");
        put("display_name", "ja", "表示名");
        put("no_valid_activity", "ja", "キャプチャダイアログを表示する有効なアクティビティがありません。");
        put("display_name_saved", "ja", "表示名が保存されました。");
        put("display_name_save_failed", "ja", "表示名の保存に失敗しました");
        put("pronouns_save_failed", "ja", "代名詞の保存に失敗しました");

        // Chinese (Taiwan) (zh-TW)
        put("pronouns_header", "zh-TW", "代名詞");
        put("pronouns_hint", "zh-TW", "輸入代名詞");
        put("edit_display_name", "zh-TW", "編輯顯示名稱");
        put("display_name", "zh-TW", "顯示名稱");
        put("no_valid_activity", "zh-TW", "沒有有效的活動可顯示驗證碼對話框。");
        put("display_name_saved", "zh-TW", "顯示名稱已儲存。");
        put("display_name_save_failed", "zh-TW", "無法儲存顯示名稱");
        put("pronouns_save_failed", "zh-TW", "無法儲存代名詞");

        // Thai (th)
        put("pronouns_header", "th", "สรรพนาม");
        put("pronouns_hint", "th", "กรอกสรรพนาม");
        put("edit_display_name", "th", "แก้ไขชื่อที่แสดง");
        put("display_name", "th", "ชื่อที่แสดง");
        put("no_valid_activity", "th", "ไม่มีกิจกรรมที่ถูกต้องเพื่อแสดงกล่องโต้ตอบ captcha");
        put("display_name_saved", "th", "บันทึกชื่อที่แสดงแล้ว");
        put("display_name_save_failed", "th", "ไม่สามารถบันทึกชื่อที่แสดง");
        put("pronouns_save_failed", "th", "ไม่สามารถบันทึกสรรพนาม");

        // Chinese (China) (zh-CN)
        put("pronouns_header", "zh-CN", "代词");
        put("pronouns_hint", "zh-CN", "输入代词");
        put("edit_display_name", "zh-CN", "编辑显示名称");
        put("display_name", "zh-CN", "显示名称");
        put("no_valid_activity", "zh-CN", "没有有效的活动来显示验证码对话框。");
        put("display_name_saved", "zh-CN", "显示名称已保存。");
        put("display_name_save_failed", "zh-CN", "保存显示名称失败");
        put("pronouns_save_failed", "zh-CN", "保存代词失败");

        // Korean (ko)
        put("pronouns_header", "ko", "대명사");
        put("pronouns_hint", "ko", "대명사를 입력하세요");
        put("edit_display_name", "ko", "표시 이름 편집");
        put("display_name", "ko", "표시 이름");
        put("no_valid_activity", "ko", "캡차 대화 상자를 표시할 유효한 활동이 없습니다.");
        put("display_name_saved", "ko", "표시 이름이 저장되었습니다.");
        put("display_name_save_failed", "ko", "표시 이름 저장 실패");
        put("pronouns_save_failed", "ko", "대명사 저장 실패");

        // Hindi (hi)
        put("pronouns_header", "hi", "सर्वनाम");
        put("pronouns_hint", "hi", "सर्वनाम दर्ज करें");
        put("edit_display_name", "hi", "प्रदर्शन नाम संपादित करें");
        put("display_name", "hi", "प्रदर्शन नाम");
        put("no_valid_activity", "hi", "कैप्चा संवाद दिखाने के लिए कोई मान्य गतिविधि नहीं है।");
        put("display_name_saved", "hi", "प्रदर्शन नाम सहेजा गया।");
        put("display_name_save_failed", "hi", "प्रदर्शन नाम सहेजने में विफल");
        put("pronouns_save_failed", "hi", "सर्वनाम सहेजने में विफल");
    }

    private static void put(String key, String lang, String value) {
        STRINGS.computeIfAbsent(key, k -> new HashMap<>()).put(lang, value);
    }

    public static String getString(String key) {
        StoreUserSettingsSystem userSettingsSystem = StoreStream.getUserSettingsSystem();
        String locale = userSettingsSystem.getLocale().replace("_", "-");
        Map<String, String> translations = STRINGS.get(key);
        if (translations == null)
            return null;

        String result = translations.get(locale);
        if (result == null && locale.contains("-"))
            result = translations.get(locale.split("-")[0]);

        if (result == null)
            result = translations.get("en");

        return result;
    }
}
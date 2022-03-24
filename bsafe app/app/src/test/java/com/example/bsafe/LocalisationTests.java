package com.example.bsafe;

import com.example.bsafe.I18n.Localizer;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;


public class LocalisationTests
{
    private final Context context = new Context();

    @Test
    public void localiseText()
    {
        boolean success = true;

        Localizer englishLocalizer = new Localizer(context.englishLocale);
        for (Locale locale: context.supportedLocales)
        {
            if (locale != context.englishLocale)
            {
                Localizer localizer = new Localizer(locale);
                for (String key: context.MessagesKeys)
                {
                    if (context.isWordIgnored(key, locale))
                        continue;

                    if (englishLocalizer.get(key).equals(localizer.get(key)))
                    {
                        context.logDuplicateText(englishLocalizer.getLocale(), locale, key);
                        success = false;
                    }
                    else if (localizer.get(key).equals(""))
                    {
                        context.logMissingText(englishLocalizer.getLocale(), locale, key);
                        success = false;
                    }
                }
            }
        }

        if (!success)
        {
            System.err.println("If any of these are expected behaviour, add them to the ignore list.");
        }

        assert(success);
    }

    @Test
    public void changeLocale()
    {
        boolean success = true;

        for (int i = 0; i < context.supportedLocales.length; i++)
        {
            String key = "SHOW_ALL";
            Locale locale = context.supportedLocales[i];
            Localizer localizer = new Localizer(locale);
            String text = localizer.get(key);

            for (int j = i + 1; j < context.supportedLocales.length; j++)
            {
                Locale _locale = context.supportedLocales[j];
                localizer.setLocale(_locale);

                if (!context.isWordIgnored(key, locale, _locale) && text.equals(localizer.get(key)))
                {
                    context.logDuplicateText(locale, _locale, key);
                    success = false;
                }
            }
        }

        if (!success)
        {
            System.err.println("If any of these are expected behaviour, add them to the ignore list.");
        }

        assert(success);
    }

    private class Context
    {
        public Locale englishLocale;
        public Locale[] supportedLocales;
        public Set<String> MessagesKeys;

        // Ignores keys when localising to given locale
        public HashMap<String, Set<String>> ignoredWords = new HashMap<String, Set<String>>()
        {{
            //put("SHOW_ALL", new HashSet<String>(Arrays.asList("ro")));
        }};

        public Context ()
        {
            englishLocale = new Locale("en");
            supportedLocales = new Locale[]
            {
                englishLocale,
                new Locale("ro"),
                new Locale("it"),
                new Locale("cy")
            };

            ResourceBundle bundle = ResourceBundle.getBundle("locales.messages");
            MessagesKeys = bundle.keySet();
        }

        public void logDuplicateText(Locale locale1, Locale locale2, String key)
        {
            System.err.printf("Duplicate text between locales '%s' and '%s' for '%s'%n", locale1.getLanguage(), locale2.getLanguage(), key);
        }

        public void logMissingText(Locale locale, Locale locale2, String key)
        {
            System.err.printf("Text found in '%s' is missing in locale '%s' for '%s'%n", locale.getLanguage(), locale2.getLanguage(), key);
        }

        public boolean isWordIgnored(String key, Locale... locales)
        {
            if (ignoredWords.containsKey(key))
            {
                for (Locale locale: locales)
                {
                    if (ignoredWords.get(key).contains(locale.toString()))
                    {
                        return true;
                    }
                }
            }

            return false;
        }
    }
}

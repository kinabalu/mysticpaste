package com.mysticcoders.mysticpaste.utils;

/**
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: 1/4/12
 * Time: 5:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringUtils {

    public static boolean hasSpamKeywords(String content) {
        String lowercasedContent = content.toLowerCase();

        for (String badWord : badWords) {
            if (lowercasedContent.indexOf(badWord) != -1) return true;
        }

        return false;
    }

    private static String[] badWords = new String[]{
            "[/URL]",
            "[/url]",
            "adipex",
            "adultfriendfinder",
            "adult-dvd",
            "adult-friend-finder",
            "adult-personal",
            "adult personal",
            "adult-stories",
            "adult friends",
            "boob",
            "casino",
            "cheap hotel",
            "cialis",
            "classified ad",
            "diazepam",
            "diazepan",
            "fiksa.org",
            "forexcurrency",
            "free ringtones",
            "fuck",
            "gay porn",
            "geo.ya",
            "httpgeo",
            "hot sex",
            "hydroconone",
            "incest",
            "inderal",
            "insulin",
            "jewish dating",
            "keflex",
            "klonopin",
            "lamictal",
            "lasix",
            "levaquin",
            "levitra",
            "lipitor",
            "male porn",
            "malenhancement",
            "masya",
            "mature porn",
            "milf",
            "murphy bed",
            "nude celebrity",
            "oxycodone",
            "paxil",
            "payday",
            "phenergan",
            "phentermine",
            "poker",
            "porn link",
            "porn video",
            "porno portal",
            "pornmaster",
            "premarin",
            "prozac",
            "rape",
            "strattera",
            "tramadol",
            "tussionex",
            "valium",
            "viagra",
            "vicodin",
            "web gratis",
            "without prescription",
            "xanax",
            "xxx ",
            " xxx",
            "xxxvideo",
            "youradult",
            "zelnorm",
            "zenegra",
            "megaupload.com",
            "members.lycos.co.uk",
            "lix.in"
    };
}

package oais.regex;

import java.util.regex.Pattern;

public class RegexOrigin {
    public static final Pattern REGEX_A = Pattern.compile("(A)\\s*?_(FPS[X\\d]+)_([X\\d]+-?[X\\d]+)_([X\\d-]+)_([X\\d]{1,2})_([X\\d]{1,2})_([X\\d]{1,4})_([X\\d]+)_([X\\d]+)_([X\\d]+)_(\\d+)_(\\d+)(_(TIPO_\\d+))*");
    public static final Pattern REGEX_B = Pattern.compile("(B)\\s*?_(FPS[X\\d]+)_([X\\d]+-?[X\\d]+)_([A-zÁ-ú-]+)_([X\\d]{1,2})_([X\\d]{1,2})_([X\\d]{1,4})_([X\\d]+)_([X\\d]+)_([X\\d]+)_(\\d+)_(\\d+)(_(TIPO_\\d+))*");
    public static final Pattern REGEX_PA = Pattern.compile("(PA)\\s*?_(FPS[X\\d]+)_([X\\d]+-?[X\\d]+)_([X\\d-]+)_([X\\d]{1,2})_([X\\d]{1,2})_([X\\d]{1,4})_([X\\d]+)_(\\d+)_(\\d+)(_(TIPO_\\d+))*");
}
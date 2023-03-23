package com.tcblauweiss.getraenkeabrechner.util;

import java.text.NumberFormat;
import java.util.Locale;

public class StringFormatter {

    public static String formatToCurrencyString(double number) {
        Locale currentLocale = Locale.GERMANY;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(currentLocale);

        return currencyFormatter.format(number);
    }
}

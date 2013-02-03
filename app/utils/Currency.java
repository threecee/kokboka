package utils;

import play.Logger;

import java.text.DecimalFormat;

public class Currency {

    public static String prettyDouble(String doubleString) {
        try{
        return prettyDouble(Double.parseDouble(doubleString));
        } catch (NumberFormatException e)
        {
            Logger.error(e.getMessage(), e);

        }
        return "undefined";
    }

    public static String prettyDouble(double doubleString) {
        DecimalFormat df2 = new DecimalFormat("#####0.0");
        double dd2dec = new Double(df2.format(doubleString)).doubleValue();

        String newValue = dd2dec + "";

        return newValue.replaceAll("\\.0", "");

    }

}

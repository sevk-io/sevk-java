package io.sevk.markup;

final class JsNumber {
    private JsNumber() {
    }

    static String format(double value) {
        if (value == 0) {
            return "0";
        }
        if (Double.isNaN(value)) {
            return "NaN";
        }
        if (Double.isInfinite(value)) {
            return value > 0 ? "Infinity" : "-Infinity";
        }

        String[] parts = Double.toString(Math.abs(value)).split("E");
        String[] mantissa = parts[0].split("\\.");
        String fraction = mantissa.length > 1 ? mantissa[1].replaceAll("0+$", "") : "";
        String digits = mantissa[0] + fraction;
        String trimmed = digits.replaceFirst("^0+", "");
        int point = mantissa[0].length() + (parts.length > 1 ? Integer.parseInt(parts[1]) : 0) - (digits.length() - trimmed.length());
        int count = trimmed.length();

        String text;
        if (count <= point && point <= 21) {
            text = trimmed + "0".repeat(point - count);
        } else if (point > 0 && point <= 21) {
            text = trimmed.substring(0, point) + "." + trimmed.substring(point);
        } else if (point > -6 && point <= 0) {
            text = "0." + "0".repeat(-point) + trimmed;
        } else {
            int power = point - 1;
            String exponent = "e" + (power < 0 ? "-" : "+") + Math.abs(power);
            text = count == 1 ? trimmed + exponent : trimmed.charAt(0) + "." + trimmed.substring(1) + exponent;
        }

        return value < 0 ? "-" + text : text;
    }
}

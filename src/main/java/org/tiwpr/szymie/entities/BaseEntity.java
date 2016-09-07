package org.tiwpr.szymie.entities;

import org.tiwpr.szymie.models.Model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BaseEntity {

    public abstract Model toModel();

    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public static Date dateFromString(String date) {
        try {
            return dateFormat.parse(date);
        } catch (Exception e) {
            return null;
        }
    }

    public static String stringFromDate(Date date) {
        return dateFormat.format(date);
    }
}

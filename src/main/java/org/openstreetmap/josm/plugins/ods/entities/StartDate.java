package org.openstreetmap.josm.plugins.ods.entities;

import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;

/**
 * Representation of an OSM start_date.
 * 
 * The start date can be represented in 3 ways:
 * Year 
 * Year and Month
 * Year, month and day.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class StartDate {
    private final Temporal originalValue;
    private final LocalDate dateValue;
    
    public StartDate(Year year) {
        this.originalValue = year;
        this.dateValue = LocalDate.of(year.getValue(), 7, 1);
    }
    
    public StartDate(YearMonth yearMonth) {
        this.originalValue = yearMonth;
        this.dateValue = LocalDate.of(yearMonth.getYear(), 
                yearMonth.getMonthValue(), 15);
    }
    
    public StartDate(LocalDate date) {
        this.originalValue = date;
        this.dateValue = date;
    }

    public Temporal getOriginalValue() {
        return originalValue;
    }

    public LocalDate getDateValue() {
        return dateValue;
    }
    
    /**
     * Get the age of this start date object as a Period.
     * @return
     */
    public Period getAge() {
        return Period.between(dateValue, LocalDate.now());
    }
    
    public static StartDate parse(String value) {
        try {
            switch (value.length()) {
            case 10:
            case 9:
            case 8:
                return new StartDate(LocalDate.parse(value));
            case 7:
            case 6:
                return new StartDate(YearMonth.parse(value));
            case 4:
                return new StartDate(Year.parse(value));
            default:
                return null;
            }
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    
    @Override
    public int hashCode() {
        return originalValue.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof StartDate) {
            return ((StartDate)obj).originalValue.equals(originalValue);
        }
        return false;
    }

    @Override
    public String toString() {
        return originalValue.toString();
    }
}

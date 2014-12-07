package com.jscriptive.moneyfx.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.jscriptive.moneyfx.util.BigDecimalUtils.isEqual;
import static java.lang.String.format;
import static java.time.LocalDate.now;
import static java.time.LocalDate.of;

/**
 * Created by jscriptive.com on 30/11/14.
 */
public class TransactionVolume {

    private Account account;
    private Category category;
    private Integer year;
    private Integer month;
    private Integer day;
    private BigDecimal volume;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public LocalDate getDate() {
        LocalDate now = now();
        int _year = (year == null) ? now.getYear() : year;
        int _month = (month == null) ? now.getMonthValue() : month;
        int _day = (day == null) ? now.getDayOfMonth() : day;
        return of(_year, _month, _day);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionVolume)) return false;

        TransactionVolume that = (TransactionVolume) o;

        if (account != null ? !account.equals(that.account) : that.account != null) return false;
        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        if (day != null ? !day.equals(that.day) : that.day != null) return false;
        if (month != null ? !month.equals(that.month) : that.month != null) return false;
        if (year != null ? !year.equals(that.year) : that.year != null) return false;
        return isEqual(volume, that.volume);
    }

    @Override
    public int hashCode() {
        int result = account != null ? account.hashCode() : 0;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (year != null ? year.hashCode() : 0);
        result = 31 * result + (month != null ? month.hashCode() : 0);
        result = 31 * result + (day != null ? day.hashCode() : 0);
        result = 31 * result + (volume != null ? volume.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return format("TransactionVolume{account=%s, category=%s, year=%d, month=%d, day=%d, volume=%s}", account, category, year, month, day, volume);
    }
}

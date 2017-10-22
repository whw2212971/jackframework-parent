package org.jackframework.testservice.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class TData {

    protected Long dataId;

    protected String dataString;

    protected Integer dataInt;

    protected BigDecimal dataDecimal;

    protected Date dataDate;

    protected Date dataDatetime;

    protected Boolean dataBoolean;

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public String getDataString() {
        return dataString;
    }

    public void setDataString(String dataString) {
        this.dataString = dataString;
    }

    public Integer getDataInt() {
        return dataInt;
    }

    public void setDataInt(Integer dataInt) {
        this.dataInt = dataInt;
    }

    public BigDecimal getDataDecimal() {
        return dataDecimal;
    }

    public void setDataDecimal(BigDecimal dataDecimal) {
        this.dataDecimal = dataDecimal;
    }

    public Date getDataDate() {
        return dataDate;
    }

    public void setDataDate(Date dataDate) {
        this.dataDate = dataDate;
    }

    public Date getDataDatetime() {
        return dataDatetime;
    }

    public void setDataDatetime(Date dataDatetime) {
        this.dataDatetime = dataDatetime;
    }

    public Boolean getDataBoolean() {
        return dataBoolean;
    }

    public void setDataBoolean(Boolean dataBoolean) {
        this.dataBoolean = dataBoolean;
    }

}

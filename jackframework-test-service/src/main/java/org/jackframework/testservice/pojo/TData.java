package org.jackframework.testservice.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class TData {

    private Long dataId;

    private String dataString;

    private Integer dataInt;

    private BigDecimal dataDecimal;

    private Date dataDate;

    private Date dataDatetime;

    private Boolean dataBoolean;

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public Long getDataId() {
        return dataId;
    }

    public void setDataString(String dataString) {
        this.dataString = dataString;
    }

    public String getDataString() {
        return dataString;
    }

    public void setDataInt(Integer dataInt) {
        this.dataInt = dataInt;
    }

    public Integer getDataInt() {
        return dataInt;
    }

    public void setDataDecimal(BigDecimal dataDecimal) {
        this.dataDecimal = dataDecimal;
    }

    public BigDecimal getDataDecimal() {
        return dataDecimal;
    }

    public void setDataDate(Date dataDate) {
        this.dataDate = dataDate;
    }

    public Date getDataDate() {
        return dataDate;
    }

    public void setDataDatetime(Date dataDatetime) {
        this.dataDatetime = dataDatetime;
    }

    public Date getDataDatetime() {
        return dataDatetime;
    }

    public void setDataBoolean(Boolean dataBoolean) {
        this.dataBoolean = dataBoolean;
    }

    public Boolean getDataBoolean() {
        return dataBoolean;
    }


}
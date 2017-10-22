package org.serviceframework.component;

import com.github.pagehelper.Page;

import java.util.List;

public class Pagination<T> {

    protected Integer pageNum;

    protected Integer pageSize;

    protected Long total;

    protected List<T> list;

    public Pagination() {
    }

    public Pagination(Page<T> page) {
        this.pageNum = page.getPageNum();
        this.pageSize = page.getPageSize();
        this.total = page.getTotal();
        this.list = page.getResult();
    }

    public static <T> Pagination<T> toPagination(Page<T> page) {
        return new Pagination<T>(page);
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

}

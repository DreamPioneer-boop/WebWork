package com.fitness.entity;

import java.util.List;

/**
 * 分页结果封装：总条数 + 当前页数据。
 */
public class PageResult<T> {

    private long total;
    private List<T> records;

    public PageResult() {
    }

    public PageResult(long total, List<T> records) {
        this.total = total;
        this.records = records;
    }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public List<T> getRecords() { return records; }
    public void setRecords(List<T> records) { this.records = records; }

    /** 总页数（供 JSP 分页条使用） */
    public int getPages(int size) {
        if (size <= 0) {
            return 0;
        }
        return (int) Math.ceil(total * 1.0 / size);
    }
}

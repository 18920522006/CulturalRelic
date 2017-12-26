package com.base.convert;

/**
 *  转换 ext js 的分页属性 和 mapper 的分页属性
 * @param <T>
 */
public class PageInfo<T> extends com.github.pagehelper.PageInfo<T> {

    private String _dc;

    private int page;

    private int start;

    private int limit;

    public String get_dc() {
        return _dc;
    }

    public void set_dc(String _dc) {
        this._dc = _dc;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.setPageNum(page);
        this.setPageSize(this.limit - this.start);
        this.page = page;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}

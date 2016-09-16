package org.tiwpr.szymie.resources;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

/**
 * Created by szymie on 02.08.16.
 */
public class PaginationFilter {

    @QueryParam("offset")
    @DefaultValue("0")
    private int offset;
    @QueryParam("limit")
    @DefaultValue("10")
    private int limit;

    public PaginationFilter() {
    }

    public PaginationFilter(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}

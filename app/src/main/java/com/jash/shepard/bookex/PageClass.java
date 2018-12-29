package com.jash.shepard.bookex;

public class PageClass {
    private String page_text;
    private int page_number;

    public PageClass(String page_text, int page_number) {
        this.page_text = page_text;
        this.page_number = page_number;
    }

    public String getPage_text() {
        return page_text;
    }

    public void setPage_text(String page_text) {
        this.page_text = page_text;
    }

    public int getPage_number() {
        return page_number;
    }

    public void setPage_number(int page_number) {
        this.page_number = page_number;
    }
}

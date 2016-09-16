package org.tiwpr.szymie.models;

import java.util.List;

public class ModelWithLinks<T> {

    private T content;
    private List<Link> links;

    public ModelWithLinks() {
    }

    public ModelWithLinks(T content, List<Link> links) {
        this.content = content;
        this.links = links;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}

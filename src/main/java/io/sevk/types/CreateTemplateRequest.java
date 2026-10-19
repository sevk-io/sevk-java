package io.sevk.types;

public class CreateTemplateRequest {
    public String title;
    public String content;
    public ContentStyle style;

    public CreateTemplateRequest title(String title) {
        this.title = title;
        return this;
    }

    public CreateTemplateRequest content(String content) {
        this.content = content;
        return this;
    }

    public CreateTemplateRequest style(ContentStyle style) {
        this.style = style;
        return this;
    }
}

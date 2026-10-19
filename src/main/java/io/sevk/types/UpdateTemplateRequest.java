package io.sevk.types;

public class UpdateTemplateRequest {
    public String title;
    public String content;
    public ContentStyle style;

    public UpdateTemplateRequest title(String title) {
        this.title = title;
        return this;
    }

    public UpdateTemplateRequest content(String content) {
        this.content = content;
        return this;
    }

    public UpdateTemplateRequest style(ContentStyle style) {
        this.style = style;
        return this;
    }
}

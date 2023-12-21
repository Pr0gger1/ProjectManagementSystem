package ru.sfedu.projectmanagement.core.model;

import com.opencsv.bean.CsvBindByName;
import ru.sfedu.projectmanagement.core.model.enums.EntityType;

import java.util.Objects;
import java.util.UUID;

public class DocumentationData implements Entity {
    @CsvBindByName(column = "id", required = true)
    private UUID id;

    @CsvBindByName(column = "article_title", required = true)
    private String articleTitle;

    @CsvBindByName(column = "article", required = true)
    private String article;

    public DocumentationData() {}
    public DocumentationData(UUID id, String articleTitle, String article) {
        this.id = id;
        this.article = article;
        this.articleTitle = articleTitle;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        DocumentationData that = (DocumentationData) object;
        return Objects.equals(id, that.id) && Objects.equals(articleTitle, that.articleTitle) && Objects.equals(article, that.article);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, articleTitle, article);
    }

    @Override
    public String toString() {
        return "DocumentationData{" +
                "id=" + id +
                ", articleTitle='" + articleTitle + '\'' +
                ", article='" + article + '\'' +
                '}';
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.DocumentationData;
    }

    @Override
    public UUID getId() {
        return id;
    }
}

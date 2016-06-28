package sk.eea.arttag.game.model;

public class CardMetadata {

    private String author;
    private String externalUrl;
    private String description;

    public CardMetadata(String author, String externalUrl, String description) {
        this.author = author;
        this.externalUrl = externalUrl;
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "CardMetadata [author=" + author + ", externalUrl=" + externalUrl + ", description=" + description + "]";
    }
}

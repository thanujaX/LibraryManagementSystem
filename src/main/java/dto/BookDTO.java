package dto;

public class BookDTO {
    private String id;
    private String title;
    private String author;
    private String category;
    private String publishedYear;
    private int quantity;

    public BookDTO() {}
    public BookDTO(String id, String title, String author, String category, String publishedYear, int quantity) {
        this.id = id; this.title = title; this.author = author;
        this.category = category; this.publishedYear = publishedYear; this.quantity = quantity;
    }
    public String getId() { return id; } public void setId(String id) { this.id = id; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; } public void setAuthor(String author) { this.author = author; }
    public String getCategory() { return category; } public void setCategory(String category) { this.category = category; }
    public String getPublishedYear() { return publishedYear; } public void setPublishedYear(String publishedYear) { this.publishedYear = publishedYear; }
    public int getQuantity() { return quantity; } public void setQuantity(int quantity) { this.quantity = quantity; }
}
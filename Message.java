import java.io.Serializable;

public class Message implements Serializable {
  private String author;
  private String contents;

  public Message(String author, String contents) {
    this.author = author;
    this.contents = contents;
  }

  public String getAuthor() {
    return author;
  }

  public String getContents() {
    return contents;
  }
}

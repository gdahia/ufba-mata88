import java.io.Serializable;

public class Message implements Serializable {
  private String author;
  private String contents;
  private String replyInformation;
  private boolean editable;

  public Message(String author, String contents, boolean editable) {
    this.author = author;
    this.contents = contents;
    this.replyInformation = "";
    this.editable = editable;
  }

  public Message(String author, String contents, String replyInformation) {
    this.author = author;
    this.contents = contents;
    this.replyInformation = replyInformation;
    editable = true;
  }

  public String getAuthor() {
    return author;
  }

  public String getContents() {
    return contents;
  }

  public String getReplyInformation() {
    return replyInformation;
  }

  public boolean isEditable() {
    return editable;
  }

  public void setContents(String newContents) {
    contents = newContents;
  }
}

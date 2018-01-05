import java.io.Serializable;

public class Message implements Serializable {
  private String author;
  private String contents;
  private String replyInformation;
  private boolean editStatus;

  public Message(String author, String contents, boolean editable) {
    this.author = author;
    this.contents = contents;
    this.replyInformation = "";
    editStatus = editable;
  }

  public Message(String author, String contents, String replyInformation) {
    this.author = author;
    this.contents = contents;
    this.replyInformation = replyInformation;
    editStatus = true;
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

  public boolean getEditStatus() {
    return editStatus;
  }

  public void setContents(String newContents) {
    contents = newContents;
  }
}

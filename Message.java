import java.io.Serializable;

public class Message implements Serializable {
  private String author;
  private String contents;
  private String replyInformation;

  public Message(String author, String contents) {
    this.author = author;
    this.contents = contents;
    this.replyInformation = "";
  }

  public Message(String author, String contents, String replyInformation) {
    this.author = author;
    this.contents = contents;
    this.replyInformation = replyInformation;
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

  public void setContents(String newContents){
  	contents = newContents;
  }
}

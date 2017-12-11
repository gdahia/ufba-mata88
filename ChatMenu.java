import java.util.Scanner;

public class ChatMenu {
  public ChatMenu(ChatHandler chatHand) {
    Scanner inputReader = new Scanner(System.in);

    // print menu options
    System.out.println(
        "1 - Send message\n2 - See message log\n3 - Add new user\n4 - Change topic\n5 - Display members\n6 - Quit");

    // menu loop
    boolean quit = false;
    while (!quit && inputReader.hasNext()) {
      // handle user chosen menu option
      int opt = inputReader.nextInt();
      switch (opt) {
        case 1:
          chatHand.sendMessage();
          break;
        case 2:
          chatHand.fetchMessages();
          break;
        case 3:
          chatHand.addUser();
          break;
        case 4:
          chatHand.changeTopic();
          break;
        case 5:
          chatHand.displayMembers();
          break;
        case 6:
          quit = true;
          break;
        default:
          System.out.println("Unrecognized option");
          break;
      }

      // only reprint menu if not quitted
      if (!quit)
        System.out.println(
            "1 - Send message\n2 - See message log\n3 - Add new user\n4 - Change topic\n5 - Display members\n6 - Quit");
    }
  }
}

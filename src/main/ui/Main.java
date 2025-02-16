package ui;

import java.util.*;
import ui.Clue;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner ui = new Scanner(System.in);

        System.out.println("Welcome to Clue!");
        System.out.println();
        System.out.print("Enter number of players: ");
        int numPlayers = ui.nextInt();
        while (numPlayers < 3 | numPlayers > 6) {
            System.out.println("There must be 3 to 6 players!");
            System.out.print("Enter number of players: ");
            numPlayers = ui.nextInt();
        }
        System.out.println();
        Clue c = new Clue(numPlayers);
    }
}

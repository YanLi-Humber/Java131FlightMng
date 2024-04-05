import java.util.Scanner;

public class Seat {
    private static boolean[] seats = new boolean[20];

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Seat Reserving Page");
        
        while (true) {
            
            System.out.println("1. Reserve Seats");
            System.out.println("2. View all Seats");
            System.out.println("3. Exit");

            int choice = scan.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("How many tickets do you want to book?");
                    int tickets = scan.nextInt();
                    reserveSeats(scan, tickets);
                    break;
                case 2:
                    viewSeats();
                    break;
                case 3:
                    scan.close();
                    return; 
                default:
                    System.out.println("Please try again.");
            }
        }
    }

    private static void reserveSeats(Scanner scan, int numberOfTickets) {
        if (numberOfTickets <= availableSeats()) {
            for (int i = 0; i < numberOfTickets; i++) {
                System.out.print("Enter seat number for ticket " + (i + 1) + ": ");
                int seatNum = scan.nextInt();
                if (!seats[seatNum - 1]) {
                    seats[seatNum - 1] = true;
                    System.out.println("Seat " + seatNum + " reserved.");
                } else {
                    System.out.println("Seat " + seatNum + " is already reserved. Please choose a different seat."); 
                }
            }
            System.out.println("All requested seats have been reserved. Thank you.");
        } else {
            System.out.println("Sorry, there are not enough available seats for your request.");
        }
    }

    private static int availableSeats() {
        int count = 0;
        for (boolean seat : seats) {
            if (!seat) count++;
        }
        return count;
    }

    private static void viewSeats() {
        for (int i = 0; i < seats.length; i++) {
            System.out.print("Seat " + (i + 1) + ": ");
            if (seats[i]) {
                System.out.println("Reserved");
            } else {
                System.out.println("Available");
            }
        }
    }

}

    


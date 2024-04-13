import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Flight {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FlightCart cart = new FlightCart();

        while (true) {
            System.out.println("Do you want to search for a flight? (yes/no/manage):");
            String initialInput = scanner.nextLine().trim();

            if (initialInput.equalsIgnoreCase("no")) {
                System.out.println("Goodbye!");
                break;
            } else if (initialInput.equalsIgnoreCase("manage")) {
                manageCart(scanner, cart);
                continue;
            }

            System.out.println("Enter departure airport code:");
            String departureId = scanner.nextLine().toUpperCase();
            System.out.println("Enter arrival airport code:");
            String arrivalId = scanner.nextLine().toUpperCase();
            System.out.println("Enter outbound date (YYYY-MM-DD):");
            String outboundDate = scanner.nextLine();

            System.out.println("Is this a round trip? (yes/no):");
            boolean isRoundTrip = scanner.nextLine().trim().equalsIgnoreCase("yes");
            String returnDate = "";
            String type = isRoundTrip ? "1" : "2";

            if (isRoundTrip) {
                System.out.println("Enter return date (YYYY-MM-DD):");
                returnDate = scanner.nextLine();
            }

            String apiKey = "79de6f5bc9efe6f045adce292ef1cba73cef1a5e29baccb3a9cf361740202d79";
            String requestURL = "https://serpapi.com/search.json?engine=google_flights" +
                                "&departure_id=" + departureId +
                                "&arrival_id=" + arrivalId +
                                "&outbound_date=" + outboundDate +
                                "&type=" + type +
                                (isRoundTrip ? "&return_date=" + returnDate : "") +
                                "&api_key=" + apiKey;

            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(requestURL))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Gson gson = new Gson();
                FlightResponse flightResponse = gson.fromJson(response.body(), FlightResponse.class);

                if (flightResponse.bestFlights != null && !flightResponse.bestFlights.isEmpty()) {
                    List<FlightDetail> flights = flightResponse.bestFlights.get(0).flights;

                    for (int i = 0; i < Math.min(3, flights.size()); i++) {
                        FlightDetail flight = flights.get(i);
                        System.out.println((i + 1) + ". Flight Number: " + flight.flightNumber +
                                           ", Departure: " + flight.departureAirport.name +
                                           " at " + flight.departureAirport.time +
                                           ", Arrival: " + flight.arrivalAirport.name +
                                           " at " + flight.arrivalAirport.time +
                                           ", Airline: " + flight.airline +
                                           ", Class: " + flight.travelClass +
                                           ", Price: $" + flightResponse.bestFlights.get(0).price);
                    }

                    System.out.println("Enter the number of the flight to add to your cart (1, 2, or 3), or type 'manage' to edit/view cart:");
                    String input = scanner.nextLine().trim();

                    if (input.equalsIgnoreCase("manage")) {
                        manageCart(scanner, cart);
                    } else {
                        int choice = Integer.parseInt(input) - 1;
                        if (choice >= 0 && choice < flights.size()) {
                            FlightDetail selectedFlight = flights.get(choice);
                            System.out.println("Select a seat number (e.g., 12A):");
                            String seatNumber = scanner.nextLine();
                            selectedFlight.setSeatNumber(seatNumber);
                            cart.addFlight(selectedFlight);
                            System.out.println("Added to your cart: Flight Number: " +
                                               selectedFlight.flightNumber + " from " +
                                               selectedFlight.departureAirport.name + " to " +
                                               selectedFlight.arrivalAirport.name);
                        } else {
                            System.out.println("Invalid selection.");
                        }
                    }
                } else {
                    System.out.println("No flights available.");
                }
            } catch (Exception e) {
                System.out.println("Error fetching flight information: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static void manageCart(Scanner scanner, FlightCart cart) {
        boolean inCartManagement = true;
        while (inCartManagement) {
            System.out.println("Type 'view' to see your cart, 'remove' to remove a flight, or 'checkout' to checkout:");
            String action = scanner.nextLine().trim();
            switch (action.toLowerCase()) {
                case "view":
                    cart.displayCart();
                    break;
                case "remove":
                    if (cart.flights.isEmpty()) {
                        System.out.println("Your cart is empty.");
                    } else {
                        System.out.println("Enter the flight number to remove:");
                        String flightNum = scanner.nextLine().trim();
                        cart.removeFlight(flightNum);
                    }
                    break;
                case "checkout":
                    cart.checkout(scanner);
                    inCartManagement = false;  // Assume checkout completes the cart management
                    break;
                default:
                    System.out.println("Returning to main menu.");
                    inCartManagement = false;
                    break;
            }
        }
    }

    static class FlightResponse {
        @SerializedName("best_flights")
        List<BestFlight> bestFlights;
    }

    static class BestFlight {
        List<FlightDetail> flights;
        double price;
    }

    static class FlightDetail {
        @SerializedName("departure_airport")
        Airport departureAirport;
        @SerializedName("arrival_airport")
        Airport arrivalAirport;
        @SerializedName("flight_number")
        String flightNumber;
        String airline;
        @SerializedName("travel_class")
        String travelClass;
        String seatNumber;

        public void setSeatNumber(String seatNumber) {
            this.seatNumber = seatNumber;
        }
    }

    static class Airport {
        String name;
        String time;
    }

    static class FlightCart {
        private final List<FlightDetail> flights = new ArrayList<>();

        public void addFlight(FlightDetail flight) {
            flights.add(flight);
        }

        public void removeFlight(String flightNumber) {
            flights.removeIf(flight -> flight.flightNumber.equals(flightNumber));
            System.out.println("Flight " + flightNumber + " has been removed from your cart.");
        }

        public void displayCart() {
            if (flights.isEmpty()) {
                System.out.println("Your cart is empty.");
                return;
            }
            System.out.println("Your Cart:");
            for (FlightDetail flight : flights) {
                System.out.println("Flight Number: " + flight.flightNumber + " from " + flight.departureAirport.name +
                                   " to " + flight.arrivalAirport.name + " - Seat: " + flight.seatNumber);
            }
        }

        public void checkout(Scanner scanner) {
            if (flights.isEmpty()) {
                System.out.println("Your cart is empty. No checkout needed.");
                return;
            }
            System.out.println("Please enter your fake bank account number:");
            String bankAccount = scanner.nextLine();
            System.out.println("Please enter your fake bank routing number:");
            String routingNumber = scanner.nextLine();
            System.out.println("Processing your checkout...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Checkout complete! Bank Account: " + bankAccount + ", Routing Number: " + routingNumber);
        }
    }
}

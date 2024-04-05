import java.lang.reflect.Array;
import java.util.ArrayList;

public class Username extends Flights {
    private String email;
    private String username;
    private String password;
    private ArrayList<String> flightNumber = new ArrayList<String>();

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // flights = [{flightNumber:"123", adult:2,children:0}] add this in the array
    public void addFlight(String flightNumber, int adult, int children) {
        Flights flight = new Flights();
        flight.setFlightNumber(flightNumber);
        flight.setAdult(adult);
        flight.setChildren(children);
    }

    public String getEmail() {
        return this.email;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public ArrayList<String> getFlights() {

    }
}
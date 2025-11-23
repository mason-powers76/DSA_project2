/**
 * Main application entry point. Delegates control to the Driver class
 * to ensure minimization of code in main (OO Requirement).
 */
public class FlightPlannerMain {

    public static void main(String[] args) {
        // Instantiate the controller and run the application logic
        Driver flightDriver = new Driver();
        flightDriver.run();
    }
}
package fooddiary;

public class Main {
    public static void main(String[] args) {
        ApiHttpRequests apiHttpRequests = new ApiHttpRequests();
        apiHttpRequests.findFood("Sausage", 100, 170);
        apiHttpRequests.findBasicFood("carrot", 100);
    }
}
package fooddiary;

import fooddiary.usda.api.ApiHttpRequests;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ApiHttpRequests apiHttpRequests = new ApiHttpRequests();
        apiHttpRequests.findFood("Sausage", 100, 170);
        apiHttpRequests.findBasicFood("carrot", 100);
        apiHttpRequests.findHomeFood("dsfsd", 100);
        CloudConnect cloudConnect = new CloudConnect();
        cloudConnect.launchConnect();
        // store eaten food in db
        // method calculating calories and PFC in percents per day
    }
}
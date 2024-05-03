package app.entities;

public class Status {

    int statusId;

    String name;


    public Status(int statusId, String name) {
        this.statusId = statusId;
        this.name = name;
    }

    public int getStatusId() {
        return statusId;
    }

    public String getName() {
        return name;
    }
}

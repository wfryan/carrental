package traits;

public enum ContractStatus {
    UPCOMING("UPCOMING"),
    ACTIVE("ACTIVE"),
    LATE("LATE"),
    RETURNED("RETURNED"),
    DELAYED("DELAYED"),
    CANCELED("CANCELED");

    private final String status;

    private ContractStatus(String status){this.status = status;}

    @Override
    public String toString(){return this.status;}
}

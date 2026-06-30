package traits;

public enum CarColor {
    RED("RED"),
    BLUE("BLUE"),
    ORANGE("ORANGE"),
    YELLOW("YELLOW"),
    PURPLE("PURPLE"),
    BLACK("BLACK"),
    WHITE("WHITE"),
    SILVER("SILVER"),
    GREY("GREY");

    private final String name;

    private CarColor(String name){
        this.name = name;
    }

    @Override
    public String toString() {return this.name;}
}

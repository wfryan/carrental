package traits;

public enum CarMake {
    VOLKSWAGEN("VOLKSWAGEN"),
    TOYOTA("TOYOTA"),
    HONDA("HONDA"),
    FORD("FORD"),
    CHEVY("CHEVY"),
    JEEP("JEEP")
    ;

    private final String name;

    private CarMake(String name){
        this.name = name;
    }

    @Override
    public String toString() {return this.name;}
}

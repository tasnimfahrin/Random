public class Student {
    private int id;
    private String name;
    private Gender gender;

    public Student(int id, String name, Gender gender) {
        this.id = id;
        this.name = name;
        this.gender = gender;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Gender getGender() { return gender; }
}
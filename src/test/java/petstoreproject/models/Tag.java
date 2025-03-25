package petstoreproject.models;

public class Tag {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format(
                """
                     Category{
                       id= %d,
                       name= '%s'
                }""",
                id, name
        );
    }
}

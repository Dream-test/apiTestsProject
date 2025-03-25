package petstoreproject.models;

import java.util.List;

public class StorePet {

    private long id;
    private Category category;
    private String name;
    private List<String> photoUrls;
    private List<Tag> tags;
    private String status;

    public StorePet() {};

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format(
                """
                StorePet{
                  id = %d,
                  category = %s,
                  name = '%s',
                  photoUrls = %s,
                  tags = %s,
                  status = '%s'
                '}""",
                id,
                category != null ? category.toString() : "null",
                name,
                photoUrls != null ? photoUrls.toString() : "null",
                tags != null ? tags.toString() : "null",
                status
        );
    }
}


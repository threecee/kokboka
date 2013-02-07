package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "my_tag")
public class Tag extends Model implements Comparable<Tag> {

    public String name;
    public String nameHash;

    private Tag(String name) {
        this.name = name;
        this.nameHash = hashName(name);
    }

    public static String hashName(String name) {
        return name.replaceAll(" ", "_");
    }

    public String toString() {
        return nameHash;
    }

    public int compareTo(Tag otherTag) {
        return nameHash.compareTo(otherTag.nameHash);
    }

    public int compareTo(String otherTag) {
        return nameHash.compareTo(otherTag);
    }

    public static Tag findOrCreateByName(String name) {
        Tag tag = Tag.find("nameHash = ?", hashName(name)).first();
        if (tag == null) {
            tag = new Tag(name);
        }
        return tag;
    }

    public static Collection<Tag> findTagsFromRecipes(List<Recipe> recipes) {
        HashMap<String, Tag> stringTagHashMap = new HashMap<String, Tag>();

        for (Recipe recipe : recipes) {
            for (Tag tag : recipe.tags) {
                stringTagHashMap.put(tag.nameHash, tag);
            }
        }

        return stringTagHashMap.values();
    }


    public static List<Map> getCloud() {
        List<Map> result = Tag.find(
                "select new map(t.nameHash as tag, count(t.id) as pound) " +
                        "from Recipe p join p.tags as t group by t.nameHash order by t.nameHash"
        ).fetch();
        return result;
    }


    public static String tagsAsCommaSeparatedList() {
        List<Tag> tags = Tag.find("order by nameHash desc").fetch();

        String result = "[";
        for (Tag tag : tags) {
            result += "\"" + tag.name + "\", ";
        }
        if (tags.size() > 0) {
            result = result.substring(0, result.length() - 2);
        }

        result += "]";

        return result;
    }

}
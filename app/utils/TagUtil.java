package utils;


import models.Tag;

import java.util.ArrayList;
import java.util.List;

public class TagUtil {
    public static String[] removeTagAndCreateArray(List<Tag> selectedTags, String tag) {
        if (selectedTags == null) {
            selectedTags = new ArrayList<Tag>();
        }

        List<Tag> newTagList = new ArrayList<Tag>();

        for (Tag tagObject : selectedTags) {
            if (tagObject.nameHash.compareTo(tag) != 0) {
                newTagList.add(tagObject);
            }
        }


        return createArray(newTagList);
    }

    public static String[] mergeAndCreateArray(List<Tag> selectedTags, String tag) {
        if (selectedTags == null) {
            selectedTags = new ArrayList<Tag>();
        }

        List<Tag> newTagList = new ArrayList<Tag>();
        newTagList.addAll(selectedTags);

        if (tag != null) {
            Tag newTag = Tag.find("nameHash = ?", tag).first();
            if (newTag != null) {
                newTagList.add(newTag);
            }
        }

        return createArray(newTagList);
    }

    public static String[] createArray(List<Tag> selectedTags) {
        if (selectedTags == null) {
            selectedTags = new ArrayList<Tag>();
        }

        String[] tagArray = new String[selectedTags.size()];
        for (int i = 0; i < selectedTags.size(); i++) {
            tagArray[i] = selectedTags.get(i).nameHash;
        }

        return tagArray;
    }
}

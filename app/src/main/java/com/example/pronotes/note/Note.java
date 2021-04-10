package com.example.pronotes.note;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Note{
        private String title,content;
        ArrayList<String> listAllTitle;

        public Note(){}

    public ArrayList<String> getListAllTitle() {
        return listAllTitle;
    }

    public void setListAllTitle(ArrayList<String> listAllTitle) {
        this.listAllTitle = listAllTitle;
    }

    public Note(ArrayList<String> listAllTitle) {
        this.listAllTitle = new ArrayList<String>(Arrays.asList(title));
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content) {
            this.content = content;
    }
}

package com.qsmp;

import java.util.Objects;

public class Scan_Beacon_Bean {

    String Title_Name;
    String Media_Text;
    String Media_Url;
    String media_redirection_url;


    public String getTitle_Name() {
        return Title_Name;
    }

    public void setTitle_Name(String title_Name) {
        Title_Name = title_Name;
    }

    public String getMedia_Text() {
        return Media_Text;
    }

    public void setMedia_Text(String media_Text) {
        Media_Text = media_Text;
    }

    public String getMedia_Url() {
        return Media_Url;
    }

    public void setMedia_Url(String media_Url) {
        Media_Url = media_Url;
    }

    public String getMedia_redirection_url() {
        return media_redirection_url;
    }

    public void setMedia_redirection_url(String media_redirection_url) {
        this.media_redirection_url = media_redirection_url;
    }

    public Scan_Beacon_Bean()
    {

    }

    public Scan_Beacon_Bean(String Title_Name, String Media_Text, String Media_Url,String media_redirection_url)
    {
        this.Title_Name = Title_Name;
        this.Media_Text = Media_Text;
        this.Media_Url = Media_Url;
        this.media_redirection_url = media_redirection_url;

    }

}

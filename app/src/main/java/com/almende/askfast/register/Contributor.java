package com.almende.askfast.register;

/**
 * Created by Freeware Sys on 8/20/2016.
 */
public class Contributor {

    String login;
    String html_url;

    @Override
    public String toString() {
        return login + " (" + html_url + ")";
    }
}

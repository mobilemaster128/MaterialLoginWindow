package com.almende.askfast.register;

/**
 * Created by Freeware Sys on 8/20/2016.
 */
public class Response {
    String login;
    String name;

    @Override
    public String toString() {
        return(login + name);
    }
}

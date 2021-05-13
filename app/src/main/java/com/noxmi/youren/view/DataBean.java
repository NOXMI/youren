package com.noxmi.youren.view;

import android.provider.ContactsContract;

import java.security.PublicKey;

public class DataBean {
    public String Autor;
    public String Content;
    public DataBean(String Autor,String Content){
        this.Autor=Autor;
        this.Content=Content;
    }
    public String getAutor()
    {
        return this.Autor;
    }
    public String getContent(){
        return this.Content;
    }
}

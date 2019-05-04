package com.example.scanasyoushop;

// REFRENCE ------------> https://www.simplifiedcoding.net/android-mysql-tutorial-to-perform-basic-crud-operation/

public class Api {
    private static final String ROOT_URL = "http://192.168.0.36:8080/scanasyoushop/v1/api.php?apicall=";


    public static final String URL_CREATE_USER = ROOT_URL + "createuser";
    public static final String URL_SELECT_USER = ROOT_URL + "selectuser";
    public static final String URL_SELECT_ITEM = ROOT_URL + "selectitem";

}



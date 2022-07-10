package com.example.buunytoys.model;


import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class ModelUser extends SugarRecord {

    @Unique
    public String customer_id = "";

    public String first_name = "";
    public String last_name = "";
    public String email = "";
    public String password = "";
    public String confirm_password = "";
    public String profile_photo = "";




}

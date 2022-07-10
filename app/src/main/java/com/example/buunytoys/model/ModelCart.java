package com.example.buunytoys.model;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class ModelCart extends SugarRecord {

    @Unique
    public String product_id;

    public String product_name;
    public String product_image;
    public int quantity = 1;
    public int product_price = 0;
}

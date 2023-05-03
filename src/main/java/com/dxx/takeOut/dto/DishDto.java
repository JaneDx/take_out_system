package com.dxx.takeOut.dto;


import com.dxx.takeOut.entity.Dish;
import com.dxx.takeOut.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}

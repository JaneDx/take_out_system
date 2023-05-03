package com.dxx.takeOut.dto;

import com.dxx.takeOut.entity.Setmeal;
import com.dxx.takeOut.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}

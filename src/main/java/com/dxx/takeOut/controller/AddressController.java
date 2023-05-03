package com.dxx.takeOut.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.dxx.takeOut.common.BaseContext;
import com.dxx.takeOut.common.R;
import com.dxx.takeOut.entity.AddressBook;
import com.dxx.takeOut.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressController {

    @Autowired
    private AddressBookService addressBookService;
    /**
     * 新增
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());  //threadlocal获得当前用户id
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/default")
    public R<String> isdefault(@RequestBody AddressBook addressBook){
        //只能有1个默认地址，所以应该先把地址都设置为0，在把当前地址设置为1
        Long currentId = BaseContext.getCurrentId();
        LambdaUpdateWrapper<AddressBook> queryWrapper=new LambdaUpdateWrapper<>();  //注意这里是LambdaUpdateWrapper
        queryWrapper.eq(AddressBook::getUserId,currentId);
        queryWrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(queryWrapper);

        AddressBook address = addressBookService.getById(addressBook.getId());
        if(address!=null){
            address.setIsDefault(1);
            addressBookService.updateById(address);
            return R.success("设置默认地址成功");
        }

        return R.error("设置默认地址失败");
    }
    /**
     * 获取默认地址
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,currentId);
        queryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        return R.success(addressBook);
    }
    /**
     * 获取当前所有地址list
     */
    @GetMapping("/list")
    public R<List<AddressBook>> get(){
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,currentId);
        List<AddressBook> list = addressBookService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 编辑地址
     */
    @GetMapping("/{userId}")
    public R getById(@PathVariable Long userId){
        AddressBook addressBook = addressBookService.getById(userId);
        if(addressBook!=null){
            return R.success(addressBook);
        }
        return R.error("没找到此对象");
    }

    /**
     * 修改地址
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){

        if(addressBookService.updateById(addressBook)){
            return R.success("修改成功");
        }
        return R.error("修改失败");
    }

    /**
     * 删除地址
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        LambdaQueryWrapper<AddressBook> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getId,ids);
        addressBookService.remove(queryWrapper);
        return R.success("删除成功");
    }
}

package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {

    @Resource
    AddressBookService addressBookService;

    /**
     * 57.新增地址
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        //addressBook.saveUserId(BaseContext.getCurrent());
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 58.设置默认地址（通过更改default）
     */
    @PutMapping("default")
    public R<AddressBook>setDefault (@RequestBody AddressBook addressBook) {
        log.info("addressBook:(}", addressBook);
        LambdaUpdateWrapper<AddressBook> wrapper= new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrent());
        wrapper.set(AddressBook::getIsDefault, 0);//只把这个用户的地址都设置成0
        addressBookService.update(wrapper);
//SQL:update address book set is_default 0 where user id =BaseContext.getCurrent()

        addressBook.setIsDefault(1);//再单独把当前用户的地址改为默认地址
        addressBookService.updateById(addressBook);
//SQL:update address book set is default 1 where id =addressBook.id
        return R.success(addressBook);
    }
}

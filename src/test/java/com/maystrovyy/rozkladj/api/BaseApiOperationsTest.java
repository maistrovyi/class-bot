package com.maystrovyy.rozkladj.api;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(value = SpringRunner.class)
public class BaseApiOperationsTest {

    @Resource
    ScheduleApiOperations baseApiOperations;

    @Test
    public void isValidGroupName_Success() {
        Assert.assertTrue(baseApiOperations.isValidGroupName("vv-41"));
        Assert.assertTrue(baseApiOperations.isValidGroupName("VV-41"));

        Assert.assertTrue(baseApiOperations.isValidGroupName(" V V - 4 1"));

        Assert.assertFalse(baseApiOperations.isValidGroupName("VV-412"));
        Assert.assertFalse(baseApiOperations.isValidGroupName("-4@12"));
        Assert.assertFalse(baseApiOperations.isValidGroupName("12"));
        Assert.assertFalse(baseApiOperations.isValidGroupName("12345"));
        Assert.assertFalse(baseApiOperations.isValidGroupName("VVVVV"));
        Assert.assertFalse(baseApiOperations.isValidGroupName("VV"));

        Assert.assertFalse(baseApiOperations.isValidGroupName(null));
        Assert.assertTrue(baseApiOperations.isValidGroupName(" ВВ - 4 1"));
        Assert.assertTrue(baseApiOperations.isValidGroupName("ЇЯ-41"));
    }

}
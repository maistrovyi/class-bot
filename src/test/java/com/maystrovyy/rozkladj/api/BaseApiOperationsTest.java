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
    GroupApiOperations groupApiOperations;

    @Test
    public void isValidGroupName_Success() {
        Assert.assertTrue(groupApiOperations.isValidGroupName("vv-41"));
        Assert.assertTrue(groupApiOperations.isValidGroupName("VV-41"));

        Assert.assertTrue(groupApiOperations.isValidGroupName(" V V - 4 1"));

        Assert.assertFalse(groupApiOperations.isValidGroupName("VV-412"));
        Assert.assertFalse(groupApiOperations.isValidGroupName("-4@12"));
        Assert.assertFalse(groupApiOperations.isValidGroupName("12"));
        Assert.assertFalse(groupApiOperations.isValidGroupName("12345"));
        Assert.assertFalse(groupApiOperations.isValidGroupName("VVVVV"));
        Assert.assertFalse(groupApiOperations.isValidGroupName("VV"));

        Assert.assertFalse(groupApiOperations.isValidGroupName(null));
        Assert.assertTrue(groupApiOperations.isValidGroupName(" ВВ - 4 1"));
        Assert.assertTrue(groupApiOperations.isValidGroupName("ЇЯ-41"));
    }

}
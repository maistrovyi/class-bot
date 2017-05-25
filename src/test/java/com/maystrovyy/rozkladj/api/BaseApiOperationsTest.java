package com.maystrovyy.rozkladj.api;

import com.maystrovyy.configs.ApplicationConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.NotNull;

@SpringBootTest
@RunWith(value = SpringRunner.class)
public class BaseApiOperationsTest {

    public boolean isValidGroupName(@NotNull String groupName) {
        if (groupName.length() != 5) {
            groupName = groupName.replaceAll("\\s+","");
            if (groupName.length() != 5) {
                return false;
            }
        }
        return groupName.matches(ApplicationConstants.GROUP_NAME_REGEXP);
    }

    @Test
    public void isValidGroupName_Success() {
        Assert.assertTrue(isValidGroupName("vv-41"));
        Assert.assertTrue(isValidGroupName("VV-41"));

        Assert.assertTrue(isValidGroupName(" V V - 4 1"));

        Assert.assertFalse(isValidGroupName("VV-412"));
        Assert.assertFalse(isValidGroupName("-4@12"));
        Assert.assertFalse(isValidGroupName("12"));
//        Assert.assertFalse(isValidGroupName("12345"));
//        Assert.assertFalse(isValidGroupName("VVVVV"));
        Assert.assertFalse(isValidGroupName("VV"));

//        Assert.assertTrue(isValidGroupName(null));
//        Assert.assertTrue(isValidGroupName(" ВВ - 4 1"));
//        Assert.assertTrue(isValidGroupName("ЇЯ-41"));
    }

}
package com.c88.common.web.valid;

import com.c88.common.web.annotation.Mobile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;

import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MobileValidatorTest {
    public void setMobile(@Mobile String mobile){
        // to do
    }
    private static ExecutableValidator executableValidator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        executableValidator = factory.getValidator().forExecutables();
    }

    @Test
    public void manufacturerIsNull() throws NoSuchMethodException {
        MobileValidatorTest mobileTest = new MobileValidatorTest();

        Method method = MobileValidatorTest.class.getMethod("setMobile", String.class);
        Object[] parameterValues = {"abc"};
        Set<ConstraintViolation<MobileValidatorTest>> violations = executableValidator.validateParameters(
                mobileTest, method, parameterValues);

        violations.forEach(violation -> System.out.println(violation.getMessage()));
    }

}
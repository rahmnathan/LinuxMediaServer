package Phone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Nathan on 7/23/2016.
 */
public class PhoneTest {

    Phone myPhone;

    @Before
    public void initialize(){
        myPhone = new Phone("TestCastIP", "TestPhoneIP", "TestPhoneName", "TestPath");
    }

    @Test
    public void testPhoneConstructor(){
        Assert.assertEquals("TestCastIP", myPhone.getCastIP());
        Assert.assertEquals("TestPhoneIP", myPhone.getPhoneIP());
        Assert.assertEquals("TestPhoneName", myPhone.getPhoneName());
        Assert.assertEquals("TestPath", myPhone.getMainPath());
    }
}
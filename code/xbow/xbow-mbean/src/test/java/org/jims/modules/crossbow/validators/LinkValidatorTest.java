package org.jims.modules.crossbow.validators;

import org.jims.modules.crossbow.link.validators.LinkValidator;
import org.jims.modules.crossbow.link.validators.RegexLinkValidator;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for RegexLinkValidator class
 *
 * @author robert boczek
 */
public class LinkValidatorTest {

    private LinkValidator linkValidator = new RegexLinkValidator();

    @Test
    public void testIncorrectIpFormat(){

        assertFalse(linkValidator.isIpAddressValid("129.244.2.256"));
    }

    @Test
    public void testIncorrectIpFormat2(){

        assertFalse(linkValidator.isIpAddressValid("12.244.1202.2"));
    }

    @Test
    public void testIncorrectIpFormat3(){

        assertFalse(linkValidator.isIpAddressValid("198.23.4.f"));
    }

    @Test
    public void testCorrectIpFormat(){

        assertTrue(linkValidator.isIpAddressValid("255.255.255.255"));
    }

    @Test
    public void testCorrectIpForma2t(){

        assertTrue(linkValidator.isIpAddressValid("0.0.0.0"));
    }

    @Test
    public void testCorrectIpFormat3(){

        assertTrue(linkValidator.isIpAddressValid("126.34.67.45"));
    }

}

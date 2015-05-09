package com.weijde.idea.rascal;

import junit.framework.TestCase;

public class RascalUtilTest extends TestCase
{
    public void testCamelCase()
    {
        assertCamelCase("camelCase", "Camel Case");
        assertCamelCase("camelCase", "Camel case");
        assertCamelCase("camelCase", " Camel Case");
        assertCamelCase("camelCase", " CamelCase");
        assertCamelCase("camelCase", " camelCase");
        assertCamelCase("camelCase", "camel Case");
    }

    private void assertCamelCase(String expectedOutput, String input)
    {
        assertEquals(expectedOutput, RascalUtil.getCamelCase(input));
    }
}
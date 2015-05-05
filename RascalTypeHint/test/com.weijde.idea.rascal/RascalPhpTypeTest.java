package com.weijde.idea.rascal;

import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import junit.framework.TestCase;

public class RascalPhpTypeTest extends TestCase
{
    private RascalPhpType object;

    @Override
    public void setUp() throws Exception
    {
        object = new RascalPhpType(new PhpType());
    }

    public void testPhpToRascal() throws Exception
    {
        assertType(getArrayType(RascalPhpType.RASCAL_TYPE_ANY), "\\array");
        assertType(getArrayType(RascalPhpType.RASCAL_TYPE_ANY), "array");

        // simple array types
        assertType(getArrayType(RascalPhpType.RASCAL_TYPE_STRING), "\\string[]");
        assertType(getArrayType(RascalPhpType.RASCAL_TYPE_STRING), "string[]");
        assertType(getArrayType(RascalPhpType.RASCAL_TYPE_INT), "\\int[]");
        assertType(getArrayType(RascalPhpType.RASCAL_TYPE_INT), "int[]");

        assertType(String.format(RascalPhpType.RASCAL_TYPE_CLASS, "|php+object:///classinstance|"), "ClassInstance");
        assertType(String.format(RascalPhpType.RASCAL_TYPE_CLASS, "|php+object:///some/randomobject|"), "Some\\RandomObject");

        assertType(RascalPhpType.RASCAL_TYPE_BOOL, "bool");
        assertType(RascalPhpType.RASCAL_TYPE_BOOL, "\\bool");
        assertType(RascalPhpType.RASCAL_TYPE_BOOL, "boolean");
        assertType(RascalPhpType.RASCAL_TYPE_BOOL, "\\boolean");

        assertType(RascalPhpType.RASCAL_TYPE_CALLABLE, "callable");
        assertType(RascalPhpType.RASCAL_TYPE_CALLABLE, "\\callable");

        assertType(RascalPhpType.RASCAL_TYPE_FLOAT, "float");
        assertType(RascalPhpType.RASCAL_TYPE_FLOAT, "\\float");

        assertType(RascalPhpType.RASCAL_TYPE_INT, "int");
        assertType(RascalPhpType.RASCAL_TYPE_INT, "\\int");
        assertType(RascalPhpType.RASCAL_TYPE_INT, "integer");
        assertType(RascalPhpType.RASCAL_TYPE_INT, "\\integer");


        assertType(RascalPhpType.RASCAL_TYPE_NUMBER, "number");
        assertType(RascalPhpType.RASCAL_TYPE_NUMBER, "\\number");

        assertType(RascalPhpType.RASCAL_TYPE_NULL, "\\null");
        assertType(RascalPhpType.RASCAL_TYPE_NULL, "null");

        assertType(RascalPhpType.RASCAL_TYPE_OBJECT, "\\object");
        assertType(RascalPhpType.RASCAL_TYPE_OBJECT, "object");
        assertType(RascalPhpType.RASCAL_TYPE_OBJECT, "\\stdClass");
        assertType(RascalPhpType.RASCAL_TYPE_OBJECT, "stdClass");

        assertType(RascalPhpType.RASCAL_TYPE_RESOURCE, "\\resource");
        assertType(RascalPhpType.RASCAL_TYPE_RESOURCE, "resource");

        assertType(RascalPhpType.RASCAL_TYPE_SCALAR, "\\scalar");
        assertType(RascalPhpType.RASCAL_TYPE_SCALAR, "scalar");
        assertType(RascalPhpType.RASCAL_TYPE_SCALAR, "Scalar");

        assertType(RascalPhpType.RASCAL_TYPE_STRING, "\\string");
        assertType(RascalPhpType.RASCAL_TYPE_STRING, "string");
        assertType(RascalPhpType.RASCAL_TYPE_STRING, "String");


    }

    private String getArrayType(String arrayType) {
        return String.format(RascalPhpType.RASCAL_TYPE_ARRAY, arrayType);
    }

    private void assertType(String expectedOutput, String input) throws Exception {
        assertEquals(expectedOutput, object.phpToRascal(input));
    }
}
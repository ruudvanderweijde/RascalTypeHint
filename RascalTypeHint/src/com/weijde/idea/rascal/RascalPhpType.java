package com.weijde.idea.rascal;

import com.intellij.openapi.util.text.StringUtil;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class RascalPhpType extends PhpType
{
    public static String RASCAL_TYPE_ARRAY = "\\arrayType(%s)";
    public static String RASCAL_TYPE_CLASS = "\\classType(%s)";
    public static final String RASCAL_TYPE_ANY = "\\any()";
    public static final String RASCAL_TYPE_BOOL = "\\booleanType()";
    public static final String RASCAL_TYPE_CALLABLE = "\\callableType()";
    public static final String RASCAL_TYPE_FLOAT = "\\floatType()";
    public static final String RASCAL_TYPE_INT = "\\integerType()";
    public static final String RASCAL_TYPE_NUMBER = "\\numberType()";
    public static final String RASCAL_TYPE_NULL = "\\nullType()";
    public static final String RASCAL_TYPE_OBJECT = "\\objectType()";
    public static final String RASCAL_TYPE_RESOURCE = "\\resourceType()";
    public static final String RASCAL_TYPE_SCALAR = "\\scalarType()";
    public static final String RASCAL_TYPE_STRING = "\\stringType()";

    public void setPhpType(PhpType type)
    {
        super.add(type.filterUnknown());
    }

    public RascalPhpType(PhpType type) {
        super.add(type.filterUnknown());
    }

    @Override
    public String toString()
    {
        Set<String> rascalTypes = new HashSet<String>();
        Set<String> phpTypes = getTypes();

        if (phpTypes.isEmpty()) { return RASCAL_TYPE_ANY; }

        for(String type: phpTypes) {
            rascalTypes.add(phpToRascal(type));
        }

        return StringUtil.join(rascalTypes, ",");
    }

    protected String phpToRascal(String type) {
        if (type.matches("^\\\\.*")) { // remove first backslash
            type = type.substring(1, type.length());
        }
        if (type.matches("(?i)^mixed$")) {
            return RASCAL_TYPE_ANY;
        }
        if (type.matches("(?i)^array$")) {
            return String.format(RASCAL_TYPE_ARRAY, RASCAL_TYPE_ANY);
        }
        if (type.matches("(?i)^(bool|boolean|true|false)$")) {
            return RASCAL_TYPE_BOOL;
        }
        if (type.matches("(?i)^(callable|callback)$")) {
            return RASCAL_TYPE_CALLABLE;
        }
        if (type.matches("(?i)^(float|double|real)$")) {
            return RASCAL_TYPE_FLOAT;
        }
        if (type.matches("(?i)^number$")) {
            return RASCAL_TYPE_NUMBER;
        }
        if (type.matches("(?i)^(int|integer)$")) {
            return RASCAL_TYPE_INT;
        }
        if (type.matches("(?i)^(object|stdClass)$")) {
            return RASCAL_TYPE_OBJECT;
        }
        if (type.matches("(?i)^(null|void)$")) {
            return RASCAL_TYPE_NULL;
        }
        if (type.matches("(?i)^resource$")) {
            return RASCAL_TYPE_RESOURCE;
        }
        if (type.matches("(?i)^scalar$")) {
            return RASCAL_TYPE_SCALAR;
        }
        if (type.matches("(?i)^string$")) {
            return RASCAL_TYPE_STRING;
        }
        if (isArrayType(type)) {
            return String.format(RASCAL_TYPE_ARRAY, phpToRascal(getArrayType(type)));
        }

        // the rest should be a class
        String objectName = String.format("|php+object:///%s|", RascalUtil.replaceSlashes(type));
        return String.format(RASCAL_TYPE_CLASS, objectName);
    }

    @NotNull
    private String getArrayType(String str) {
        return str.substring(0, str.lastIndexOf('['));
    }

    private boolean isArrayType(String str) {
        return str.matches("^(.*)\\[\\]$");
    }
}

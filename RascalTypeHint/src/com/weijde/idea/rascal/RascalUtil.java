package com.weijde.idea.rascal;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.*;
import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.lang.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.Introspector;
import java.util.List;

public class RascalUtil
{
    /**
     * Convert and print the by phpstorm inferred types into rascal types
     *
     * @param element a typeable php element
     * @return String
     */
    static public String printRascalTypes(PhpTypedElement element)
    {
        return (new RascalPhpType(element.getType())).toString();
    }

    /**
     * Get the location in rascal format, for more info see link below
     * @link http://tutor.rascal-mpl.org/Rascal/Expressions/Values/Location/Location.html
     *
     * @param element a typeable php element
     * @return String
     */
    static public String getRascalLocation(PhpTypedElement element)
    {
        String format = "|php+%s://%s|";

        if (element instanceof PhpNamespace) {
            return null; // do not print namespaces, because they will always resolve to any
            //return String.format(format, "namespace", getFormattedNamespaceName(((PhpNamespace) element).getFQN()));
        }

        if (element instanceof PhpClass) {
            PhpClass clazz = (PhpClass) element;
            String type = "class";
            if (clazz.isTrait()) { type = "trait"; }
            if (clazz.isInterface()) { type = "interface"; }

            return String.format(format, type, getFullClassName(clazz));
        }

        // class method
        if (element instanceof Method) {
            return String.format(format, "method", getFullMethodName((Method) element));
        }
        // normal functions
        if (element instanceof Function) {
            return String.format(format, "function", getFullFunctionName((Function) element));
        }

        // class field
        if (element instanceof Field) {
            String fqn = ((Field) element).getFQN();
            if (fqn == null) {
                return null;
            }
            List<String> names = StringUtil.split(fqn, ".");

            return String.format(format, "classConstant", getFormattedNamespaceName(names.get(0)) + "/" + names.get(1));
        }
        if (element instanceof Constant) {
            String fqn = ((Constant) element).getFQN();
            String namespaceName = getNormalizedFQN(fqn);
            if (namespaceName == null) return null;
            if (!namespaceName.isEmpty()) {
                namespaceName = "/" + namespaceName;
            }

            return String.format(format, "constant", namespaceName + "/" + ((Constant) element).getName());
        }
        // variables
        if (element instanceof Variable) {
            Variable variable = (Variable) element;

            Function function = PhpPsiUtil.getParentByCondition(variable.getParent(), true, Function.INSTANCEOF, Method.INSTANCEOF);
            if (function != null) {
                return String.format(format, "functionVar", getFullFunctionName(function) + "/" + variable.getName());
            }

            Method method = PhpPsiUtil.getParentByCondition(variable.getParent(), true, Method.INSTANCEOF);
            if (method != null) {
                return String.format(format, "methodVar", getFullMethodName(method) + "/" + variable.getName());
            }
            String namespaceName = getFormattedNamespaceName((variable).getNamespaceName());

            return String.format(format, "globalVar", namespaceName + variable.getName());
        }

        if (element instanceof Parameter) {
            Parameter param = (Parameter) element;
            PsiElement parent = ((Parameter) element).getParent().getParent();
            if (parent instanceof Method) {
                return String.format(format, "methodParam", getFullMethodName((Method) parent) + "/" + param.getName());
            }
            if (parent instanceof Function) {
                return String.format(format, "functionParam", getFullFunctionName((Function) parent) + "/" + param.getName());
            }
        }

        return null;
    }

    @NotNull
    private static String getFullMethodName(Method method) {
        PhpClass inClass = method.getContainingClass();

        return getFullClassName(inClass) + "/" + method.getName().toLowerCase();
    }

    private static String getFullFunctionName(Function function) {
        String namespaceName = function.getNamespaceName();
        String functionName = function.isClosure() ? "__closure" : function.getName();

        return getFormattedNamespaceName(namespaceName) + functionName.toLowerCase();
    }

    @Nullable
    protected static String getCamelCase(String input)
    {
        return Introspector.decapitalize(WordUtils.capitalize(input).replaceAll("\\s", ""));
    }

    @NotNull
    private static String getFullClassName(PhpClass element) {
        String namespaceName = element.getNamespaceName();
        String className = element.getName();

        return getFormattedNamespaceName(namespaceName) + className.toLowerCase();
    }

    @Nullable
    private static String getNormalizedFQN(String fqn) {
        if (fqn == null) {
            return null;
        }
        List<String> names = StringUtil.split(fqn, "\\");
        names.remove(names.size() - 1); // remove last item
        return StringUtil.join(names, "/").toLowerCase();
    }

    @NotNull
    public static String getFormattedNamespaceName(String namespaceName) {
        return namespaceName.replace("\\", "/").toLowerCase();
    }
}

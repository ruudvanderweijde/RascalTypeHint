package com.weijde.idea.rascal;

import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.*;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            //return String.format(format, "namespace", replaceSlashes(((PhpNamespace) element).getFQN()));
        }

        if (element instanceof PhpClass) {
            String type = "class";
            if (((PhpClass) element).isTrait()) { type = "trait"; }
            if (((PhpClass) element).isInterface()) { type = "interface"; }

            return String.format(format, type, getFullClassName((PhpClass) element));
        }

        // class method
        if (element instanceof Method) {
            PhpClass inClass = ((Method) element).getContainingClass();

            return String.format(format, "method", getFullClassName(inClass) + "/" + ((Method) element).getName());
        }
        // normal functions
        if (element instanceof Function) {
            String namespaceName = ((Function) element).getNamespaceName();

            return String.format(format, "function", replaceSlashes(namespaceName) + ((Function) element).getName());
        }

        // class field
        if (element instanceof Field) {
            String fqn = ((Field) element).getFQN();
            if (fqn == null) {
                return null;
            }
            List<String> names = StringUtil.split(fqn, ".");

            return String.format(format, "classConstant", replaceSlashes(names.get(0)) + "/" + names.get(1));
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
            String fqn = ((Variable) element).getFQN();

            Function function = PhpPsiUtil.getParentByCondition(((Variable) element).getParent(), true, Function.INSTANCEOF, Method.INSTANCEOF);
            if (function != null) {
                String namespaceName = getNormalizedFQN(fqn);
                if (namespaceName == null) return null;

                return String.format(format, "functionVar", namespaceName + "/" + function.getName() + "/" + ((Variable) element).getName());
            }

            Method method = PhpPsiUtil.getParentByCondition(((Variable) element).getParent(), true, Method.INSTANCEOF);
            if (method != null) {
                String namespaceName = getNormalizedFQN(fqn);
                if (namespaceName == null) return null;

                return String.format(format, "methodVar", namespaceName + "/" + method.getName() + "/" + ((Variable) element).getName());
            }
            String namespaceName = replaceSlashes(((Variable) element).getNamespaceName());

            return String.format(format, "globalVar", namespaceName + ((Variable) element).getName());
        }

        return null;
    }

    @NotNull
    private static String getFullClassName(PhpClass element) {
        String namespaceName = element.getNamespaceName();
        String className = element.getName();

        return replaceSlashes(namespaceName) + className.toLowerCase();
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
    public static String replaceSlashes(String namespaceName) {
        return namespaceName.replace("\\", "/").toLowerCase();
    }
}

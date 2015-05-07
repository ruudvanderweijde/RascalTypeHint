package com.weijde.idea.rascal;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;

public class TypeReporterInspection extends LocalInspectionTool {

    public static final ProblemHighlightType PROBLEM_HIGHLIGHT_TYPE = ProblemHighlightType.INFORMATION;

    @Override
    public boolean runForWholeFile() {
        return true;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(final @NotNull ProblemsHolder holder, boolean isOnTheFly)
    {
        PsiFile psiFile = holder.getFile();

        if(psiFile instanceof PhpFile) {
            psiFile.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
                @Override
                public void visitElement(PsiElement element) {
                    if (element instanceof PhpTypedElement) {
                        String info = getReportInfo((PhpTypedElement) element);
                        if (info != null) {
                            holder.registerProblem(element, info, PROBLEM_HIGHLIGHT_TYPE);
                        }
                    }
                    super.visitElement(element);
                }
            });
        }

        return super.buildVisitor(holder, isOnTheFly);
    }

    private String getReportInfo(PhpTypedElement element)
    {
        String rascalLoction = RascalUtil.getRascalLocation(element);
        if (rascalLoction == null) {
            return null; // returns null on unsupported or untyped elements
        }

        String typeString = RascalUtil.printRascalTypes(element);

        return String.format("<%s:{%s}>", rascalLoction, typeString);
    }
}

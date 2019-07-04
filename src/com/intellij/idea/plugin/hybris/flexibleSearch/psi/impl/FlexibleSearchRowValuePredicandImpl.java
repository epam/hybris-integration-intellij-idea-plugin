// This is a generated file. Not intended for manual editing.
package com.intellij.idea.plugin.hybris.flexibleSearch.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.intellij.idea.plugin.hybris.flexibleSearch.psi.FlexibleSearchTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.idea.plugin.hybris.flexibleSearch.psi.*;

public class FlexibleSearchRowValuePredicandImpl extends ASTWrapperPsiElement implements FlexibleSearchRowValuePredicand {

  public FlexibleSearchRowValuePredicandImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull FlexibleSearchVisitor visitor) {
    visitor.visitRowValuePredicand(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof FlexibleSearchVisitor) accept((FlexibleSearchVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<FlexibleSearchCommonValueExpression> getCommonValueExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, FlexibleSearchCommonValueExpression.class);
  }

  @Override
  @Nullable
  public FlexibleSearchValueExpression getValueExpression() {
    return findChildByClass(FlexibleSearchValueExpression.class);
  }

}

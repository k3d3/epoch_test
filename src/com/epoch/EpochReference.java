package com.epoch;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.xml.XmlTagImpl;
import com.intellij.psi.impl.source.xml.XmlTokenImpl;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class EpochReference extends PsiReferenceBase<XmlTokenImpl> {
    private String myProcName;
    private XmlTokenImpl myElement1;
    private XmlTagImpl myTarget;

    public EpochReference(XmlTokenImpl element, TextRange rangeInElement, String procName, XmlTagImpl target) {
        super(element, rangeInElement, false);
        myProcName = procName;
        myElement1 = element;
        myTarget = target;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return myTarget;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }
}
